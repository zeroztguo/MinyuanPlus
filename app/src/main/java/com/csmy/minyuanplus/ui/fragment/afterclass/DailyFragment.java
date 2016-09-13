package com.csmy.minyuanplus.ui.fragment.afterclass;

import android.content.Intent;
import android.net.Uri;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.afterclass.Dailies;
import com.csmy.minyuanplus.model.afterclass.Daily;
import com.csmy.minyuanplus.model.afterclass.Stories;
import com.csmy.minyuanplus.support.API;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.ui.activity.DailyActivity;
import com.csmy.minyuanplus.ui.fragment.SwipeRereshFragment;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;

/**
 * 知乎日报列表
 * Created by Zero on 16/7/23.
 */
public class DailyFragment extends SwipeRereshFragment<Daily> {

    private Calendar mCalendar;

    private boolean isFirstLoadMore = true;

    public static DailyFragment newInstance() {
        return new DailyFragment();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {

    }


    /**
     * 获取当天知乎日报
     */
    private void obtainDailyList() {

        OkHttpUtils
                .get()
                .url(API.ZHIHU_DAILY_LATEST_LIST)
                .tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showShort(getContext(), getString(R.string.zhihu_daily_load_fail));
                        OkHttpUtils.getInstance().cancelTag(this);
                        setRefresh(false);
                    }

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Dailies dailies = gson.fromJson(response, Dailies.class);
                        List<Stories> storiesList = dailies.getStories();

                        mCalendar = Calendar.getInstance();
                        int year = Integer.valueOf(dailies.getDate().substring(0, 4));
                        int month = Integer.valueOf(dailies.getDate().substring(4, 6));
                        int day = Integer.valueOf(dailies.getDate().substring(6, 8));

                        mCalendar.set(Calendar.YEAR, year);
                        mCalendar.set(Calendar.MONTH, month - 1);
                        mCalendar.set(Calendar.DAY_OF_MONTH, day);

                        String date = year + "-" + month + "-" + day;

                        List<Daily> dailyList = new ArrayList<>();
                        for (Stories stories : storiesList) {
                            Daily daily = new Daily();
                            daily.setImage(stories.getImages().get(0))
                                    .setId(stories.getId())
                                    .setId_str(stories.getId() + "")
                                    .setTitle(stories.getTitle())
                                    .setType(stories.getType())
                                    .setGa_prefix(stories.getGa_prefix())
                                    .setDate(date);
                            dailyList.add(daily);
                        }


                        DataSupport.deleteAll(Daily.class);
                        DataSupport.saveAll(dailyList);


                        Logger.d("日报有：" + dailyList.size());

                        addAllData(dailyList);
                        setRefresh(false);
                        setLoadMore();
                    }
                });
    }

    /**
     * 获取以前的知乎日报
     */
    private void obtainBeforeDailyList() {
        String month;
        String day;
        month = mCalendar.get(Calendar.MONTH) < 9
                ? 0 + "" + (mCalendar.get(Calendar.MONTH) + 1)
                : "" + (mCalendar.get(Calendar.MONTH) + 1);

        day = mCalendar.get(Calendar.DAY_OF_MONTH) < 10
                ? 0 + "" + mCalendar.get(Calendar.DAY_OF_MONTH)
                : "" + mCalendar.get(Calendar.DAY_OF_MONTH);
        String url = API.ZHIHU_DAILY_BEFORE_LIST + mCalendar.get(Calendar.YEAR) + month + day;

        OkHttpUtils
                .get()
                .url(url)
                .tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        setRefresh(false);
                    }

                    @Override
                    public void onResponse(String response) {

                        Gson gson = new Gson();
                        Dailies dailies = gson.fromJson(response, Dailies.class);
                        List<Stories> storiesList = dailies.getStories();

                        int year = Integer.valueOf(dailies.getDate().substring(0, 4));
                        int month = Integer.valueOf(dailies.getDate().substring(4, 6));
                        int day = Integer.valueOf(dailies.getDate().substring(6, 8));

                        String date = year + "-" + month + "-" + day;

                        List<Daily> dailyList = new ArrayList<>();
                        for (Stories stories : storiesList) {
                            Daily daily = new Daily();
                            daily.setImage(stories.getImages().get(0))
                                    .setId(stories.getId())
                                    .setId_str(stories.getId() + "")
                                    .setTitle(stories.getTitle())
                                    .setType(stories.getType())
                                    .setGa_prefix(stories.getGa_prefix())
                                    .setDate(date);
                            dailyList.add(daily);
                        }


                        DataSupport.saveAll(dailyList);


                        Logger.d("日报有：" + dailyList.size());

                        updateAllData(dailyList);

                    }
                });
    }


    @Override
    protected int getItemId() {
        return R.layout.item_after_class;
    }

    @Override
    protected void loadFromDatabase() {
        List<Daily> dailyList = DataSupport.findAll(Daily.class);
        addAllData(dailyList);
    }


    @Override
    protected void setItem(ViewHolder holder, Daily daily, int position) {
        /*
        设置标题和时间
         */
        holder.setText(R.id.id_after_class_title_tv, daily.getTitle());
        holder.setText(R.id.id_after_class_author_tv, daily.getDate());

        /*
        设置小图片
         */
        SimpleDraweeView draweeView = holder.getView(R.id.id_after_class_image);
        Uri uri = Uri.parse(daily.getImage());
        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        draweeView.setImageURI(uri);
    }


    @Override
    protected void setOnItemClick(Daily daily) {
        Intent intent = new Intent(getHoldingActivity(), DailyActivity.class);
        intent.putExtra(DailyFragment.class.getSimpleName(), daily.getId_str());
        startActivity(intent);
    }

    @Override
    protected void refresh() {
        setRefresh(true);
        obtainDailyList();
    }

    @Override
    protected void loadMore() {
        /*
        第一次加载更多不更改查询的时间，因为查询过往知乎日报会显示前一天的
         */
        if (isFirstLoadMore) {
            isFirstLoadMore = false;
        } else {
            mCalendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        obtainBeforeDailyList();
    }


}
