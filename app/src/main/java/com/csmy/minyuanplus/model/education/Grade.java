package com.csmy.minyuanplus.model.education;

/**
 * 成绩实体类
 * Created by Zero on 16/7/2.
 */
public class Grade {
    //学年
    String academicYear;
    //学期
    String term;
    //课程名
    String courseName;
    //课程类型
    String courseType;
    //绩点
    String gpa;
    //平时成绩
    String ordinaryLevel;
    //期末成绩
    String terminalLevel;
    //总成绩
    String level;

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getGpa() {
        return gpa;
    }

    public void setGpa(String gpa) {
        this.gpa = gpa;
    }

    public String getOrdinaryLevel() {
        return ordinaryLevel;
    }

    public void setOrdinaryLevel(String ordinaryLevel) {
        this.ordinaryLevel = ordinaryLevel;
    }

    public String getTerminalLevel() {
        return terminalLevel;
    }

    public void setTerminalLevel(String terminalLevel) {
        this.terminalLevel = terminalLevel;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }
}
