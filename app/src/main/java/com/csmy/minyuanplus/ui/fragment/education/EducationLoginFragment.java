package com.csmy.minyuanplus.ui.fragment.education;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.support.BadgeActionProvider;
import com.csmy.minyuanplus.support.Notification;
import com.csmy.minyuanplus.support.education.EduInfo;
import com.csmy.minyuanplus.support.education.EduLogin;
import com.csmy.minyuanplus.support.education.EduRxVolley;
import com.csmy.minyuanplus.support.education.EduSchedule;
import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.event.EventTag;
import com.csmy.minyuanplus.model.education.AcademicYear;
import com.csmy.minyuanplus.model.education.Course;
import com.csmy.minyuanplus.model.education.PersonalInfo;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.ui.BaseToolbarView;
import com.csmy.minyuanplus.ui.activity.NotifyActivity;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;

/**
 * Created by Zero on 16/6/21.
 */
public class EducationLoginFragment extends BaseFragment implements BaseToolbarView {
    @Bind(R.id.id_login_user_name_til)
    android.support.design.widget.TextInputLayout mUserNameWrapper;
    @Bind(R.id.id_login_psw_til)
    android.support.design.widget.TextInputLayout mPswWrapper;

    @Bind(R.id.id_login_toolbar)
    Toolbar mLoginToolbar;

    @BindString(R.string.input_complete_info)
    String inputCompleteInfo;


    private BadgeActionProvider mBadgeActionProvider;
    private AppCompatEditText mUserNameET;
    private AppCompatEditText mPswET;
    private String mUserName;
    private String mPsw;
    private boolean mIsInit = true;


    private ScheduleFragment mScheduleFragment = ScheduleFragment.newInstance();

    public static EducationLoginFragment newInstance() {
        return new EducationLoginFragment();
    }

    private static final String TAG = "EducationLoginFragment";

    @Override
    protected void initView(View view, Bundle saveInstanceState) {
        initToolbar();

        initEditText();
    }

    private void initEditText() {
        mUserNameET = (AppCompatEditText) mUserNameWrapper.getEditText();
        mPswET = (AppCompatEditText) mPswWrapper.getEditText();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPswWrapper.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mUserNameET.addTextChangedListener(textWatcher);

        mPswET.addTextChangedListener(textWatcher);

        mUserNameET.setText(EduInfo.getEducationUserName());
        mPswET.setText(EduInfo.getEducationPassword());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_education_login;
    }


    @OnClick(R.id.id_login_tv)
    void login(View view) {
        Log.i(TAG, "login: click");
        hideKeyboard();
        checkLoginInfo();
    }

    @OnClick(R.id.id_login_cannot_login)
    void cannotLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.prompt));
        builder.setMessage(getString(R.string.login_prompt));
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_login, menu);
        MenuItem menuItem = menu.findItem(R.id.action_login_notification);
        mBadgeActionProvider = (BadgeActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mBadgeActionProvider.setOnClickListener(0, new BadgeActionProvider.OnClickListener() {
            @Override
            public void onClick(int what) {
                Intent intent = new Intent(getHoldingActivity(), NotifyActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 检查登录信息
     */
    private void checkLoginInfo() {
        mUserName = mUserNameWrapper.getEditText().getText().toString().trim();
        mPsw = mPswWrapper.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mPsw)) {
            showError(inputCompleteInfo);
        } else {
            EduInfo.saveEducationUserName(mUserName);
            EduInfo.saveEducationPassword(mPsw);
            EventTag.saveCurrentTag(EventTag.LOGIN_EDUCATION);
            EduRxVolley.enterEducationHome();
            showWaitDialog();
        }
    }

    /**
     * 显示错误信息
     *
     * @param error 错误信息
     */
    private void showError(String error) {
        mPswWrapper.setErrorEnabled(true);
        mPswWrapper.setError(error);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {
        switch (eventModel.getEventCode()) {
            case Event.EDUCATION_LOGIN_FAIL:
                dismissWaitDialog();
                ToastUtil.showShort(getContext(), getString(R.string.login_fail));
                break;
            case Event.EDUCATION_OBTAIN_SCHEDULE_SUCCESS:
                dismissWaitDialog();
                List<Course> courseList = EduSchedule.getEducationScheduleCourses(true);
                DataSupport.deleteAll(Course.class);
                DataSupport.saveAll(courseList);

                List<Course> courses = DataSupport.findAll(Course.class);

                for (Course course : courses) {
                    Logger.d("0831：" + course.toString());
                }

                EduLogin.setEducationLogin(true);

                Event.sendEmptyMessage(Event.EDUCATION_LOGIN_SUCCESS);

                if (EduLogin.isEducationLogined()) {
                    dismissWaitDialog();
                    addFragment(mScheduleFragment);
                }

                break;
//            case Event.EDUCATION_OBTAIN_SCHEDULE_FAIL:
//                ToastUtil.show(obtainScheduleFail);
//                Logger.d("获取课表html失败");
//                dismissWaitDialog();
//                break;
            case Event.EDUCATION_OBTAIN_PERSONAL_INFO_SUCCESS:
                PersonalInfo pi = EduLogin.getPersonalInfo();
                pi.save();

                DataSupport.deleteAll(AcademicYear.class);
                int grade = Integer.valueOf(pi.getGrade());
                for (int i = 0; i < 3; i++) {
                    AcademicYear ay = new AcademicYear();
                    ay.setAcademicYear((grade + i) + "-" + (grade + i + 1));
                    ay.save();
                }

                EduRxVolley.obtainSchedule();
                break;
//            case Event.EDUCATION_OBTAIN_PERSONAL_INFO_FAIL:
//                Logger.d("获取个人信息失败:" + eventModel.getData().toString());
//                break;
            case Event.NOTIFY_UPDATE:
                Logger.d("收到 通知：" + Notification.getLatestNotifyCode());
                if (mBadgeActionProvider != null) {
                    mBadgeActionProvider.setTextInt(BadgeActionProvider.getUnreadCount());
                }
                break;

        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (!mIsInit) {
//            if (mBadgeActionProvider != null) {
//                mBadgeActionProvider.setTextInt(BadgeActionProvider.getUnreadCount());
//            }
//        } else {
//            mIsInit = !mIsInit;
//        }
//    }

    /**
     * 隐藏键盘
     */
    private void hideKeyboard() {
        View view = getHoldingActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getHoldingActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    public void initToolbar() {
        setHasOptionsMenu(true);
        getHoldingActivity().setSupportActionBar(mLoginToolbar);
        getHoldingActivity().getSupportActionBar().setTitle(getString(R.string.login));
    }
}
