package com.csmy.minyuanplus.support.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.csmy.minyuanplus.ui.fragment.college.ATMFragment;

/**
 * Created by Zero on 16/7/15.
 */
public class CollegePagerAdapter extends FragmentStatePagerAdapter{
    public static final int COUNT = 2;
    private String[] titles = new String[]{"ATM","快递"};

    private ATMFragment mATMFragment;






    public CollegePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if(mATMFragment == null){
                    mATMFragment = ATMFragment.newInstance();
                }
                return mATMFragment;
//            case 1:
//                if(mGuokrFragmnet == null){
//                    mGuokrFragmnet = GuokrFragment.newInstance();
//                }
//                return mGuokrFragmnet;
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
        return titles[position];
    }
}
