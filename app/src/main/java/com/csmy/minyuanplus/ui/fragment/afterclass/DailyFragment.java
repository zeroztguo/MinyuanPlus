package com.csmy.minyuanplus.ui.fragment.afterclass;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatTextView;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.afterclass.Dailies;
import com.csmy.minyuanplus.model.afterclass.Daily;
import com.csmy.minyuanplus.model.afterclass.Stories;
import com.csmy.minyuanplus.support.API;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.support.util.Util;
import com.csmy.minyuanplus.ui.activity.DailyActivity;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Zero on 16/7/23.
 */
public class DailyFragment extends AfterClassSwipeRereshFragment<Daily> {

    public static DailyFragment newInstance() {
        return new DailyFragment();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {

    }

    @Override
    protected ItemViewDelegate<Daily> getFirstItemViewDelegate() {
        ItemViewDelegate<Daily> firstItemViewDelegate = new ItemViewDelegate<Daily>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_daily_text_image;
            }

            @Override
            public boolean isForViewType(Daily item, int position) {
                return !Util.isStringNull(item.getImage());
            }

            @Override
            public void convert(ViewHolder holder, Daily daily, int position) {
                /*
                设置标题
                 */
                AppCompatTextView textView = holder.getView(R.id.id_daily_title);
                textView.setText(daily.getTitle());

                /*
                设置小图片
                 */
                SimpleDraweeView draweeView = holder.getView(R.id.id_daily_image);
                Uri uri = Uri.parse(daily.getImage());
                draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
                draweeView.setImageURI(uri);
            }
        };
        return firstItemViewDelegate;
    }

    @Override
    protected ItemViewDelegate<Daily> getSecondItemViewDelegate() {
        ItemViewDelegate<Daily> secondItemViewDelegate = new ItemViewDelegate<Daily>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_daily_text;
            }

            @Override
            public boolean isForViewType(Daily item, int position) {
                return Util.isStringNull(item.getImage());
            }

            @Override
            public void convert(ViewHolder holder, Daily daily, int position) {
                /*
                设置标题
                 */
                AppCompatTextView textView = holder.getView(R.id.id_daily_text_title);
                AutoUtils.autoTextSize(textView);
                textView.setText(daily.getTitle());
            }
        };
        return secondItemViewDelegate;
    }

    private void obtainDailyList() {

        Logger.d("请求ing...");
        OkHttpUtils
                .get()
                .url(API.ZHIHU_DAILY_LIST)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.show(getString(R.string.zhihu_daily_load_fail));
                        setRefresh();
                    }

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Dailies dailies = gson.fromJson(response, Dailies.class);
                        List<Stories> storiesList = dailies.getStories();

                        List<Daily> dailyList = new ArrayList<>();
                        for (Stories stories : storiesList) {
                            Daily daily = new Daily();
                            daily.setImage(stories.getImages().get(0))
                                    .setId(stories.getId())
                                    .setId_str(stories.getId() + "")
                                    .setTitle(stories.getTitle())
                                    .setType(stories.getType())
                                    .setGa_prefix(stories.getGa_prefix());
                            dailyList.add(daily);
                        }


                        DataSupport.deleteAll(Daily.class);
                        DataSupport.saveAll(dailyList);


                        Logger.d("日报有：" + dailyList.size());
//
                        addAllData(dailyList);
                        setRefresh();
                    }
                });
    }


    @Override
    protected void loadFromDatabase() {
        List<Daily> dailyList = DataSupport.findAll(Daily.class);
        addAllData(dailyList);
    }


    @Override
    protected void setOnItemClick(Daily daily) {
        Intent intent = new Intent(getHoldingActivity(), DailyActivity.class);
        intent.putExtra("daily", daily.getId_str());
        startActivity(intent);
    }

    @Override
    protected void refresh() {
        setRefresh();
        obtainDailyList();
    }


}
