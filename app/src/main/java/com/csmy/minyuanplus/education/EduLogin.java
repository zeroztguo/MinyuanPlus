package com.csmy.minyuanplus.education;

import com.csmy.minyuanplus.model.education.PersonalInfo;
import com.csmy.minyuanplus.support.util.SPUtil;
import com.csmy.minyuanplus.support.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * 登录教务系统的工具类
 * Created by Zero on 16/5/28.
 */
public class EduLogin extends Util {
    public static final String IS_EDUCATION_LOGIN = "isEducationLogin";

    public static final String PERSONAL_INFO = "personalInfo";

    private static final String TAG = "LoginUtil";


    /**
     * 获取是否登录教务系统
     *
     * @return
     */
    public static boolean isEducationLogined() {
        return (boolean) SPUtil.get(IS_EDUCATION_LOGIN, false);
    }

    /**
     * 设置教务系统登录状态
     *
     * @param isLogin
     */
    public static void setEducationLogin(boolean isLogin) {
        SPUtil.put(IS_EDUCATION_LOGIN, isLogin);
    }


    public static PersonalInfo getPersonalInfo() {
        PersonalInfo pi = new PersonalInfo();

        Document document = Jsoup.parse(getPersonalInfoHtml());
        Elements eName = document.select("span#xm");
        Elements eSex = document.select("span#lbl_xb");
        Elements eClass = document.select("span#lbl_xzb");
        Elements eMajor = document.select("span#lbl_zymc");
        Elements eGrade = document.select("span#lbl_dqszj");

        Elements eNation = document.select("span#lbl_mz");
        Elements ePoliticalStatus = document.select("span#lbl_zzmm");
        Elements eOriginArea = document.select("span#lbl_lydq");
        Elements eOriginProvince = document.select("span#lbl_lys");
        Elements eCollege = document.select("span#lbl_xy");

        pi.setName(eName.text());
        pi.setSex(eSex.text());
        pi.setClassInfo(eClass.text());
        pi.setMajor(eMajor.text());
        pi.setGrade(eGrade.text());

        pi.setNation(eNation.text());
        pi.setPoliticalStatus(ePoliticalStatus.text());
        pi.setOriginArea(eOriginArea.text());
        pi.setOriginProvince(eOriginProvince.text());
        pi.setCollege(eCollege.text());


        return pi;
    }

    /**
     * 保存教务系统个人信息html源码
     *
     * @param personalInfo 源码
     */
    public static void savePersonalInfoHtml(String personalInfo) {
        SPUtil.put(PERSONAL_INFO, personalInfo);
    }

    /**
     * 获取教务系统个人信息html源码
     */
    public static String getPersonalInfoHtml() {
        return SPUtil.get(PERSONAL_INFO, PERSONAL_INFO).toString();
    }


}
