package com.csmy.minyuanplus.support.education;

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
 * 教务系统课表的帮助类
 * Created by Zero on 16/5/31.
 */
public class EduSchedule extends Util {
    public static final String SCHEDULE = "schedule";
    public static final String SCHEDULE_WEEK = "schedule_week";
    public static final String CURRENT_MONDAY_OF_WEEK_FORMAT_DATE = "current_monday_week_format_date";
    //是否夏季作息时间
    public static final String IS_SUMMMER_TIMETABLE = "is_summer_timetable";
    //课表默认周数
    private static final int DEFAULT_SCHEDULE_WEEK = 2;
    //课表默认有课的天数，最少显示到星期五
    public static final int DEFAULT_DAYS_HAVE_CLASS = 5;
    //课表默认的最大节次，最少显示到8节
    public static final int DEFAULT_NUM_OF_CLASS = 4;
    //设置当前周时的日期
    public static final String SET_CURRENT_WEEK_DATE = "set_current_week_date";
    private static final String TAG = "ScheduleUtil";
    //夏季作息，上课时间
    public static final String[] SUMMMER_TIME = {"8:10", "9:05", "10:15", "11:10", "14:40", "15:35", "16:40", "17:35", "7.30", "20:25"};
    //冬季作息，上课时间
    public static final String[] WINTER_TIME = {"8:10", "9:05", "10:15", "11:10", "14:10", "15:05", "16:10", "17:05", "7.30", "20:25"};


    public static void setSummerTimebable(boolean flag) {
        SPUtil.put(IS_SUMMMER_TIMETABLE, flag);
    }

    /**
     * @return 是否夏季作息时间
     */
    public static boolean isSummerTimetable() {
        return (boolean) SPUtil.get(IS_SUMMMER_TIMETABLE, true);
    }

    /**
     * 保存设置当前周时的日期，格式如：2016-9-5
     */
    public static void setCurrentWeekDate() {
        SPUtil.put(SET_CURRENT_WEEK_DATE, Util.getCurrentDate());
    }

    /**
     * @return 设置当前周时的日期，如果没有返回当天的日期
     */
    public static String getSettingCurrentWeekDate() {
        return (String) SPUtil.get(SET_CURRENT_WEEK_DATE, Util.getCurrentDate());
    }


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


    public static List<Course> getEducationScheduleCourses(boolean isSaveGradeInfo) {
        Document doc = Jsoup.parse(EduSchedule.getScheduleHtml());

        String academicYear = null;
        String term = null;

        Elements xnd = doc.select("select#xnd");
        Elements xnds = xnd.select("option[selected=selected]");
        Elements xqd = doc.select("select#xqd");
        Elements xqds = xqd.select("option[selected=selected]");
        academicYear = xnds.get(0).text();
        term = xqds.get(0).text();
        if (isSaveGradeInfo) {
            EduInfo.saveCurrentAcademicYear(academicYear);
            EduInfo.saveCurrentTerm(term);
        }
        /*
          把页面上课程信息存入集合
         */
        List<Course> courseList = new ArrayList<>();

        /*
         * 起始和结束节次
         */
        int commonBeginClass = 0;
        int commonEndClass;
        /*
        课程表
         */
        Elements elements = doc.select("table#Table1");

        for (int i = 0; i < elements.size(); i++) {
            /*
            课表每大行
             */
            Elements trs = elements.get(0).select("tr");

            for (int i1 = 2; i1 < trs.size(); i1++) {
                /*
                第一列
                 */
                String firstTd = trs.get(i1).select("td").get(0).text().trim();
                /*
                第二列
                 */
                String secondTd = trs.get(i1).select("td").get(1).text().trim();
                /*
                如果第一列是节次，则解析并保存
                */
                if (firstTd.indexOf("第") == 0) {
                    commonBeginClass = Util.numberString2int(firstTd.substring(1, 2));
                } else {
                    commonBeginClass = Util.numberString2int(secondTd.substring(1, 2));
                }

                /*
                每列的课程信息
                 */
                Elements tds = trs.get(i1).select("td");
                for (int i3 = 0; i3 < tds.size(); i3++) {

                    /*
                    具体某门课的课程信息字符串，待解析
                     */
                    String courseStr = tds.get(i3).toString();

                    Logger.d(i3 + ":" + courseStr.toString());
                    if (courseStr.contains("align=\"Center\" rowspan=\"2\"")) {

                        String[] courses = courseStr.split("<br><br><br><br>");
                        for (int i2 = 0; i2 < courses.length; i2++) {
                            courseStr = courses[i2];

                    /*
                    课程信息每个部分集合
                     */
                            String[] coursePart = courseStr.split("<br>");
                    /*
                    课程实体
                     */
                            Course course = new Course();
                    /*
                    课程名
                     */
                            if (coursePart[0].trim().contains("align=\"Center\" rowspan=\"2\"")) {
                                course.setCourseName(coursePart[0].trim().split(">")[1]);
                            } else {
                                course.setCourseName(coursePart[0].trim());
                            }
                    /*
                    教师
                     */
                            course.setTeacher(coursePart[2].trim());
                    /*
                    教室
                     */
                            course.setClassroom(coursePart[3].trim());

                    /*
                    解析周几，节次，周次
                     */
                            String detail = coursePart[1].trim();
                     /*
                     非普通情况，比如 院长讲坛 ,{第12-12周|2节/周} ,刘晓,影视厅01
                     */
                            if (detail.indexOf("{") == 0) {
                            /*
                            周几
                            */
                                course.setDay(i3 - 1);

                                String[] d1 = detail.split("周\\|");
                                String[] d2 = d1[0].split("第")[1].split("-");

                            /*
                            起始和结束周次
                            */
                                course.setBeginWeek(Integer.valueOf(d2[0]));
                                course.setEndWeek(Integer.valueOf(d2[1]));

                            /*
                            起始和结束节次
                            */
                                course.setBeginClass(commonBeginClass);
                                commonEndClass = commonBeginClass + Integer.valueOf(d1[1].substring(0, 1)) - 1;
                                course.setEndClass(commonEndClass);

                            }
                    /*
                    普通情况，比如 软件技术基础，周四第1,2节{第8-19周}，刘淳，S19-210
                    */
                            else {
                        /*
                        周几
                         */
                                course.setDay(Util.numberString2int(detail.substring(1, 2)));

                                String[] d1 = detail.split("节\\{第");
                                String[] d2 = d1[0].split("第")[1].split(",");
                                String[] d3 = d1[1].split("周")[0].split("-");
                        /*
                        起始和结束节次
                         */
                                course.setBeginClass(Integer.valueOf(d2[0]));
                                course.setEndClass(Integer.valueOf(d2[1]));
                        /*
                        起始和结束周次
                         */
                                course.setBeginWeek(Integer.valueOf(d3[0]));
                                course.setEndWeek(Integer.valueOf(d3[1]));

                            }
                            course.setAcademicYear(academicYear);
                            course.setTerm(term);
                            courseList.add(course);
                        }
                    }

                }

            }


        }

        return courseList;
    }


