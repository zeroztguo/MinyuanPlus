package com.csmy.minyuanplus.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.model.collegenews.NewsBean;
import com.csmy.minyuanplus.model.collegenews.NewsDetail;
import com.csmy.minyuanplus.ui.BaseView;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.utils.AutoUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zzhoujay.richtext.RichText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * 使用RichText框架显示在TextView中
 */
public class RichTextNewsActivity extends BaseActivity implements BaseView {
    @Bind(R.id.id_news2_content_toolbar)
    Toolbar mToolbar;
    /**
     * 显示新闻标题
     */
    @Bind(R.id.id_news_content_title_tv)
    AppCompatTextView mNewsTitleTextView;

    /**
     * 显示新闻作者和日期
     */
    @Bind(R.id.id_news_content_tv)
    AppCompatTextView mContentTextView;

    @Bind(R.id.id_news_content_author_time_tv)
    AppCompatTextView mNewsAuthorTimeTextView;


    @Bind(R.id.id_news2_content_progress_view)
    CircularProgressView mProgressView;





    public static final String COLLEGE_NEWS = "http://web.csmzxy.com/netCourse/readData";
    /**
     * 新闻内容数据
     */
    private List<NewsDetail> mDatas;
    private String mTitle;
    private String mAuthor;
    private String mSubmitTime;
    private String mShareUrl;
    private String mArticleID;


    @Override
    protected void initView(Bundle savedInstanceState) {
        init();
        handleIntent(getIntent());
    }

    private void init() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //设置返回按钮颜色
//        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        upArrow.setColorFilter(getResources().getColor(R.color.textOrIcon), PorterDuff.Mode.SRC_ATOP);
//        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        mDatas = new ArrayList<>();


    }

    private String getShareMessage() {
        return "【" + mTitle + "\n" + mAuthor + "】：" + mShareUrl + "(分享至民院+)";
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_rech_text_news;
    }

    @Override
    protected int getFragmentContentId() {
        return R.id.id_news_content;
    }

    private void handleIntent(Intent intent) {
        /**
         * 获取新闻信息
         */
        if (intent != null) {
            NewsBean newsBean = (NewsBean) getIntent().getSerializableExtra("college_news");
            mTitle = newsBean.getContentTitle();
            mAuthor = newsBean.getContentAuthor();
            mSubmitTime = newsBean.getSubmitTime();
            mArticleID = newsBean.getContentID() + "";
            mShareUrl = newsBean.getShareUrl();

            AutoUtils.autoTextSize(mNewsTitleTextView);
            AutoUtils.autoTextSize(mNewsAuthorTimeTextView);
            mNewsTitleTextView.setText(mTitle);
            mNewsAuthorTimeTextView.setText(mAuthor + "     " + mSubmitTime);
            obtainCollegeNews(mArticleID);

        }

    }




    public void showProgress() {
        mProgressView.setVisibility(View.VISIBLE);
        mProgressView.startAnimation();
    }

    public void hideProgress() {
        mProgressView.stopAnimation();
        mProgressView.setVisibility(View.GONE);
    }

    private void obtainCollegeNews(String id) {
        showProgress();

        mDatas.clear();
        OkHttpUtils
                .get()
                .url(COLLEGE_NEWS)
                .addParams("cmd", "9")
                .addParams("v1", id)
                .addParams("tempData", new Date().toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        Logger.d("获取新闻内容失败:" + e.getMessage());
                        hideProgress();
                    }

                    @Override
                    public void onResponse(String response) {
                        hideProgress();
                        RichText.from(response)
                                .autoFix(true)
                                .async(true)
                                .error(R.mipmap.failure)
                                .into(mContentTextView);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.id_menu_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, getShareMessage());
                startActivity(Intent.createChooser(shareIntent, "分享至"));
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }





}
