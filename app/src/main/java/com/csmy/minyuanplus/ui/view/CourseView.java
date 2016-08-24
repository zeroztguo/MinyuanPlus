package com.csmy.minyuanplus.ui.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;

/**
 * Created by Zero on 16/6/13.
 */
public class CourseView extends CardView {
    private String courseName;
    private String classroom;
    private String teacher;
    private int day;
    private int beginClass;
    private int endClass;
    private int beginWeek;
    private int endWeek;

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

    private int id;


    private Context mContext;
    private LayoutInflater mInflater;

    private static final String TAG = "CourseView";


    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
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

    public CourseView(Context context) {
        this(context, null);
    }

    public CourseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CourseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.setUseCompatPadding(true);
        this.setCardElevation(0);

        mInflater = LayoutInflater.from(context);
    }
}
