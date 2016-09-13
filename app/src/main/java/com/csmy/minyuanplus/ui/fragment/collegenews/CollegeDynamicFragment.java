package com.csmy.minyuanplus.ui.fragment.collegenews;

import android.content.Intent;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.collegenews.CollegeDynamic;
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
 * 院部动态页面
 * Created by Zero on 16/7/23.
 */
public class CollegeDynamicFragment extends SwipeRereshFragment<CollegeDynamic> {


    private int mPage;

    public static CollegeDynamicFragment newInstance() {
        return new CollegeDynamicFragment();
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {

    }

    private void obtainCollegeDynamicList() {
        Logger.d("请求ing...");
        OkHttpUtils
                .get()
                .url(API.COLLEGE_NEWS)
                .addParams(CollegeNewsHelper.CMD, CollegeNewsHelper.CMD_VALUE)
                .addParams(CollegeNewsHelper.V_ONE, CollegeNewsHelper.V_ONE_COLLEGE_DYNALIC_VALUE)
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
                        Type it = new TypeToken<List<CollegeDynamic>>() {
                        }.getType();
                        List<CollegeDynamic> collegeDynamicList = gson.fromJson(response, it);

                        DataSupport.deleteAll(CollegeDynamic.class);
                        DataSupport.saveAll(collegeDynamicList);

                        addAllData(collegeDynamicList);
                        Logger.d("一共有这么多条：" + collegeDynamicList.size());
                        setRefresh(false);
                        setLoadMore();
                        mPage = 1;
                    }
                });
    }

    private void loadMoreCollegeDynamicList(String page) {
        Logger.d("加载更多ing...");
        OkHttpUtils
                .get()
                .url(API.COLLEGE_NEWS)
                .addParams(CollegeNewsHelper.CMD, CollegeNewsHelper.CMD_VALUE)
                .addParams(CollegeNewsHelper.V_ONE, CollegeNewsHelper.V_ONE_COLLEGE_DYNALIC_VALUE)
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
                        Type it = new TypeToken<List<CollegeDynamic>>() {
                        }.getType();
                        List<CollegeDynamic> collegeDynamicList = gson.fromJson(response, it);

                        DataSupport.saveAll(collegeDynamicList);

                        updateAllData(collegeDynamicList);
                        Logger.d("一共有这么多条：" + collegeDynamicList.size());
                    }
                });
    }



    @Override
    protected int getItemId() {
        return R.layout.item_college_news;
    }

    @Override
    protected void loadFromDatabase() {
        List<CollegeDynamic> collegeDynamicList = DataSupport.findAll(CollegeDynamic.class);
        addAllData(collegeDynamicList);
    }

    @Override
    protected void setItem(ViewHolder holder, CollegeDynamic cd, int position) {
        holder.setText(R.id.id_college_news_title_actv, cd.getContentTitle());
        holder.setText(R.id.id_college_news_author_actv, cd.getContentAuthor());
        holder.setText(R.id.id_college_news_date_actv, cd.getSubmitTime());
        holder.setVisible(R.id.id_college_news_head_layout, true);
        holder.setText(R.id.id_college_news_head_tv, cd.getContentAuthor().substring(0, 1));
    }

    @Override
    protected void setOnItemClick(CollegeDynamic cd) {
        Intent intent = new Intent(getHoldingActivity(), CollegeNewsActivity.class);
        NewsBean newsBean = new NewsBean();
        newsBean.setContentTitle(cd.getContentTitle())
                .setContentAuthor(cd.getContentAuthor())
                .setContentID(cd.getContentID())
                .setSubmitTime(cd.getSubmitTime())
                .setShareUrl(CollegeNewsHelper.COLLEGE_DYNAMIC_SHARE_URL + cd.getContentID());

        intent.putExtra(CollegeNewsHelper.COLLEGE_NEWS, newsBean);
        startActivity(intent);
    }

    @Override
    protected void refresh() {
        setRefresh(true);
        mPage = 1;
        obtainCollegeDynamicList();
    }

    @Override
    protected void loadMore() {
        mPage++;
        loadMoreCollegeDynamicList(mPage+"");
    }


}
