package com.csmy.minyuanplus.model.education;

import org.litepal.crud.DataSupport;

/**
 * Created by Zero on 16/5/31.
 */
public class Course extends DataSupport {

    private int id;
    //课程名
    private String courseName;
    //授课老师
    private String teacher;
    //上课教室
    private String classroom;
    //上课时间:起始周
    private int beginWeek;
    //上课时间:结束周
    private int endWeek;
    //上课时间:哪一天(周几)
    private int day;
    //上课时间:第几节课开始
    private int beginClass;
    //上课时间:第几节课结束
    private int endClass;
    //学年
    private String academicYear;
    //学期
    private String term;

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

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public int getBeginWeek() {
        return beginWeek;
    }

    public void setBeginWeek(int beginWeek) {
        this.beginWeek = beginWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getBeginClass() {
        return beginClass;
    }

    public void setBeginClass(int beginClass) {
        this.beginClass = beginClass;
    }

    public int getEndClass() {
        return endClass;
    }

    public void setEndClass(int endClass) {

        this.endClass = endClass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Course{" +
                "courseName='" + courseName + '\'' +
                ", teacher='" + teacher + '\'' +
                ", classroom='" + classroom + '\'' +
                ", beginWeek=" + beginWeek +
                ", endWeek=" + endWeek +
                ", day=" + day +
                ", beginClass=" + beginClass +
                ", endClass=" + endClass +
                ", academicYear='" + academicYear + '\'' +
                ", term='" + term + '\'' +
                '}';
    }
}
