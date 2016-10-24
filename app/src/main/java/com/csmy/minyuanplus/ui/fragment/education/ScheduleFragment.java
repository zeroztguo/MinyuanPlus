package com.csmy.minyuanplus.ui.fragment.education;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.event.EventTag;
import com.csmy.minyuanplus.model.education.AcademicYear;
import com.csmy.minyuanplus.model.education.Course;
import com.csmy.minyuanplus.support.BadgeActionProvider;
import com.csmy.minyuanplus.support.Notification;
import com.csmy.minyuanplus.support.education.EduInfo;
import com.csmy.minyuanplus.support.education.EduRxVolley;
import com.csmy.minyuanplus.support.education.EduSchedule;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.support.util.Util;
import com.csmy.minyuanplus.ui.activity.GradeActivity;
import com.csmy.minyuanplus.ui.activity.NotifyActivity;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;
import com.csmy.minyuanplus.ui.view.CourseLayout;
import com.csmy.minyuanplus.ui.view.CourseView;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindString;

public class ScheduleFragment extends BaseFragment {
    @Bind(R.id.id_schedule_horizontal)
    LinearLayout mScheduleHorzLayout;
    //    @Bind(R.id.id_course_layout)
    private com.csmy.minyuanplus.ui.view.CourseLayout mCourseLayout;
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

    private BadgeActionProvider mBadgeActionProvider;
    private String mAcademicYear;
    private String mTerm;
    private int mSelectedWeek;


    /**
     * 查询某周课表的sql where语句
     */
    private static final String SQL_SWITCH_SCHEDULE_WITH_WEEK_WHERE = "beginWeek <= ? and ? <= endWeek and academicYear = ? and term = ?";

    /**
     * 查询某学期课表的sql where语句
     */
    private static final String SQL_SWITCH_REFRESH_SCHEDULE_WITH_CURRENT_TERM_WHERE = "academicYear = ? and term = ?";


    public static ScheduleFragment newInstance() {
        return new ScheduleFragment();
    }


