package com.csmy.minyuanplus.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.model.NotifyContent;
import com.csmy.minyuanplus.ui.BaseProgressView;
import com.csmy.minyuanplus.ui.BaseToolbarView;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zzhoujay.richtext.RichText;

import butterknife.Bind;
import okhttp3.Call;

public class NotifyContentActivity extends BaseActivity implements BaseToolbarView, BaseProgressView {
    @Bind(R.id.id_base_tool_bar)
    Toolbar mToolbar;
    @Bind(R.id.id_notify_content_tv)
    AppCompatTextView mContentTextView;
    @Bind(R.id.id_notify_content_wv)
    WebView mWebView;
    @Bind(R.id.id_notify_content_progress_view)
    CircularProgressView mProgressView;

    private int mType;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_notify_content;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        showProgress();
        initToolbar();
        initWebView();
        handleIntent();
    }

    @Override
    public void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            NotifyContent notifyContent = (NotifyContent) intent.getSerializableExtra("notify_content");
            getSupportActionBar().setTitle(notifyContent.getTitle());
            String url = notifyContent.getUrl();
            mType = Integer.valueOf(notifyContent.getType());
            /*
            文字
             */
            if (mType == 0) {
                mContentTextView.setVisibility(View.VISIBLE);
                obtainNotifyContent(url);
            }
            /*
            网页
             */
            else {
                mWebView.setVisibility(View.VISIBLE);
                mWebView.loadUrl(url);
            }
        }
    }


    private void obtainNotifyContent(String url) {
        OkHttpUtils
                .get()
                .url(url)
                .tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        hideProgress();
                    }

                    @Override
                    public void onResponse(String response) {
                        hideProgress();
                        RichText.from(response).into(mContentTextView);
                    }
                });
    }

    private void initWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                hideProgress();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                hideProgress();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
    }

    @Override
    public void showProgress() {
        if (mProgressView != null) {
            mProgressView.setVisibility(View.VISIBLE);
            mProgressView.startAnimation();
        }

    }

    @Override
    public void hideProgress() {
        if (mProgressView != null) {
            mProgressView.stopAnimation();
            mProgressView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }
}
