package com.csmy.minyuanplus.support.education;

import com.csmy.minyuanplus.model.education.PersonalInfo;
import com.csmy.minyuanplus.support.util.SPUtil;
import com.orhanobut.logger.Logger;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 教务系统个人信息的帮助类
 * Created by Zero on 16/7/2.
 */
public class EduInfo {
    public static final String COOKIE = "cookie";
    public static final String SAVE_COOKIE_DATE = "saveCookieDate";
    public static final String EDUCATION_USER_NAME = "educationUserName";
    public static final String EDUCATION_PASSWORD = "educationPassword";
    public static final String CURRENT_ACADEMIC_YEAR = "current_academic_year";
    public static final String CURRENT_TERM = "current_term";


    public static String getSchoolYear(String academicYear) {
        PersonalInfo pi = DataSupport.findAll(PersonalInfo.class).get(0);
        int ay = Integer.valueOf(academicYear.split("-")[0]);
        int grade = Integer.valueOf(pi.getGrade());
        if (ay == grade) {
            return "大一";
        } else if (ay == grade + 1) {
            return "大二";
        }
        return "大三";
    }

    /**
     * @return cookie是否可用
     */
    public static boolean isCookieAvaliable() {
        if (getCookie().equals(COOKIE)) {
            Logger.d("cookie不可用");
            return false;
        } else {
            SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            long between = 0;
            try {
                Date begin = dfs.parse(getObtainCookieDate());
                Date end = dfs.parse(dfs.format(new Date()));
                between = end.getTime() - begin.getTime();// 得到两者的毫秒数
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            long day = between / (24 * 60 * 60 * 1000);
            long hour = (between / (60 * 60 * 1000) - day * 24);
            long min = ((between / (60 * 1000)) - day * 24 * 60 - hour * 60);
//            long s = (between / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
//            long ms = (between - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000
//                    - min * 60 * 1000 - s * 1000);
            Logger.d(min);
            if (min > 5) {
                return false;
            }
        }
        Logger.d("cookie可用");
        return true;
    }

    public static void saveObtainCookieDate() {
        Calendar dateBegin = Calendar.getInstance();
        dateBegin.setTime(new Date());
        String bdStr = dateBegin.get(Calendar.YEAR) + "-" + String.valueOf(dateBegin.get(Calendar.MONTH) + 1) + "-" + dateBegin.get(Calendar.DAY_OF_MONTH) + " " + dateBegin.get(Calendar.HOUR) + ":" + dateBegin.get(Calendar.MINUTE);
        SPUtil.put(SAVE_COOKIE_DATE, bdStr);
    }

    public static String getObtainCookieDate() {
        return SPUtil.get(SAVE_COOKIE_DATE, "1970-1-1 00:00").toString();
    }


    /**
     * 保存操作教务系统所需要的cookie
     *
     * @param cookie
     */
    public static void saveCookie(String cookie) {
        SPUtil.put(COOKIE, cookie);
        saveObtainCookieDate();
    }

    /**
     * 获取操作教务系统所需要的cookie
     */
    public static String getCookie() {
        return SPUtil.get(COOKIE, COOKIE).toString();
    }

    public static void saveEducationUserName(String username) {
        SPUtil.put(EDUCATION_USER_NAME, username);
    }

    public static String getEducationUserName() {
        return (String) SPUtil.get(EDUCATION_USER_NAME, "");
    }

    public static void saveEducationPassword(String password) {
        SPUtil.put(EDUCATION_PASSWORD, password);
    }

    public static String getEducationPassword() {
        return (String) SPUtil.get(EDUCATION_PASSWORD, "");
    }

    /**
     * @param currentAcademicYear 当前学年
     */
    public static void saveCurrentAcademicYear(String currentAcademicYear) {
        SPUtil.put(CURRENT_ACADEMIC_YEAR, currentAcademicYear);
    }

    /**
     * @return 当前学年
     */
    public static String getCurrentAcademicYear() {
        return (String) SPUtil.get(CURRENT_ACADEMIC_YEAR, "");
    }

    /**
     * @param currentTerm 当前学期
     */
    public static void saveCurrentTerm(String currentTerm) {
        SPUtil.put(CURRENT_TERM, currentTerm);
    }

    /**
     * @return 当前学期
     */
    public static String getCurrentTerm() {
        return (String) SPUtil.get(CURRENT_TERM, "1");
    }
}
