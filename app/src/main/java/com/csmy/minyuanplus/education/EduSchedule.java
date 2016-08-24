package com.csmy.minyuanplus.education;

import android.support.annotation.NonNull;
import android.util.Log;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.model.education.Course;
import com.csmy.minyuanplus.support.util.SPUtil;
import com.csmy.minyuanplus.support.util.Util;
import com.orhanobut.logger.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Zero on 16/5/31.
 */
public class EduSchedule extends Util {
    public static final String SCHEDULE = "schedule";
    public static final String SCHEDULE_WEEK = "schedule_week";
    public static final String DAYS_HAVE_CLASS = "days_have_class";
    public static final String NUM_OF_CLASS = "num_of_class";

    public static final String CURRENT_MONDAY_OF_WEEK_FORMAT_DATE = "current_monday_week_format_date";

    private static final int DEFAULT_SCHEDULE_WEEK = 2;
    public static final int DEFAULT_DAYS_HAVE_CLASS = 5;
    public static final int DEFAULT_NUM_OF_CLASS = 5;

    private static final String TAG = "ScheduleUtil";


    /**
     * 保存课表html远吗
     */
    public static void saveScheduleHtml(String schedule) {
        SPUtil.put(SCHEDULE, schedule);
    }

    /**
     * 获取课表html源码
     *
     * @return
     */
    public static String getScheduleHtml() {
        return SPUtil.get(SCHEDULE, SCHEDULE).toString();
    }

    /**
     * 格式化周几 字符转整型
     *
     * @param week 周几字符
     * @return 周几整型
     */
    public static int weekStringToInteger(String week) {
        String[] weeks = context.getResources().getStringArray(R.array.week);
        for (int i = 0; i < weeks.length; i++) {
            if (week.equals(weeks[i])) {
                if (i == 0) {
                    return 7;
                }
                return i;
            }
        }
        return 1;
    }

    /**
     * 格式化周几 整型转字符
     *
     * @param weekNum 周几整型
     * @return 周几字符
     */
    public static String weekIntToString(int weekNum) {
        if (weekNum == 1) return "一";
        else if (weekNum == 2) return "二";
        else if (weekNum == 3) return "三";
        else if (weekNum == 4) return "四";
        else if (weekNum == 5) return "五";
        else if (weekNum == 6) return "六";
        else if (weekNum == 7) return "日";

        return "";
    }


    @NonNull
    public static List<Course> getEducationScheduleCourses(boolean isSaveGradeInfo) {
        Document doc = Jsoup.parse(EduSchedule.getScheduleHtml());

        String academicYear = null;
        String term = null;

        Elements xnd = doc.select("select#xnd");
        Elements xnds = xnd.select("option");
        for (Element e : xnds) {
            if (e.hasAttr("selected")) {
                academicYear = e.text();
                if (isSaveGradeInfo) {
                    Log.i(TAG, "academicYear save before: " + EduInfo.getCurrentAcademicYear());
                    EduInfo.saveCurrentAcademicYear(academicYear);
                    Log.i(TAG, "academicYear save after: " + EduInfo.getCurrentAcademicYear());

                }
            }
        }

        Elements xqd = doc.select("select#xqd");
        Elements xqds = xqd.select("option");
        for (Element e : xqds) {
            if (e.hasAttr("selected")) {
                term = e.text();
                if (isSaveGradeInfo) {
                    EduInfo.saveCurrentTerm(term);
                }
            }
        }

        /**
         * 把页面上课程信息存入集合
         */
        List<Course> courseList = new ArrayList<Course>();
        Elements elements = doc.select("[align=Center][rowspan=2]");
        for (Element e : elements) {
            String courseStr = e.toString();
            String[] couresArray = courseStr.split("<br><br><br><br>");
            for (String c : couresArray) {
                String[] courseInfoArray = c.split("<br>");
                Course course = new Course();
                for (int i = 0; i < courseInfoArray.length; i++) {
                    String courseInfo = courseInfoArray[i];
                    if (i == 0) {
                        if (courseInfo.contains("rowspan")) {
                            String[] courseInfoPart = courseInfo.split(">");
                            course.setCourseName(courseInfoPart[1]);
                        } else {
                            course.setCourseName(courseInfo);
                        }
                        Log.i(TAG, course.getCourseName());
                    } else if (i == 1) {
                        String[] strArray1 = courseInfo.split("\\{第");
                        String[] strArray2 = strArray1[0].split(",");
                        int day = EduSchedule.weekStringToInteger(strArray2[0].substring(1, 2));  //这里应转换成数字
                        int beginClass = Integer.valueOf(strArray2[0].substring(3, 4));
                        int endClass = Integer.valueOf(strArray2[1].split("节")[0]);
                        int beginWeek = Integer.valueOf(strArray1[1].split("-")[0]);
                        int endWeek = Integer.valueOf(strArray1[1].split("-")[1].split("周")[0]);
                        course.setDay(day);
                        course.setBeginClass(beginClass);
                        course.setEndClass(endClass);
                        course.setBeginWeek(beginWeek);
                        course.setEndWeek(endWeek);
                        Logger.d("星期" + course.getDay() + "节次" + course.getBeginClass() + "," + course.getEndClass() + "周次" + course.getBeginWeek() + "-" + course.getEndWeek());
                    } else if (i == 2) {
                        course.setTeacher(courseInfo);
                        Log.i(TAG, course.getTeacher());
                    } else if (i == 3) {
                        course.setClassroom(courseInfo);
                        Log.i(TAG, course.getClassroom());
                    }
                }
                course.setAcademicYear(academicYear);
                course.setTerm(term);
                courseList.add(course);
            }
        }
        return courseList;

    }

