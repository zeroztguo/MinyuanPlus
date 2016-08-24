package com.csmy.minyuanplus.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.apptopus.progressive.Progressive;
import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.ui.BaseToolbarView;
import com.csmy.minyuanplus.ui.BaseView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zzhoujay.richtext.RichText;

import butterknife.Bind;
import okhttp3.Call;

public class AppIntroduceActivity extends BaseActivity implements BaseToolbarView, BaseView {
    @Bind(R.id.id_base_tool_bar)
    Toolbar mToolbar;
    @Bind(R.id.id_introduce_tv)
    AppCompatTextView mContentTextView;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_app_introduce;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initToolbar();
        showProgress();
        obtainAppIntroduceMessage();
    }

    private void obtainAppIntroduceMessage() {
        OkHttpUtils
                .get()
                .url("https://coding.net/u/zeroztguo/p/CollegePlus/git/raw/master/appIntroduce.txt")
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


    @Override
    public void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("应用简介");
    }

    @Override
    public void showProgress() {
        Progressive.showProgress(mContentTextView);
    }

    @Override
    public void hideProgress() {
        Progressive.hideProgress(mContentTextView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
