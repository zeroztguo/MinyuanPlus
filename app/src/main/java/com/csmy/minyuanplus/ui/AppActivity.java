package com.csmy.minyuanplus.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.ui.activity.BaseActivity;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import butterknife.BindColor;
import butterknife.ButterKnife;

public abstract class AppActivity extends BaseActivity {
    /**
     * 系统是否保存activity状态
     */
    protected boolean isSaveInstanceStateNull = false;

    protected int index;


    @BindColor(R.color.colorPrimary)
    int colorPrimary;

    private static final String TAG = "AppActivity";

    //获取第一个fragment
    protected abstract BaseFragment getFirstFragment();

    protected abstract void initView();


    public abstract void onUserEvent(EventModel eventModel);

    protected void handleIntent(Intent intent) {
    } //获取Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);

        //避免重复添加Fragment
//        if (null == getSupportFragmentManager().getFragments()) {
//            BaseFragment firstFragment = getFirstFragment();
//            if (null != firstFragment) {
//                addFragment(firstFragment);
//            }
//        }
//        setSupportActionBar(mToolbar);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        //注册EventBus


        /*
        内存重启时防止fragment重叠
         */
//        EducationLoginFragment loginFragment = null;
//        ScheduleFragment scheduleFragment = null;
//        NewsFragment newsFragent = null;
//        AfterClassFragment afterClassFragment = null;

//
//        if(savedInstanceState ==null){
//            isSaveInstanceStateNull = true;
//        }else{
//           loginFragment = (EducationLoginFragment) getSupportFragmentManager()
//                   .findFragmentByTag(loginFragment.getClass().getSimpleName());
//
//            scheduleFragment = (ScheduleFragment) getSupportFragmentManager()
//                    .findFragmentByTag(scheduleFragment.getClass().getSimpleName());
//            newsFragent = (NewsFragment) getSupportFragmentManager()
//                    .findFragmentByTag(newsFragent.getClass().getSimpleName());
//            afterClassFragment = (AfterClassFragment) getSupportFragmentManager()
//                    .findFragmentByTag(afterClassFragment.getClass().getSimpleName());
//
//
//            isSaveInstanceStateNull  = false;
//        }

        initView();


        initStatuBar();




    }

    private void initStatuBar() {
        //当api>=19时
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ){
            setTranslucentStatus(true);
        }
        //创建状态栏的管理实例
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        //设置一个颜色给系统栏
//        tintManager.setTintColor(colorPrimary);
        tintManager.setTintResource(R.color.white);
        //设置状态栏需颜色或背景图
        tintManager.setStatusBarTintResource(R.color.colorPrimary);
        //设置一个样式背景给导航栏，当使用actionbar的时候使用
        tintManager.setNavigationBarTintColor(R.color.colorPrimary);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on){
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if(on){
            winParams.flags |= bits;
        }else{
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    @Override
    protected int getFragmentContentId() {

        return R.id.fragment_container;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }



}
