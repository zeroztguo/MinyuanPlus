package com.csmy.minyuanplus.ui.fragment.collegenews;

import android.content.Intent;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.collegenews.CollegeNews;
import com.csmy.minyuanplus.model.collegenews.NewsBean;
import com.csmy.minyuanplus.support.API;
import com.csmy.minyuanplus.support.CollegeNewsHelper;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.ui.activity.CollegeNewsActivity;
import com.csmy.minyuanplus.ui.fragment.SwipeRereshFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

/**
 * 学院新闻页面
 * Created by Zero on 16/7/23.
 */
public class CollegeNewsFragment extends SwipeRereshFragment<CollegeNews> {
    private int mPage;

    public static CollegeNewsFragment newInstance() {
        return new CollegeNewsFragment();
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {

    }

    private void obtainCollegeNewsList() {
        Logger.d("请求ing...");
        OkHttpUtils
                .get()
                .url(API.COLLEGE_NEWS)
                .addParams(CollegeNewsHelper.CMD, CollegeNewsHelper.CMD_VALUE)
                .addParams(CollegeNewsHelper.V_ONE, CollegeNewsHelper.V_ONE_COLLEGE_NEWS_VALUE)
                .addParams(CollegeNewsHelper.V_TWO, CollegeNewsHelper.V_TWO_VALUE)
                .addParams(CollegeNewsHelper.V_THREE, CollegeNewsHelper.V_THREE_VALUE)
                .addParams(CollegeNewsHelper.TEMP_DATE, new Date().toString())
                .tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showShort(getContext(), getString(R.string.minyuan_news_load_fail));
                        OkHttpUtils.getInstance().cancelTag(this);
                        setRefresh(false);
                    }

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type it = new TypeToken<List<CollegeNews>>() {
                        }.getType();
                        List<CollegeNews> CollegeNewsList = gson.fromJson(response, it);

                        DataSupport.deleteAll(CollegeNews.class);
                        DataSupport.saveAll(CollegeNewsList);

                        addAllData(CollegeNewsList);
                        Logger.d("一共有这么多条：" + CollegeNewsList.size());
                        setRefresh(false);
                        setLoadMore();
                        mPage = 1;
                    }
                });
    }

    private void loadMoreCollegeNewsList(String page) {
        Logger.d("加载更多ing...");
        OkHttpUtils
                .get()
                .url(API.COLLEGE_NEWS)
                .addParams(CollegeNewsHelper.CMD, CollegeNewsHelper.CMD_VALUE)
                .addParams(CollegeNewsHelper.V_ONE, CollegeNewsHelper.V_ONE_COLLEGE_NEWS_VALUE)
                .addParams(CollegeNewsHelper.V_TWO, page)
                .addParams(CollegeNewsHelper.V_THREE, CollegeNewsHelper.V_THREE_VALUE)
                .addParams(CollegeNewsHelper.TEMP_DATE, new Date().toString())
                .tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showShort(getContext(), getString(R.string.minyuan_news_load_fail));
                        OkHttpUtils.getInstance().cancelTag(this);
                    }

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Type it = new TypeToken<List<CollegeNews>>() {
                        }.getType();
                        List<CollegeNews> CollegeNewsList = gson.fromJson(response, it);

                        DataSupport.saveAll(CollegeNewsList);

                        updateAllData(CollegeNewsList);
                        Logger.d("一共有这么多条：" + CollegeNewsList.size());
                    }
                });
    }


    @Override
    protected int getItemId() {
        return R.layout.item_college_news;
    }

    @Override
    protected void loadFromDatabase() {
        List<CollegeNews> CollegeNewsList = DataSupport.findAll(CollegeNews.class);
        addAllData(CollegeNewsList);
    }

    @Override
    protected void setItem(ViewHolder holder, CollegeNews cd, int position) {
        holder.setText(R.id.id_college_news_title_actv, cd.getContentTitle());
        holder.setText(R.id.id_college_news_author_actv, cd.getContentAuthor());
        holder.setText(R.id.id_college_news_date_actv, cd.getSubmitTime());
        holder.setVisible(R.id.id_college_news_head_layout, true);
        holder.setText(R.id.id_college_news_head_tv, cd.getContentAuthor().substring(0, 1));
    }

    @Override
    protected void setOnItemClick(CollegeNews cn) {
        Intent intent = new Intent(getHoldingActivity(), CollegeNewsActivity.class);
        NewsBean newsBean = new NewsBean();
        newsBean.setContentTitle(cn.getContentTitle())
                .setContentAuthor(cn.getContentAuthor())
                .setContentID(cn.getContentID())
                .setSubmitTime(cn.getSubmitTime())
                .setShareUrl(CollegeNewsHelper.COLLEGE_NEWS_SHARE_URL + cn.getContentID());

        intent.putExtra(CollegeNewsHelper.COLLEGE_NEWS, newsBean);
        startActivity(intent);
    }

    @Override
    protected void refresh() {
        setRefresh(true);
        mPage = 1;
        obtainCollegeNewsList();
    }

    @Override
    protected void loadMore() {
        mPage++;
        loadMoreCollegeNewsList(mPage+"");
    }


}
