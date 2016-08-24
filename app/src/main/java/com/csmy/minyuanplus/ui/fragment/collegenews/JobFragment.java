package com.csmy.minyuanplus.ui.fragment.collegenews;

import android.content.Intent;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.collegenews.Job;
import com.csmy.minyuanplus.model.collegenews.NewsBean;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.ui.activity.MyNewsActivity;
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
//    @Bind(R.id.id_job_rv)
//    RecyclerView mJobRecyclerView;
//    @Bind(R.id.id_job_srl)
//    SwipeRefreshLayout mSwipeRefreshLayout;

    private int mPage;

    public static JobFragment newInstance() {
        return new JobFragment();
    }


    public static final String COLLEGE_NEWS = "http://web.csmzxy.com/netCourse/readData";
    private static final String SHARE_URL = "http://www.csmzxy.com/xb/jiuye.html?content,";



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {

    }

    private void obtainJobList() {
        Logger.d("请求ing...");
        OkHttpUtils
                .get()
                .url(COLLEGE_NEWS)
                .addParams("cmd", "7")
                .addParams("v1", "32908")
                .addParams("v2", "1")
                .addParams("v3", "15")
                .addParams("tempData", new Date().toString())
                .tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.show("获取院部动态失败" + e.getMessage());
                        OkHttpUtils.getInstance().cancelTag(this);
                        setRefresh();
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
                        setRefresh();
                        setLoadMore();
                        mPage = 1;
                    }
                });
    }

    private void loadMoreJobList(int page) {
        Logger.d("加载更多ing...");
        OkHttpUtils
                .get()
                .url(COLLEGE_NEWS)
                .addParams("cmd", "7")
                .addParams("v1", "32908")
                .addParams("v2", page + "")
                .addParams("v3", "15")
                .addParams("tempData", new Date().toString())
                .tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.show("获取院部动态失败" + e.getMessage());
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
        Intent intent = new Intent(getHoldingActivity(), MyNewsActivity.class);
        NewsBean newsBean = new NewsBean();
        newsBean.setContentTitle(job.getContentTitle())
                .setContentAuthor(job.getContentAuthor())
                .setContentID(job.getContentID())
                .setSubmitTime(job.getSubmitTime())
                .setShareUrl(SHARE_URL+job.getContentID());

        intent.putExtra("college_news", newsBean);
        startActivity(intent);
    }

    @Override
    protected void refresh() {
        setRefresh();
        mPage = 1;
        obtainJobList();
    }

    @Override
    protected void loadMore() {
        mPage++;
        loadMoreJobList(mPage);
    }

//    @Override
//    protected void initView(View view, Bundle saveInstanceState) {
//        init();
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.fragment_job;
//    }
}
