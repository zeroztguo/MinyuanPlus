package com.csmy.minyuanplus.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.support.util.SnackbarUtil;
import com.csmy.minyuanplus.ui.BaseToolbarView;
import com.csmy.minyuanplus.ui.view.PinchImageView;

import butterknife.Bind;

public class MapActivity extends BaseActivity implements BaseToolbarView {
    @Bind(R.id.id_base_tool_bar)
    Toolbar mToolbar;
    @Bind(R.id.id_map_piv)
    PinchImageView mPinchImageView;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_map;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initToolbar();
        SnackbarUtil.showWithNoAction(mPinchImageView, getString(R.string.scale));
    }

    @Override
    public void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.map);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPinchImageView.destroyDrawingCache();
        mPinchImageView = null;
    }
}