    /**
     * @return 第多少周
     */
    public static int getScheduleWeek() {
        return (int) SPUtil.get(SCHEDULE_WEEK, DEFAULT_SCHEDULE_WEEK);
    }

    /**
     * @param week 第多少周
     */
    public static void saveScheduleWeek(int week) {
        SPUtil.put(SCHEDULE_WEEK, week);
        SPUtil.put(CURRENT_MONDAY_OF_WEEK_FORMAT_DATE, getCurrentMondayWeekformatDate());
    }

    public static int getCmdFormatDate() {
        return (int) SPUtil.get(CURRENT_MONDAY_OF_WEEK_FORMAT_DATE, getCurrentMondayWeekformatDate());
    }

    /**
     * @return 当前周的星期一格式化后的日期
     */
    public static int getCurrentMondayWeekformatDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(date);
        String[] dateInfo = dateStr.split("-");

//		System.out.println(dateStr);
        int year = Integer.valueOf(dateInfo[0]).intValue();
        int month = Integer.valueOf(dateInfo[1]).intValue();
        int day = Integer.valueOf(dateInfo[2]).intValue();

        System.out.println(year + "-" + month + "-" + day);
        int weekDay = dayForWeek(date);
//        System.out.println("今天星期"+weekDay);
        int fDay = formatDayOfYear(year, month, day);
//        System.out.println("格式化后的日期:"+fDay);
        int cmdDay = curretMondayOfWeekToDayOfYear(fDay, weekDay);
        return cmdDay;
//        System.out.println("当周周一格式化后日期:"+ cmdDay);
    }

    public static int splitWeek(String week) {
        return Integer.valueOf(week.split("第")[1].split("周")[0]);
    }

    /**
     * @param days 课上到周几
     */
    public static void saveDaysHaveClass(int days) {
        SPUtil.put(DAYS_HAVE_CLASS, days);
    }

    /**
     * @return 课上到周几
     */
    public static int getDaysHaveClass() {
        return (int) SPUtil.get(DAYS_HAVE_CLASS, DEFAULT_DAYS_HAVE_CLASS);
    }

    /**
     * @param classNum 有课的节次
     */
    public static void saveNumOfClass(int classNum) {
        SPUtil.put(NUM_OF_CLASS, classNum);
    }

    /**
     * @return 有课的节次
     */
    public static int getNumOfClass() {
        return (int) SPUtil.get(NUM_OF_CLASS, DEFAULT_NUM_OF_CLASS);
    }


}