    @NonNull
    public static List<Course> getEducationScheduleCourses2(boolean isSaveGradeInfo) {
        Document doc = Jsoup.parse(EduSchedule.getScheduleHtml());

        String academicYear = null;
        String term = null;

        Elements xnd = doc.select("select#xnd");
        Elements xnds = xnd.select("option[selected=selected]");
        Elements xqd = doc.select("select#xqd");
        Elements xqds = xqd.select("option[selected=selected]");
        academicYear = xnds.get(0).text();
        term = xqds.get(0).text();
        if (isSaveGradeInfo) {
            EduInfo.saveCurrentAcademicYear(academicYear);
            EduInfo.saveCurrentTerm(term);
        }


        /**
         * 把页面上课程信息存入集合
         */
        List<Course> courseList = new ArrayList<>();
//        Elements elements = doc.select("[align=Center][rowspan=2]");
        Elements elements = doc.select("table#Table1");
        for (int j = 0; j < elements.size(); j++) {
            Element e = elements.get(j);
            String courseStr = e.toString();
            if (j > 1) {
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
                            /*
                            非普通情况，比如 院长讲坛 ,{第12-12周|2节/周} ,刘晓,影视厅01
                             */
                            if (courseInfo.indexOf("{") == 0) {

                            }
                            /*
                            普通情况，比如 软件技术基础，周四第1,2节{第8-19周}，刘淳，S19-210
                             */
                            else {
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
                            }
                        } else if (i == 2) {
                            course.setTeacher(courseInfo);
                        } else if (i == 3) {
                            course.setClassroom(courseInfo);
                        }
                    }
                    course.setAcademicYear(academicYear);
                    course.setTerm(term);
                    courseList.add(course);
                }
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

//    /**
//     * @param days 课上到周几
//     */
//    public static void saveDaysHaveClass(int days) {
//        SPUtil.put(DAYS_HAVE_CLASS, days);
//    }
//
//    /**
//     * @return 课上到周几
//     */
//    public static int getDaysHaveClass() {
//        return (int) SPUtil.get(DAYS_HAVE_CLASS, DEFAULT_DAYS_HAVE_CLASS);
//    }
//
//    /**
//     * @param classNum 有课的节次
//     */
//    public static void saveNumOfClass(int classNum) {
//        SPUtil.put(NUM_OF_CLASS, classNum);
//    }
//
//    /**
//     * @return 有课的节次
//     */
//    public static int getNumOfClass() {
//        return (int) SPUtil.get(NUM_OF_CLASS, DEFAULT_NUM_OF_CLASS);
//    }


}
