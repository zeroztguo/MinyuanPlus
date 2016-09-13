package com.csmy.minyuanplus.ui.fragment.collegenews;

import android.content.Intent;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.collegenews.Job;
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
 * Created by Zero on 16/7/23.
 */
public class JobFragment extends SwipeRereshFragment<Job> {

    private int mPage;

    public static JobFragment newInstance() {
        return new JobFragment();
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {

    }

    private void obtainJobList() {
        Logger.d("请求ing...");
        OkHttpUtils
                .get()
                .url(API.COLLEGE_NEWS)
                .addParams(CollegeNewsHelper.CMD, CollegeNewsHelper.CMD_VALUE)
                .addParams(CollegeNewsHelper.V_ONE, CollegeNewsHelper.V_ONE_JOB_VALUE)
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
                        Type it = new TypeToken<List<Job>>() {
                        }.getType();
                        List<Job> JobList = gson.fromJson(response, it);

                        DataSupport.deleteAll(Job.class);
                        DataSupport.saveAll(JobList);

                        addAllData(JobList);
                        Logger.d("一共有这么多条：" + JobList.size());
                        setRefresh(false);
                        setLoadMore();
                        mPage = 1;
                    }
                });
    }

    private void loadMoreJobList(String page) {
        Logger.d("加载更多ing...");
        OkHttpUtils
                .get()
                .url(API.COLLEGE_NEWS)
                .addParams(CollegeNewsHelper.CMD, CollegeNewsHelper.CMD_VALUE)
                .addParams(CollegeNewsHelper.V_ONE, CollegeNewsHelper.V_ONE_HOT_LINE_VALUE)
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
                        Type it = new TypeToken<List<Job>>() {
                        }.getType();
                        List<Job> jobList = gson.fromJson(response, it);

                        DataSupport.saveAll(jobList);

                        updateAllData(jobList);
                        Logger.d("一共有这么多条：" + jobList.size());
                    }
                });
    }

//    @Override
//    protected SwipeRefreshLayout getSwipeRefreshLayout() {
//        return mSwipeRefreshLayout;
//    }
//
//    @Override
//    protected RecyclerView getRecyclerView() {
//        return mJobRecyclerView;
//    }


    @Override
    protected int getItemId() {
        return R.layout.item_college_news;
    }

    @Override
    protected void loadFromDatabase() {
        List<Job> JobList = DataSupport.findAll(Job.class);
        addAllData(JobList);
    }

    @Override
    protected void setItem(ViewHolder holder, Job hl, int position) {
        holder.setText(R.id.id_college_news_title_actv, hl.getContentTitle());
        holder.setText(R.id.id_college_news_author_actv, hl.getContentAuthor());
        holder.setText(R.id.id_college_news_date_actv, hl.getSubmitTime());
    }

    @Override
    protected void setOnItemClick(Job job) {
        Intent intent = new Intent(getHoldingActivity(), CollegeNewsActivity.class);
        NewsBean newsBean = new NewsBean();
        newsBean.setContentTitle(job.getContentTitle())
                .setContentAuthor(job.getContentAuthor())
                .setContentID(job.getContentID())
                .setSubmitTime(job.getSubmitTime())
                .setShareUrl(CollegeNewsHelper.JOB_SHARE_URL + job.getContentID());

        intent.putExtra(CollegeNewsHelper.COLLEGE_NEWS, newsBean);
        startActivity(intent);
    }

    @Override
    protected void refresh() {
        setRefresh(true);
        mPage = 1;
        obtainJobList();
    }

    @Override
    protected void loadMore() {
        mPage++;
        loadMoreJobList(mPage + "");
    }

}
