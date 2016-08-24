package com.csmy.minyuanplus.ui.fragment.education;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.education.EduInfo;
import com.csmy.minyuanplus.education.EduLogin;
import com.csmy.minyuanplus.education.EduRxVolley;
import com.csmy.minyuanplus.education.EduSchedule;
import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.event.EventTag;
import com.csmy.minyuanplus.model.education.AcademicYear;
import com.csmy.minyuanplus.model.education.Course;
import com.csmy.minyuanplus.model.education.PersonalInfo;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.ui.BaseToolbarView;
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

    @Bind(R.id.id_base_tool_bar)
    Toolbar mLoginToolbar;

    @BindString(R.string.try_again_later)
    String tryAgainLater;
    @BindString(R.string.psw_error)
    String pswError;
    @BindString(R.string.obtain_schedule_fail)
    String obtainScheduleFail;
    @BindString(R.string.input_complete_info)
    String inputCompleteInfo;
    @BindString(R.string.not_common)
    String notCommon;
    @BindString(R.string.need_common)
    String needCommon;
    @BindString(R.string.login_education)
    String loginEducation;

    private AppCompatEditText mUserNameET;
    private AppCompatEditText mPswET;
    private String mUserName;
    private String mPsw;

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
            //-------------
            EduRxVolley.enterEducationHome();
            //-------------
//            EduRxVolley.enterEducationHome();
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
                ToastUtil.show("登录失败...");
                break;
            case Event.EDUCATION_OBTAIN_SCHEDULE_SUCCESS:
                dismissWaitDialog();
                List<Course> courseList = EduSchedule.getEducationScheduleCourses(true);
                DataSupport.deleteAll(Course.class);
                DataSupport.saveAll(courseList);

                List<Course> courses = DataSupport.findAll(Course.class);

                StringBuilder sb = new StringBuilder();
                for (Course course : courses) {
                    sb.append(course.getAcademicYear() + " " + course.getTerm() + "学期" + course.getCourseName() + " " + "周" + course.getDay() + "第" + course.getBeginClass() + "," + course.getEndClass() + "节" + " " + "第" + course.getBeginWeek() + "," + course.getEndWeek() + "周" + "\n");
                }
                Logger.d(sb.toString());

                EduLogin.setEducationLogin(true);

                Event.sendEmptyMessage(Event.EDUCATION_LOGIN_SUCCESS);

                if (EduLogin.isEducationLogined()) {
                    dismissWaitDialog();
                    addFragment(mScheduleFragment);
                }

                break;
            case Event.EDUCATION_OBTAIN_SCHEDULE_FAIL:
                ToastUtil.show(obtainScheduleFail);
                Logger.d("获取课表html失败");
                dismissWaitDialog();
                break;
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

                List<AcademicYear> academicYearList = DataSupport.findAll(AcademicYear.class);
                for (AcademicYear ay : academicYearList) {
                    String ayStr = ay.getAcademicYear();
                    Log.i(TAG, "ay: " + ayStr);
                }
                Logger.d("获取个人信息成功: " + "姓名:" + pi.getName() + " 性别:" + pi.getSex() + " 班级:" + pi.getClassInfo() + " 专业:" + pi.getMajor() + pi.getGrade());
                EduRxVolley.obtainSchedule();
                break;
            case Event.EDUCATION_OBTAIN_PERSONAL_INFO_FAIL:
                Logger.d("获取个人信息失败:" + eventModel.getData().toString());
                break;

        }
    }

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
        getHoldingActivity().setSupportActionBar(mLoginToolbar);
        getHoldingActivity().getSupportActionBar().setTitle("登录教务系统");
    }
}
