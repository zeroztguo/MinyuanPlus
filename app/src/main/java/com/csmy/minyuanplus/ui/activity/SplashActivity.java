package com.csmy.minyuanplus.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.support.util.Util;

import butterknife.Bind;

/**
 * 闪屏页
 */
public class SplashActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    @Bind(R.id.id_splash_vp)
    ViewPager mViewPager;
    @Bind(R.id.id_tips_layout)
    LinearLayout mTipsLayout;
    @Bind(R.id.id_splash_right_fab)
    FloatingActionButton mRightFab;
    @Bind(R.id.id_splash_left_fab)
    FloatingActionButton mLeftFab;

    private SplashPagerAdapter mPagerAdapter;

    /**
     * 装点点的数组
     */
    private AppCompatImageView[] mTips;

    /**
     * 装图片的数组
     */
    private AppCompatImageView[] mImageViews;

    /**
     * 图片资源id
     */
    private int[] mImgIdArray;


    private static final int COUNT = 6;

    private int mCurrentItem = 0;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        /*
        本地保存的版本号小于当前应用版本号则跳转到主页面
         */
        if (Util.getCurrentVersionCode() < Util.getVersionCode()) {
            Util.setCurrentVersionCode(Util.getVersionCode());
        }else{
            forward2Main();
        }

//        setSwipeBackEnable(false);

        mImgIdArray = new int[]{R.mipmap.splash_01, R.mipmap.splash_02
                , R.mipmap.splash_03, R.mipmap.splash_04
                , R.mipmap.splash_05, R.mipmap.splash_06};

        //把点点放入数组
        mTips = new AppCompatImageView[mImgIdArray.length];
        for (int i = 0; i < mTips.length; i++) {
            AppCompatImageView imageView = new AppCompatImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(40, 40));

            mTips[i] = imageView;
            mTips[i].setBackgroundResource(
                    i == 0 ? R.mipmap.tip_select : R.mipmap.tip_unselect);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(40, 40));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            mTipsLayout.addView(imageView, layoutParams);
        }

        //把图片放入数组
        mImageViews = new AppCompatImageView[mImgIdArray.length];
        for (int i = 0; i < mImageViews.length; i++) {
            AppCompatImageView imageView = new AppCompatImageView(this);
            mImageViews[i] = imageView;
            imageView.setBackgroundResource(mImgIdArray[i]);
        }

        mLeftFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mCurrentItem - 1);
            }
        });

        mRightFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentItem == COUNT - 1) {
                    forward2Main();
                } else {
                    mViewPager.setCurrentItem(mCurrentItem + 1);
                }
            }
        });

        mLeftFab.setVisibility(View.GONE);


        mPagerAdapter = new SplashPagerAdapter();

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

    }

    private void forward2Main() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentItem = position;

        if (mCurrentItem == (COUNT - 1)) {
            mRightFab.setImageResource(R.mipmap.finish);
        } else {
            mRightFab.setImageResource(R.mipmap.arrow_right);
        }

        if (position == 0) {
            mLeftFab.setVisibility(View.GONE);
        } else {
            mLeftFab.setVisibility(View.VISIBLE);
        }

        //更新点点状态
        for (int i = 0; i < mTips.length; i++) {
            mTips[i].setBackgroundResource(
                    position == i ? R.mipmap.tip_select : R.mipmap.tip_unselect);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    /**
     * Created by Zero on 16/9/10.
     */
    public class SplashPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return COUNT;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mImageViews[position]);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mImageViews[position]);
            return mImageViews[position];
        }
    }
}
