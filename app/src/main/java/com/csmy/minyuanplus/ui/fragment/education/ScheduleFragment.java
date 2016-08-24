package com.csmy.minyuanplus.ui.fragment.education;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.csmy.minyuanplus.ui.activity.GradeActivity;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;
import com.csmy.minyuanplus.ui.view.CourseLayout;
import com.csmy.minyuanplus.ui.view.CourseView;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindString;

public class ScheduleFragment extends BaseFragment{
    @Bind(R.id.id_schedule_horizontal)
    LinearLayout mScheduleHorzLayout;
    @Bind(R.id.id_course_layout)
    CourseLayout mCourseLayout;
    @Bind(R.id.id_schedule_vertical)
    LinearLayout mScheduleVertLayout;
    @Bind(R.id.id_home_toolbar)
    Toolbar mScheduleToolbar;
    @Bind(R.id.id_schedule_week_spinner)
    MaterialSpinner mScheduleWeekSpinner;
    @BindString(R.string.confirm)
    String confirm;
    @BindString(R.string.set_success)
    String setSuccess;
    @BindString(R.string.cancel)
    String cancel;
    @BindString(R.string.current_week)
    String currentWeek;
    @BindString(R.string.week)
    String week;
    @BindString(R.string.set_current_week)
    String setCurrentWeek;
    @BindString(R.string.obtain_schedule_fail)
    String obtainScheduleFail;


    private String mAcademicYear;
    private String mTerm;