    @Override
    protected void initView(View view, Bundle saveInstanceState) {
        mCourseLayout = new CourseLayout(getContext());
        mCourseLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mScheduleVertLayout.addView(mCourseLayout);


        mAcademicYear = EduInfo.getCurrentAcademicYear();
        mTerm = EduInfo.getCurrentTerm();
        initToolbar();
        initCurrentWeek();
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
     * 初始化当前周
     */
    private void initCurrentWeek() {
        //设置时周数加间隔周数等于当前周数
        EduSchedule.saveScheduleWeek(EduSchedule.getScheduleWeek() + getSpaceWeek());
        EduSchedule.setCurrentWeekDate();
        Logger.d("间隔的周数:" + getSpaceWeek());
    }

    /**
     * 获取今天日期与设置当前周时的日期的间隔天数
     */
    private int getSpaceDay() {
        String[] dateInfo = EduSchedule.getSettingCurrentWeekDate().split("-");
        //设置当前周时的日期
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.valueOf(dateInfo[0]));
        cal.set(Calendar.MONTH, Integer.valueOf(dateInfo[1]));
        cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateInfo[2]));

        //现在的日期
        String[] dateInfo2 = Util.getCurrentDate().split("-");
        Calendar cal2 = Calendar.getInstance();
        cal2.set(Calendar.YEAR, Integer.valueOf(dateInfo2[0]));
        cal2.set(Calendar.MONTH, Integer.valueOf(dateInfo2[1]));
        cal2.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateInfo2[2]));


        //间隔天数
        int spaceDay = (int) ((cal2.getTimeInMillis() - cal.getTimeInMillis()) / (1000 * 60 * 60 * 24));
        return spaceDay;
    }

    /**
     * 获取设置当前周时的星期几
     */
    private int getSettingWeek() {
        String[] dateInfo = EduSchedule.getSettingCurrentWeekDate().split("-");
        //设置当前周时的日期
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.valueOf(dateInfo[0]));
        cal.set(Calendar.MONTH, Integer.valueOf(dateInfo[1]));
        cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateInfo[2]));

        int settingWeek = Util.getFormatWeekNum(cal.get(Calendar.DAY_OF_WEEK));
        return settingWeek;
    }

    /**
     * 获取当前日期与设置当前周时的日期的间隔周数
     */
    private int getSpaceWeek() {
        int settingWeek = getSettingWeek();
        int spaceDay = getSpaceDay();
        //间隔周数
        int spaceWeek = spaceDay / 7;
        if (spaceWeek > 0) {
            if (spaceDay % 7 > 0) {
                spaceWeek++;
            }

        } else {
            if ((spaceDay % 7) > (7 - settingWeek)) {
                spaceWeek++;
            }
        }
        return spaceWeek;
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
                    case R.id.action_switch_term:
                        showSetCurrentTermDialog();
                        break;
                    case R.id.action_timetable:
                        showSetTimetable();
                        break;
                    case R.id.action_refresh_schedule:
                        EventTag.saveCurrentTag(EventTag.SWITCH_SCHEDULE);
                        EduRxVolley.enterEducationHome();
                        showWaitDialog();
                        break;
                }
                return true;
            }
        });
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
        if (curtWeek > 0) {
            mScheduleWeekSpinner.setSelectedIndex(curtWeek - 1);
        }

        mScheduleWeekSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
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
        mSelectedWeek = week;

        List<Course> courses = DataSupport.where(SQL_SWITCH_SCHEDULE_WITH_WEEK_WHERE, String.valueOf(week), String.valueOf(week), mAcademicYear, mTerm)
                .find(Course.class);
        if (courses.size() == 0) {
            ToastUtil.showShort(getContext(), getString(R.string.this_week_not_course));
        }

        Logger.d("初始化课表这里所有课程数：" + DataSupport.findAll(Course.class).size());
        Logger.d("初始化课表这里课程数：" + courses.size());

        int dayHaveClass = EduSchedule.DEFAULT_DAYS_HAVE_CLASS;
        int numOfClass = EduSchedule.DEFAULT_NUM_OF_CLASS;
        for (Course course : courses) {
            if (course.getDay() > dayHaveClass) {
                dayHaveClass = course.getDay();
            }
            if (course.getEndClass() / 2 > numOfClass) {
                numOfClass = course.getEndClass() / 2;
            }
        }
        List<CourseView> courseViews = new ArrayList<>();
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

        Map<String, Integer> ids = new HashMap<>();
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

        initWeek(dayHaveClass);
        initClassNum(numOfClass);

        mCourseLayout.addCourseViews(courseViews, dayHaveClass, numOfClass);
    }


    /**
     * 初始化星期数
     *
     * @param dayHaveClass 有课的天数，周六周日没课则隐藏
     */
    private void initWeek(int dayHaveClass) {

        LinearLayout horzLayout = (LinearLayout) mScheduleHorzLayout.getChildAt(0);
        for (int i = 0; i < horzLayout.getChildCount(); i++) {
            AppCompatTextView tv = (AppCompatTextView) horzLayout.getChildAt(i);
            if (i == 0) {
                tv.setText(mSelectedWeek + week);
            } else {
                tv.setText(week + EduSchedule.weekIntToString(i));
                //星期六星期日没有课则隐藏
                if (i > dayHaveClass) {
                    tv.setVisibility(View.GONE);
                } else {
                    tv.setVisibility(View.VISIBLE);
                }
            }

        }
    }


    /**
     * 初始化节次
     *
     * @param maxClassNum 本周有课的最大节次，9.10节没课则隐藏
     */
    private void initClassNum(int maxClassNum) {
//        LinearLayout layout = ((LinearLayout) ((LinearLayout) mScheduleVertLayout.getChildAt(0)).getChildAt(0));
        LinearLayout layout = (LinearLayout) mScheduleVertLayout.getChildAt(0);
        String[] time;
        if (EduSchedule.isSummerTimetable()) {
            time = EduSchedule.SUMMMER_TIME;
        } else {
            time = EduSchedule.WINTER_TIME;
        }
        for (int i = 0; i < layout.getChildCount(); i++) {
            LinearLayout item = (LinearLayout) layout.getChildAt(i);
            AppCompatTextView classNum = (AppCompatTextView) (item.getChildAt(0));
            AppCompatTextView when = (AppCompatTextView) (item.getChildAt(1));

            classNum.setText(i + 1 + "");
            when.setText(time[i]);
            if ((i + 1) > maxClassNum * 2) {
                item.setVisibility(View.GONE);
            } else {
                item.setVisibility(View.VISIBLE);
            }
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
                initSpinner();
                initSchedule(EduSchedule.getScheduleWeek());
                ToastUtil.showShort(getContext(), getString(R.string.update_schedule_success));
                break;
            case Event.EDUCATION_SWITCH_SCHEDULE_FAIL:
                dismissWaitDialog();
                ToastUtil.showShort(getContext(), obtainScheduleFail);
                Logger.d("切换课表失败");
                break;
            case Event.SWITCH_SCHEDULE_NO_COURSE:
                dismissWaitDialog();
                initSpinner();
                initSchedule(EduSchedule.getScheduleWeek());
                ToastUtil.showShort(getContext(), getString(R.string.this_term_no_course));
                break;
            case Event.NOTIFY_UPDATE:
                Logger.d("收到 通知：" + Notification.getLatestNotifyCode());
                if (mBadgeActionProvider != null) {
                    mBadgeActionProvider.setTextInt(BadgeActionProvider.getUnreadCount());
                }
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Logger.d("Schedule onCreateOptionMenu");
        inflater.inflate(R.menu.menu_schedule, menu);
        MenuItem menuItem = menu.findItem(R.id.action_notification);
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
     * 设置作息时间
     */
    private void showSetTimetable() {
        final int[] select = new int[1];
        final int choose;
        if (EduSchedule.isSummerTimetable()) {
            choose = 0;
        } else {
            choose = 1;
        }
        String[] timetables = {getString(R.string.summer), getString(R.string.winter)};
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.timetable));
        builder.setSingleChoiceItems(timetables,
                choose,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select[0] = which;
                    }
                });
        builder.setPositiveButton(confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (select[0] == 0) {
                    EduSchedule.setSummerTimebable(true);
                } else {
                    EduSchedule.setSummerTimebable(false);
                }
                if (choose != select[0]) {
                    initSchedule(mSelectedWeek);
                }
                ToastUtil.showShort(getContext(), getString(R.string.timetable_setting_success));
                dialog.dismiss();
            }
        });

        builder.create().show();
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
                dialog.dismiss();
                //设置保存时的日期
                if (select[0] == 0) {
                    select[0] = EduSchedule.getScheduleWeek();
                }
                EduSchedule.saveScheduleWeek(select[0]);
                EduSchedule.setCurrentWeekDate();
                initSpinner();
                initSchedule(EduSchedule.getScheduleWeek());
                ToastUtil.showShort(getContext(),setSuccess);
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
        final List ayList = new ArrayList();

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


                List<Course> courses = DataSupport.where(SQL_SWITCH_REFRESH_SCHEDULE_WITH_CURRENT_TERM_WHERE, mAcademicYear, mTerm).find(Course.class);
                //从数据库读取课表数据
                if (courses.size() > 0) {
                    Event.sendEmptyMessage(Event.EDUCATION_SWITCH_SCHEDULE_SUCCESS);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
