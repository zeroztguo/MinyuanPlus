package com.csmy.minyuanplus.model.education;

import org.litepal.crud.DataSupport;

/**
 * 个人信息实体，数据库模型
 * Created by Zero on 16/6/26.
 */
public class PersonalInfo extends DataSupport{
    //姓名
    private String name;
    //性别
    private String sex;
    //班级
    private String classInfo;
    //专业
    private String major;
    //年级
    private String grade;
    //政治面貌
    private String politicalStatus;
    //来源地区
    private String originArea;
    //来源省
    private String originProvince;
    //学院
    private String college;
    //民族
    private String nation;

    public String getPoliticalStatus() {
        return politicalStatus;
    }

    public void setPoliticalStatus(String politicalStatus) {
        this.politicalStatus = politicalStatus;
    }

    public String getOriginArea() {
        return originArea;
    }

    public void setOriginArea(String originArea) {
        this.originArea = originArea;
    }

    public String getOriginProvince() {
        return originProvince;
    }

    public void setOriginProvince(String originProvince) {
        this.originProvince = originProvince;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getClassInfo() {
        return classInfo;
    }

    public void setClassInfo(String classInfo) {
        this.classInfo = classInfo;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}
