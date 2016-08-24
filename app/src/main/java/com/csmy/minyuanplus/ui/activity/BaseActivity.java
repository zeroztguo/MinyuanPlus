package com.csmy.minyuanplus.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.csmy.minyuanplus.dialog.WaitDialog;
import com.csmy.minyuanplus.support.SettingConfig;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;

import butterknife.ButterKnife;

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
        setTheme(SettingConfig.themeArray[SettingConfig.getThemeIndex()]);
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        initView(savedInstanceState);
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

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Logger.d("BaseActivity~~~~~~");
//
//    }

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
