package com.csmy.minyuanplus.support.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.ui.fragment.collegenews.CollegeBoothFragment;
import com.csmy.minyuanplus.ui.fragment.collegenews.CollegeCultureFragment;
import com.csmy.minyuanplus.ui.fragment.collegenews.CollegeDynamicFragment;
import com.csmy.minyuanplus.ui.fragment.collegenews.CollegeNewsFragment;
import com.csmy.minyuanplus.ui.fragment.collegenews.HotLineFragment;
import com.csmy.minyuanplus.ui.fragment.collegenews.JobFragment;

/**
 * 民院新闻页面适配器
 * Created by Zero on 16/7/15.
 */
public class NewsPagerAdapter extends FragmentPagerAdapter{
    public static final int COUNT = 6;
    private Context mContext;
    private CollegeNewsFragment mCollegeNewsFragment;
    private CollegeDynamicFragment mCollegeDynamicFragment;
    private HotLineFragment mHotLineFragment;
    private CollegeBoothFragment mCollegeBoothFragment;
    private JobFragment mJobFragment;
    private CollegeCultureFragment mCollegeCultureFragment;




    public NewsPagerAdapter(Context context,FragmentManager fm) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if(mCollegeNewsFragment == null){
                    mCollegeNewsFragment = CollegeNewsFragment.newInstance();
                }
                return mCollegeNewsFragment;
            case 1:
                if(mCollegeDynamicFragment == null){
                    mCollegeDynamicFragment = CollegeDynamicFragment.newInstance();
                }
                return mCollegeDynamicFragment;
            case 2:
                if(mHotLineFragment == null){
                    mHotLineFragment = mHotLineFragment.newInstance();
                }
                return mHotLineFragment;
            case 3:
                if(mCollegeBoothFragment == null){
                    mCollegeBoothFragment = mCollegeBoothFragment.newInstance();
                }
                return mCollegeBoothFragment;
            case 4:
                if(mJobFragment == null){
                    mJobFragment = mJobFragment.newInstance();
                }
                return mJobFragment;
            case 5:
                if(mCollegeCultureFragment == null){
                    mCollegeCultureFragment = mCollegeCultureFragment.newInstance();
                }
                return mCollegeCultureFragment;
            default:
                return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return mContext.getString(R.string.my_news);
            case 1:
                return mContext.getString(R.string.college_dynamic);
            case 2:
                return mContext.getString(R.string.hot_line);
            case 3:
                return mContext.getString(R.string.college_booth);
            case 4:
                return mContext.getString(R.string.job);
            case 5:
                return mContext.getString(R.string.college_culture);
        }
        return "";
    }
}
