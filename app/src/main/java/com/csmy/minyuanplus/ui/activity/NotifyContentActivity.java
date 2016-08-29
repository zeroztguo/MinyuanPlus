package com.csmy.minyuanplus.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.model.NotifyContent;
import com.csmy.minyuanplus.ui.BaseToolbarView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zzhoujay.richtext.RichText;

import butterknife.Bind;
import okhttp3.Call;

public class NotifyContentActivity extends BaseActivity implements BaseToolbarView{
    @Bind(R.id.id_base_tool_bar)
    Toolbar mToolbar;
    @Bind(R.id.id_notify_content_tv)
    AppCompatTextView mContentTextView;


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
        initToolbar();
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


    private void handleIntent(){
        Intent intent = getIntent();
        if(intent != null){
            NotifyContent notifyContent = (NotifyContent) intent.getSerializableExtra("notify_content");
            getSupportActionBar().setTitle(notifyContent.getTitle());
            obtainNotifyContent(notifyContent.getUrl());
        }
    }

    private void obtainNotifyContent(String url){
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        RichText.from(response).into(mContentTextView);
                    }
                });
    }


}
