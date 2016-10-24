package com.csmy.minyuanplus.ui.fragment.college;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.support.adapter.CollegePagerAdapter;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;

/**
 * 民院页面
 * Created by Zero on 16/8/20.
 */
public class CollegeFragment extends BaseFragment {
    @Bind(R.id.id_college_tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.id_college_view_pager)
    ViewPager mViewPager;

    private CollegePagerAdapter mCollegeagerAdapter;


    public static CollegeFragment newInstance(){return new CollegeFragment();}

    @Override
    protected void initView(View view, Bundle saveInstanceState) {
        mCollegeagerAdapter = new CollegePagerAdapter(getContext(),getChildFragmentManager());
        mViewPager.setAdapter(mCollegeagerAdapter);
        mViewPager.setOffscreenPageLimit(CollegePagerAdapter.COUNT);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_college;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel){

    }
}
