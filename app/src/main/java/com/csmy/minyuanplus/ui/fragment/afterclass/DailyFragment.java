package com.csmy.minyuanplus.ui.fragment.afterclass;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.AppCompatTextView;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.afterclass.Dailies;
import com.csmy.minyuanplus.model.afterclass.Daily;
import com.csmy.minyuanplus.model.afterclass.Stories;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.ui.activity.DailyActivity;
import com.csmy.minyuanplus.ui.fragment.collegenews.SwipeRereshFragment;
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
import java.util.List;

import okhttp3.Call;

/**
 * Created by Zero on 16/7/23.
 */
public class DailyFragment extends SwipeRereshFragment<Daily> {


    private int mPage;

    public static DailyFragment newInstance() {
        return new DailyFragment();
    }


    public static final String DAILY_LIST = "http://news-at.zhihu.com/api/4/news/latest";


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {

    }

    private void obtainDailyList() {

        Logger.d("请求ing...");
        OkHttpUtils
                .get()
                .url(DAILY_LIST)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.show("获取日报失败" + e.getMessage());
                        setRefresh();
                    }

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Dailies dailies = gson.fromJson(response, Dailies.class);
                        List<Stories> storiesList = dailies.getStories();
//                        List<TopStories> topStoriesList = dailies.getTop_stories();

                        List<Daily> dailyList = new ArrayList<Daily>();
                        for (Stories stories : storiesList) {
                            Daily daily = new Daily();
                            daily.setImage(stories.getImages().get(0))
                            .setId(stories.getId())
                            .setId_str(stories.getId()+"")
                            .setTitle(stories.getTitle())
                            .setType(stories.getType())
                            .setGa_prefix(stories.getGa_prefix());
                            dailyList.add(daily);
                        }
//                        for (TopStories topStories : topStoriesList) {
//                            Daily daily = new Daily();
//                            daily.setImage(topStories.getImage())
//                                    .setId(topStories.getId())
//                                    .setId_str(topStories.getId()+"")
//                                    .setTitle(topStories.getTitle())
//                                    .setType(topStories.getType())
//                                    .setGa_prefix(topStories.getGa_prefix());
//                            dailyList.add(daily);
//                        }


                        DataSupport.deleteAll(Daily.class);
                        DataSupport.saveAll(dailyList);


                        Logger.d("日报有：" + dailyList.size());
//
                        addAllData(dailyList);
                        setRefresh();
                        mPage = 1;
                    }
                });
    }




    @Override
    protected int getItemId() {
        return R.layout.item_daily_text_image;
    }

    @Override
    protected void loadFromDatabase() {
        List<Daily> dailyList = DataSupport.findAll(Daily.class);
        addAllData(dailyList);
    }

    @Override
    protected void setItem(ViewHolder holder, Daily daily, int position) {
        Logger.d(daily.toString());
        AppCompatTextView textView = holder.getView(R.id.id_daily_title);
        textView.setText(daily.getTitle());

        SimpleDraweeView draweeView = holder.getView(R.id.id_daily_image);
        Uri uri = Uri.parse(daily.getImage());
        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        draweeView.setImageURI(uri);
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
        mPage = 1;
        obtainDailyList();
    }

    @Override
    protected void loadMore() {
//        mPage++;
//        loadMoreDailyList(mPage);
    }

}
