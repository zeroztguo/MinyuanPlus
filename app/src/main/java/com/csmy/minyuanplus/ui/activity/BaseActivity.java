package com.csmy.minyuanplus.ui.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.KeyEvent;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.support.SettingConfig;
import com.csmy.minyuanplus.ui.SwipeBackActivity;
import com.csmy.minyuanplus.ui.dialog.WaitDialog;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;

import java.util.Locale;

import butterknife.ButterKnife;

/**
 * Activity基类
 */
public abstract class BaseActivity extends SwipeBackActivity {
    private BaseFragment mCurrentFragment;

    //布局文件ID
    protected abstract int getContentViewId();

    //布局中Fragment的ID
    protected abstract int getFragmentContentId();

    //等待框
    protected WaitDialog waitDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        设置主题
         */
        if (SettingConfig.isNightMode()) {
            setTheme(R.style.AppTheme_Night);
        } else {
            setTheme(SettingConfig.themeArray[SettingConfig.getThemeIndex()]);
        }
        initLanguage();
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        initView(savedInstanceState);
    }


    /**
     * 初始化语言
     */
    private void initLanguage() {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        String language = SettingConfig.getLanguage();
        if (language.equals(SettingConfig.ZH_SIMPLE)) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (language.equals(SettingConfig.ZH_TW)) {
            config.locale = Locale.TRADITIONAL_CHINESE;
        } else if (language.equals(SettingConfig.EN)) {
            config.locale = Locale.ENGLISH;
        } else {
            config.locale = Locale.getDefault();
        }
        resources.updateConfiguration(config, dm);
    }


    protected abstract void initView(Bundle savedInstanceState);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    //添加fragment
    public void addFragment(BaseFragment fragment) {
        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            if (mCurrentFragment != null) {
                fragmentTransaction.hide(mCurrentFragment);
            }


            fragmentTransaction
                    .add(getFragmentContentId(), fragment,
                            fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commitAllowingStateLoss();
            mCurrentFragment = fragment;
        }
    }


    //移除Fragment
    public void removeFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    //切换Fragment，Fragment以被初始化时调用
    protected void switchFragemnt(BaseFragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .hide(mCurrentFragment)
                    .show(fragment)
                    .commit();
            mCurrentFragment = fragment;
        }
    }

    //返回键事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (getSupportFragmentManager()
                    .getBackStackEntryCount() == 1) {
                finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    protected void showWaitDialog(Context context) {
        waitDialog = new WaitDialog(context);
        waitDialog.show();
    }

    protected void dismissWaitDialog() {
        if (waitDialog != null) {
            waitDialog.dismiss();
        }
    }
}