    private static final String SQL_WHERE = "beginWeek <= ? and ? <= endWeek and academicYear = ? and term = ?";


    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }

    private static final String TAG = "ScheduleFragment";


    @Override
    protected void initView(View view, Bundle saveInstanceState) {
        mAcademicYear = EduInfo.getCurrentAcademicYear();
        mTerm = EduInfo.getCurrentTerm();
        initToolbar();
//        initCurrentWeek();q
        initSpinner();
        initSchedule(EduSchedule.getScheduleWeek());

    }

    /**
     * 跳转到成绩查询
     */
    private void intentGrade() {
        Intent intent = new Intent(getContext(), GradeActivity.class);
        startActivity(intent);
    }


    /**
     * 初始化当前周---------------------------mark 这里需要检查
     */
    private void initCurrentWeek() {
        int pastCmdDate = EduSchedule.getCmdFormatDate();
        int nowCmdDate = EduSchedule.getCurrentMondayWeekformatDate();
        int different = nowCmdDate - pastCmdDate;
        if ((different) != 0) {
            int curtWeek = EduSchedule.getScheduleWeek() + different / 7;
            EduSchedule.saveScheduleWeek(curtWeek);
        }
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        setHasOptionsMenu(true);

        getHoldingActivity().setSupportActionBar(mScheduleToolbar);
        getHoldingActivity().getSupportActionBar().setDisplayShowTitleEnabled(false);


        mScheduleToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_set_current_week:
                        showSetCurrentWeekDialog();
                        break;
                    case R.id.action_grade:
                        intentGrade();
                        break;
                    case R.id.action_quit_account:
                        quitAccount();
                        break;
                    case R.id.action_switch_term:
                        showSetCurrentTermDialog();
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 退出帐号
     */
    private void quitAccount(){
        DataSupport.deleteAll(AcademicYear.class);
        DataSupport.deleteAll(Course.class);
        DataSupport.deleteAll(PersonalInfo.class);
        EduLogin.setEducationLogin(false);
        /*
        重启应用
         */
        getHoldingActivity().recreate();
    }

    private void initSpinner() {
        int curtWeek = EduSchedule.getScheduleWeek();

        List<String> datas = Arrays.asList(getResources().getStringArray(R.array.weeks));
        for (int i = 0; i < datas.size(); i++) {
            if (EduSchedule.splitWeek(datas.get(i)) == curtWeek) {
                //标注当前周
                datas.set(i, datas.get(i) + currentWeek);
            }
        }
        mScheduleWeekSpinner.setItems(datas);
        mScheduleWeekSpinner.setSelectedIndex(curtWeek - 1);

        mScheduleWeekSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//                view.setBackgroundColor(colorPrimary);
                initSchedule(position + 1);
            }
        });

    }


    /**
     * 初始化课表
     *
     * @param week 周数
     */
    public void initSchedule(int week) {
        List<Course> courses = DataSupport.where(SQL_WHERE, String.valueOf(week), String.valueOf(week), mAcademicYear, mTerm)
                .find(Course.class);
        List<CourseView> courseViews = new ArrayList<CourseView>();
        int numOfClass = EduSchedule.getNumOfClass();
        for (Course course : courses) {
            CourseView courseView = new CourseView(getHoldingActivity());
            String courseName = course.getCourseName();
            String classroom = course.getClassroom();
            String teacher = course.getTeacher();
            int beginClass = course.getBeginClass();
            int endClass = course.getEndClass();
            int day = course.getDay();
            int beginWeek = course.getBeginWeek();
            int endWeek = course.getEndWeek();

            if (day >= EduSchedule.DEFAULT_DAYS_HAVE_CLASS) {
                EduSchedule.saveDaysHaveClass(day);
            }
            if ((endClass / 2) > numOfClass) {
                EduSchedule.saveNumOfClass(endClass / 2);
            }
            courseView.setCourseName(courseName);
            courseView.setClassroom(classroom);
            courseView.setTeacher(teacher);
            courseView.setBeginClass(beginClass);
            courseView.setEndClass(endClass);
            courseView.setDay(day);
            courseView.setBeginWeek(beginWeek);
            courseView.setEndWeek(endWeek);

            for (int i = 0; i < courseViews.size(); i++) {
                CourseView cv = courseViews.get(i);
                if (cv.getCourseName().equals(courseName)
                        && cv.getDay() == day
                        && cv.getClassroom().equals(classroom)
                        && Math.abs(cv.getBeginClass() - beginClass) == 2
                        ) {
                    courseView.setBeginClass(cv.getBeginClass());
                    courseViews.remove(i);
                }
            }
            courseViews.add(courseView);
        }

        Map<String, Integer> ids = new HashMap<String, Integer>();
        int i = -1;
        for (CourseView cv : courseViews) {
            String courseName = cv.getCourseName();

            if (ids.containsKey(courseName)) {
                cv.setId(ids.get(courseName));
            } else {
                i += 1;
                cv.setId(i);
                ids.put(courseName, i);
            }
        }

        initWeek();
        initClassNum();

        mCourseLayout.addCourseViews(courseViews);
    }


    /**
     * 初始化星期数
     */
    private void initWeek() {
        LinearLayout horzLayout = (LinearLayout) mScheduleHorzLayout.getChildAt(0);
        for (int i = 0; i < horzLayout.getChildCount(); i++) {
            TextView tv = (TextView) horzLayout.getChildAt(i);
            if (i == 0) {
                tv.setText(EduSchedule.getScheduleWeek() + week);
            } else {
                tv.setText(week + EduSchedule.weekIntToString(i));
            }
            //星期六星期日没有课则隐藏
            if (i > EduSchedule.getDaysHaveClass()) {
                tv.setVisibility(View.GONE);
            } else {
                tv.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 初始化节次
     */
    private void initClassNum() {
        LinearLayout layout = (LinearLayout) ((LinearLayout) mScheduleVertLayout.getChildAt(0)).getChildAt(0);

        for (int i = 0; i < layout.getChildCount(); i++) {
            TextView tv = (TextView) layout.getChildAt(i);

            tv.setText(i + 1 + "");
//            if ((i+ 1) > EduSchedule.getDaysHaveClass()*2) {
//                tv.setVisibility(View.GONE);
//            } else {
//                tv.setVisibility(View.VISIBLE);
//            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_schedule;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {
        switch (eventModel.getEventCode()) {
            case Event.EDUCATION_SWITCH_SCHEDULE_SUCCESS:
                dismissWaitDialog();
                initSchedule(EduSchedule.getScheduleWeek());
                break;
            case Event.EDUCATION_SWITCH_SCHEDULE_FAIL:
                dismissWaitDialog();
                ToastUtil.show(obtainScheduleFail);
                Logger.d("切换课表失败");
                break;

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_schedule, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    /**
     * 设置当前周
     */
    private void showSetCurrentWeekDialog() {
        final int[] select = new int[1];
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        builder.setTitle(setCurrentWeek);
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.weeks),
                EduSchedule.getScheduleWeek() - 1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select[0] = which + 1;
                    }
                });
        builder.setPositiveButton(confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EduSchedule.saveScheduleWeek(select[0]);
                dialog.dismiss();
                Toast.makeText(getContext(), setSuccess, Toast.LENGTH_SHORT).show();
//                SnackbarUtil.showWithNoAction(mCourseLayout,setSuccess);
                initSpinner();
                initSchedule(EduSchedule.getScheduleWeek());
            }
        });

        builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    /**
     * 设置当前学期
     */
    private void showSetCurrentTermDialog() {

        final List ayList = new ArrayList<String>();
        List<AcademicYear> ays = DataSupport.findAll(AcademicYear.class);
        for (int i = 0; i < ays.size(); i++) {
            ayList.add(ays.get(i).getAcademicYear());
        }
        String[] terms = new String[6];
        final int[] selected = new int[1];
        for (int i = 0; i < 3; i++) {
            terms[2 * i] = EduInfo.getSchoolYear(ayList.get(i) + " ") + getHoldingActivity().getString(R.string.the) + 1 + getHoldingActivity().getString(R.string.term);
            terms[2 * i + 1] = EduInfo.getSchoolYear(ayList.get(i) + " ") + getHoldingActivity().getString(R.string.the) + 2 + getHoldingActivity().getString(R.string.term);
        }
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        builder.setTitle(getHoldingActivity().getString(R.string.switch_term));
        builder.setSingleChoiceItems(terms,
                0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selected[0] = which;
                    }
                });
        builder.setPositiveButton(confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int term = selected[0] % 2 + 1;
                if (term == 2) {
                    mAcademicYear = ayList.get((selected[0] - 1) / 2).toString().trim();
                } else {
                    mAcademicYear = ayList.get(selected[0] / 2).toString().trim();
                }

                mTerm = String.valueOf(term).trim();

                //保存学年学期信息
                EduInfo.saveCurrentTerm(mTerm);
                EduInfo.saveCurrentAcademicYear(mAcademicYear);

                Logger.d("学年：" + mAcademicYear + " 学期：" + mTerm);


                List<Course> courses = DataSupport.where("academicYear = ? and term = ?", mAcademicYear, mTerm).find(Course.class);
                //从数据库读取课表数据
                if (courses.size() > 0) {
                    EventBus.getDefault().post(new EventModel<Integer>(Event.EDUCATION_SWITCH_SCHEDULE_SUCCESS));
                }
                //从网络加载课表数据
                else {
                    EventTag.saveCurrentTag(EventTag.SWITCH_SCHEDULE);
                    EduRxVolley.enterEducationHome();
                    showWaitDialog();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
