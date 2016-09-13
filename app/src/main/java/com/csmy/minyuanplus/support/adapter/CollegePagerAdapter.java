package com.csmy.minyuanplus.support.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.ui.fragment.college.ATMFragment;
import com.csmy.minyuanplus.ui.fragment.college.ExpressFragment;

import org.litepal.LitePalApplication;

/**
 * 民院页面适配器
 * Created by Zero on 16/7/15.
 */
public class CollegePagerAdapter extends FragmentStatePagerAdapter {
    public static final int COUNT = 2;
    private Context context = LitePalApplication.getContext();
    private ATMFragment mATMFragment;
    private ExpressFragment mExpressFragment;


    public CollegePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (mATMFragment == null) {
                    mATMFragment = ATMFragment.newInstance();
                }
                return mATMFragment;
            case 1:
                if(mExpressFragment == null){
                    mExpressFragment = ExpressFragment.newInstance();
                }
                return mExpressFragment;
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
                return context.getString(R.string.atm);
            case 1:
                return context.getString(R.string.express);
        }
        return "";
    }
}
