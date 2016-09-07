package com.csmy.minyuanplus.support.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.ui.fragment.afterclass.DailyFragment;
import com.csmy.minyuanplus.ui.fragment.afterclass.GuokrFragment;
import com.csmy.minyuanplus.ui.fragment.afterclass.WeixinFragment;

import org.litepal.LitePalApplication;

/**
 * Created by Zero on 16/7/15.
 */
public class AfterClassPagerAdapter extends FragmentStatePagerAdapter {
    public static final int COUNT = 3;
    private Context context = LitePalApplication.getContext();
    private DailyFragment mDailyFragment;
    private GuokrFragment mGuokrFragmnet;
    private WeixinFragment mWeixinFragment;

    public AfterClassPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (mDailyFragment == null) {
                    mDailyFragment = DailyFragment.newInstance();
                }
                return mDailyFragment;
            case 1:
                if (mWeixinFragment == null) {
                    mWeixinFragment = WeixinFragment.newInstance();
                }
                return mWeixinFragment;
            case 2:

                if (mGuokrFragmnet == null) {
                    mGuokrFragmnet = GuokrFragment.newInstance();
                }
                return mGuokrFragmnet;
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
                return context.getString(R.string.zhihu);
            case 1:
                return context.getString(R.string.weixin_special);
            case 2:
                return context.getString(R.string.guokr);
        }
        return "";
    }
}
