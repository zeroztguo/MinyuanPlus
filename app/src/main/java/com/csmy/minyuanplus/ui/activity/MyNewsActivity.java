package com.csmy.minyuanplus.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.model.collegenews.NewsBean;
import com.csmy.minyuanplus.model.collegenews.NewsDetail;
import com.csmy.minyuanplus.support.util.Util;
import com.csmy.minyuanplus.ui.BaseView;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.orhanobut.logger.Logger;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * 解析html文本将内容显示在RecyclerView中的校闻页面
 */
public class MyNewsActivity extends BaseActivity implements BaseView {
    @Bind(R.id.id_news_content_toolbar)
    Toolbar mToolbar;
    /**
     * 显示新闻标题
     */
    @Bind(R.id.id_news_content_title_actv)
    AppCompatTextView mNewsTitleTextView;

    /**
     * 显示新闻作者和日期
     */
    @Bind(R.id.id_news_content_author_time__actv)
    AppCompatTextView mNewsAuthorTimeTextView;

    /**
     * 显示新闻内容
     */
    @Bind(R.id.id_news_content)
    CoordinatorLayout mNewsContentLayout;
    @Bind(R.id.id_news_content_progress_view)
    CircularProgressView mProgressView;
    @Bind(R.id.id_news_rv)
    RecyclerView mRecyclerView;

    LinearLayoutManager mLinearLayoutManager;
    MultiItemTypeAdapter<NewsDetail> mAdapter;
    ItemViewDelegate<NewsDetail> mTextDelegate;
    ItemViewDelegate<NewsDetail> mImageDelegate;


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

        mDatas = new ArrayList<>();

        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mTextDelegate = new ItemViewDelegate<NewsDetail>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_news_content_text;
            }

            @Override
            public boolean isForViewType(NewsDetail item, int position) {
                return Util.isStringNull(item.getImg()) && !Util.isStringNull(item.getText());
            }

            @Override
            public void convert(ViewHolder holder, NewsDetail newsDetail, int position) {
                AppCompatTextView textView = holder.getView(R.id.id_news_text);
                textView.setText(newsDetail.getText());
            }
        };

        mImageDelegate = new ItemViewDelegate<NewsDetail>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_news_content_image;
            }

            @Override
            public boolean isForViewType(NewsDetail item, int position) {
                return !Util.isStringNull(item.getImg()) && Util.isStringNull(item.getText());
            }

            @Override
            public void convert(ViewHolder holder, NewsDetail newsDetail, int position) {
                SimpleDraweeView draweeView = holder.getView(R.id.id_news_image);
                Uri uri = Uri.parse(newsDetail.getImg());
                draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
                draweeView.setImageURI(uri);
            }
        };

        mAdapter = new NewsAdapter(this, mDatas);
        mRecyclerView.setAdapter(mAdapter);
    }

    private String getShareMessage() {
        return "【" + mTitle + "\n" + mAuthor + "】：" + mShareUrl + "(分享自民院+)";
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_news;
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
                        parseCollegeNewsContent(response);
                        hideProgress();
                        mAdapter.notifyDataSetChanged();
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
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, getShareMessage());
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)));
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

    /**
     * 解析新闻内容
     *
     * @param response
     */
    private void parseCollegeNewsContent(String response) {
        Document document = Jsoup.parse(response);
        Elements es = document.select("p");
        for (Element e : es) {
            Elements imgs = e.select("img");
            if (imgs.size() > 0) {
                for (Element img : imgs) {
                    //图片地址
                    String imgUrl = img.attr("src");
                    if (!Util.isStringNull(imgUrl)) {
                        NewsDetail newsDetail = new NewsDetail();
                        newsDetail.setImg(imgUrl);
                        newsDetail.setText("");
                        mDatas.add(newsDetail);
                    }
                }
            } else {
                String contentText = e.text();
                if (!Util.isStringNull(contentText)) {
                    NewsDetail newsDetail = new NewsDetail();
                    newsDetail.setText(contentText);
                    newsDetail.setImg("");
                    mDatas.add(newsDetail);
                }
            }
        }
    }


    private class NewsAdapter extends MultiItemTypeAdapter<NewsDetail> {
        public NewsAdapter(Context context, List<NewsDetail> datas) {
            super(context, datas);
            addItemViewDelegate(mTextDelegate);
            addItemViewDelegate(mImageDelegate);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
            AutoUtils.auto(viewHolder.getConvertView());
            return viewHolder;
        }
    }

}
