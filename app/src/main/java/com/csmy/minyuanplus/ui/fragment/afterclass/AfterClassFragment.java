package com.csmy.minyuanplus.ui.fragment.afterclass;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.support.adapter.AfterClassPagerAdapter;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;

/**
 * 课外页面
 * Created by Zero on 16/7/15.
 */
public class AfterClassFragment extends BaseFragment {
    @Bind(R.id.id_after_class_tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.id_after_class_view_pager)
    ViewPager mViewPager;

    private AfterClassPagerAdapter mAfterClassagerAdapter;

    public static AfterClassFragment newInstance(){return new AfterClassFragment();}

    @Override
    protected void initView(View view, Bundle saveInstanceState) {
        mAfterClassagerAdapter = new AfterClassPagerAdapter(getContext(),getChildFragmentManager());
        mViewPager.setAdapter(mAfterClassagerAdapter);
        mViewPager.setOffscreenPageLimit(AfterClassPagerAdapter.COUNT);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_after_class;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel){

    }
}
