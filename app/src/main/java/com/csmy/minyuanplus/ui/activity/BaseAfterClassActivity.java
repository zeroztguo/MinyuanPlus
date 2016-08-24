package com.csmy.minyuanplus.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.support.SettingConfig;
import com.csmy.minyuanplus.support.util.NetworkType;
import com.csmy.minyuanplus.support.util.Util;
import com.csmy.minyuanplus.ui.BaseView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import butterknife.Bind;


public abstract class BaseAfterClassActivity extends BaseActivity implements BaseView {
    @Bind(R.id.id_after_class_content_layout)
    CoordinatorLayout mContentLayout;
    @Bind(R.id.id_after_class_news_image)
    SimpleDraweeView mSimpleDraweeView;
    @Bind(R.id.id_after_class_news_web_view)
    WebView mWebView;
    @Bind(R.id.id_after_class_news_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.id_after_class_news_title)
    AppCompatTextView mTiTleTextView;
    @Bind(R.id.id_after_class_progress_view)
    CircularProgressView mProgressView;


    @Override
    protected void initView(Bundle savedInstanceState) {
        init();
    }

    @Override
    public void showProgress() {
        mProgressView.startAnimation();
        mProgressView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
//        Progressive.hideProgress(mContentLayout);

        mProgressView.stopAnimation();
        mProgressView.setVisibility(View.GONE);
    }

    private void init() {
        initStatuBar();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initWebView();

        handleIntent(getIntent());
        showProgress();
    }


    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    protected abstract void handleIntent(Intent intent);

    protected abstract void loadData();

    /**
     * 透明状态栏
     */
    private void initStatuBar() {
        //只有5.0以上以上系统才支持
        if(Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }


    private void initWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //加载完成
                hideProgress();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                hideProgress();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                hideProgress();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                hideProgress();
                mWebView.loadUrl(url);
                return false;
            }
        });

        /*
        缓存web页面
         */
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);


        /*
        WIFI下加载图片，移动网络下不加载
         */
        if (Util.getNetWorkType() == NetworkType.NETWORKTYPE_WIFI) {
            mWebView.getSettings().setBlockNetworkImage(false);
        } else if (!SettingConfig.isSaveFlow()) {
            mWebView.getSettings().setBlockNetworkImage(false);
        } else {
            mWebView.getSettings().setBlockNetworkImage(true);
        }
    }


    @Override
    protected int getContentViewId() {

        return R.layout.activity_after_class_news;
    }

    protected abstract String getShareMessage();

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


}
