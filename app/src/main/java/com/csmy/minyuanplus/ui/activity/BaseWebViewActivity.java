package com.csmy.minyuanplus.ui.activity;

import android.os.Bundle;

import com.csmy.minyuanplus.R;

public class BaseWebViewActivity extends BaseActivity {
    @Override
    protected int getContentViewId() {
        return 0;
    }

    @Override
    protected int getFragmentContentId() {
        return R.layout.activity_weixin;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

}
