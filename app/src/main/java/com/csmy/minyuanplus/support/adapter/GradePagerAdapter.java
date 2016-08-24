package com.csmy.minyuanplus.support.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.ui.fragment.education.GradeFailFragment;
import com.csmy.minyuanplus.ui.fragment.education.GradeFragment;
import com.csmy.minyuanplus.ui.fragment.education.GradeStatisticalFragment;

import org.litepal.LitePalApplication;

/**
 * 成绩查询viewpager适配器
 * Created by Zero on 16/6/28.
 */
public class GradePagerAdapter extends FragmentPagerAdapter{
    public static final int COUNT = 3;
    private Context context = LitePalApplication.getContext();

    private GradeFragment mGradeFragment;
    private GradeFailFragment mGradeFailFragment;
    private GradeStatisticalFragment mGradeStatisticalFragment;

    public GradePagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if(mGradeFragment == null){
                    mGradeFragment = GradeFragment.newInstance();
                }
                return mGradeFragment;
            case 1:
                if(mGradeFailFragment == null){
                    mGradeFailFragment = GradeFailFragment.newInstance();
                }
                return mGradeFailFragment;
            case 2:
                if(mGradeStatisticalFragment == null){
                    mGradeStatisticalFragment = GradeStatisticalFragment.newInstance();
                }
                return mGradeStatisticalFragment;
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
        switch (position) {
            case 0:
                return context.getString(R.string.grade_term);
            case 1:
                return context.getString(R.string.grade_fail);
            case 2:
                return context.getString(R.string.grade_statistical);
        }
        return "";
    }
}
