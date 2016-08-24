package com.csmy.minyuanplus.support.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.csmy.minyuanplus.ui.fragment.afterclass.DailyFragment;
import com.csmy.minyuanplus.ui.fragment.afterclass.GuokrFragment;

/**
 * Created by Zero on 16/7/15.
 */
public class AfterClassPagerAdapter extends FragmentStatePagerAdapter{
    public static final int COUNT = 2;
    private String[] titles = new String[]{"知乎日报","果壳"};

    private DailyFragment mDailyFragment;
    private GuokrFragment mGuokrFragmnet;
//    private PsychologyFragment mPsychologyFragment;






    public AfterClassPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if(mDailyFragment == null){
                    mDailyFragment = DailyFragment.newInstance();
                }
                return mDailyFragment;
            case 1:
                if(mGuokrFragmnet == null){
                    mGuokrFragmnet = GuokrFragment.newInstance();
                }
                return mGuokrFragmnet;
//            case 1:
//                if(mPsychologyFragment == null){
//                    mPsychologyFragment = PsychologyFragment.newInstance();
//                }
//                return mPsychologyFragment;
//            case 2:
//                if(mNoBoringFragment == null){
//                    mNoBoringFragment = NoBoringFragment.newInstance();
//                }
//                return mNoBoringFragment;
//            case 3:
//                if(mSportFragment == null){
//                    mSportFragment = SportFragment.newInstance();
//                }
//                return mSportFragment;
//            case 4:
//                if(mMusicFragment == null){
//                    mMusicFragment = MusicFragment.newInstance();
//                }
//                return mMusicFragment;
//            case 5:
//                if(mGameFragment == null){
//                    mGameFragment = GameFragment.newInstance();
//                }
//                return mGameFragment;
//            case 6:
//                if(mCartoonFragment == null){
//                    mCartoonFragment = CartoonFragment.newInstance();
//                }
//                return mCartoonFragment;
            default:
                return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return COUNT;
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//
//        switch (position){
//            case 0:
//                DailyFragment dailyFragment = (DailyFragment) super.instantiateItem(container,position);
//                return dailyFragment;
//
//            case 1:
//                PsychologyFragment psychologyFragment = (PsychologyFragment) super.instantiateItem(container,position);
//                return psychologyFragment;
//            case 2:
//                NoBoringFragment noBoringFragment = (NoBoringFragment) super.instantiateItem(container,position);
//                return noBoringFragment;
//            case 3:
//                SportFragment sportFragment = (SportFragment) super.instantiateItem(container,position);
//                return sportFragment;
//            case 4:
//                MusicFragment musicFragment = (MusicFragment) super.instantiateItem(container,position);
//                return musicFragment;
//            case 5:
//                GameFragment gameFragment = (GameFragment) super.instantiateItem(container,position);
//                return gameFragment;
//            case 6:
//                CartoonFragment cartoonFragment = (CartoonFragment) super.instantiateItem(container,position);
//                return cartoonFragment;
//        }
//        return super.instantiateItem(container, position);
//    }

//    @Override
//    public int getItemPosition(Object object) {
//        return PagerAdapter.POSITION_NONE;
//    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
