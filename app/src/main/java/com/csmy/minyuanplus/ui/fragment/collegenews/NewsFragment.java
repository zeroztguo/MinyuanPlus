package com.csmy.minyuanplus.ui.fragment.collegenews;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.support.adapter.NewsPagerAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;

/**
 * Created by Zero on 16/7/15.
 */
public class NewsFragment extends BaseFragment{
    @Bind(R.id.id_news_tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.id_news_view_pager)
    ViewPager mViewPager;

    private NewsPagerAdapter mNewsPagerAdapter;

    public static NewsFragment newInstance(){return new NewsFragment();}

    @Override
    protected void initView(View view, Bundle saveInstanceState) {
        mNewsPagerAdapter = new NewsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mNewsPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel){

    }
}
