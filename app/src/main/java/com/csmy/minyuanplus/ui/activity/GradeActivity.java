package com.csmy.minyuanplus.ui.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.support.adapter.GradePagerAdapter;
import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.BindColor;

/**
 * 成绩页面
 */
public class GradeActivity extends BaseActivity {
    @Bind(R.id.id_grade_toolbar)
    Toolbar mGradeToolbar;
    @Bind(R.id.id_grade_tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.id_grade_view_pager)
    ViewPager mViewPager;
    @BindColor(R.color.colorPrimary)
    int colorPrimary;

    private GradePagerAdapter mGradePagerAdapter;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_grade;
    }

    @Override
    protected int getFragmentContentId() {
        return R.id.id_grade_view_pager;
    }

    private void initToolbar() {
        setSupportActionBar(mGradeToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void initView(Bundle savedInstanceState) {

        initToolbar();

        mGradePagerAdapter = new GradePagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setOffscreenPageLimit(GradePagerAdapter.COUNT);
        mViewPager.setAdapter(mGradePagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.d("成绩页面的onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d("成绩页面的onDestroy()");
    }


}
