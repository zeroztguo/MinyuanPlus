package com.csmy.minyuanplus.ui.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.ui.activity.MapActivity;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 课程视图
 * Created by Zero on 16/6/13.
 */
public class CourseLayout extends ViewGroup {
    private int width;
    private int height;

    private int[] mColorArray = getResources().getIntArray(R.array.material_design_color);
    private List<Integer> mColors;
    private List<CourseView> mCourseViewList;


    private Context mContext;
    private LayoutInflater mInflater;

    private int mDayHaveClass = 7;
    private int mNumOfClass = 5;

    public CourseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);

        mColors = new ArrayList<>();

        for (int i = 0; i < mColorArray.length; i++) {
            mColors.add(mColorArray[i]);
        }
        mColorArray = null;
    }


    public CourseLayout(Context context) {
        this(context, null);
    }

    public CourseLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getSize(widthMeasureSpec) > 0) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        if (MeasureSpec.getSize(heightMeasureSpec) > 0) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
        Logger.d("measure width:" + width + "  measure height:" + height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);


        requestLayout();
        Logger.d("on size changed:" + w + " " + h);
    }

    public void addCourseViews(List<CourseView> courseViews, int dayHaveClass, int numOfClass) {
        mDayHaveClass = dayHaveClass;
        mNumOfClass = numOfClass;

        removeAllViews();
        Collections.shuffle(mColors);
        mCourseViewList = courseViews;
        for (CourseView cv : courseViews) {
            final String name = cv.getCourseName();
            final String classroom = cv.getClassroom();
            final String teacher = cv.getTeacher();
            final String classInfo = cv.getBeginClass() + "-" + cv.getEndClass() + "节" + "(" + cv.getBeginWeek() + "-" + cv.getEndWeek() + "周)";

            cv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCourseInfoDialog(name, classroom, teacher, classInfo);
                }
            });


            cv.setCardBackgroundColor(mColors.get(cv.getId()));


            TextView tv = new TextView(mContext);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2
                    , mContext.getResources().getDisplayMetrics());
            tv.setPadding(padding, padding, padding, padding);
            tv.setTextColor(mContext.getResources().getColor(R.color.white));
            tv.setText(cv.getCourseName() + "\n" + "@" + cv.getClassroom());
            cv.addView(tv);

            addView(cv);
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        for (int i = 0; i < getChildCount(); i++) {

            CourseView cv = (CourseView) getChildAt(i);
            int day = cv.getDay();
            int beginClass = cv.getBeginClass();
            int endClass = cv.getEndClass();

            int cl = width * (day - 1) / mDayHaveClass;
            int ct = height * ((beginClass - 1) / 2) / mNumOfClass;
            int cr = width * day / mDayHaveClass;
            int cb = height * endClass / 2 / mNumOfClass;
            cv.layout(cl, ct, cr, cb);

            float maxCardElevation = cv.getMaxCardElevation();
            double cos45 = Math.cos(Math.toRadians(45));
            float cornerRadius = cv.getRadius();
            int paddingLR = (int) (maxCardElevation + (1 - cos45) * cornerRadius);
            int paddingTB = (int) (maxCardElevation * 1.5 + (1 - cos45) * cornerRadius);

            cv.getChildAt(0).layout(paddingLR, paddingTB, cr - cl, cb - ct);

        }
//        for (int i = 0; i < getChildCount(); i++) {
//            CourseView child = (CourseView) this.getChildAt(i);
//
//            String courseName = child.getCourseName();
//            String classroom = child.getClassroom();
//            int day = child.getDay();
//            int beginClass = child.getBeginClass();
//            int endClass = child.getEndClass();
//            int id = child.getId();
//
//            int cl = width * (day - 1) / dayHaveClass;
//            int ct = height * ((beginClass - 1) / 2) / numOfClass;
//            int cr = width * day / dayHaveClass;
//            int cb = height * endClass / 2 / numOfClass;
//            child.layout(cl, ct, cr, cb);
//
//            float maxCardElevation = child.getMaxCardElevation();
//            double cos45 = Math.cos(Math.toRadians(45));
//            float cornerRadius = child.getRadius();
//            int paddingLR = (int) (maxCardElevation + (1 - cos45) * cornerRadius);
//            int paddingTB = (int) (maxCardElevation * 1.5 + (1 - cos45) * cornerRadius);
//
//            TextView tv = new TextView(mContext);
//            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//            int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2
//                    , mContext.getResources().getDisplayMetrics());
//            tv.setPadding(padding, padding, padding, padding);
//            tv.setTextColor(mContext.getResources().getColor(R.color.white));
//            tv.setText(courseName + "\n" + "@" + classroom);
//            tv.layout(paddingLR, paddingTB, cr - paddingLR, cb - paddingTB);
//            tv.layout(paddingLR, paddingTB, cr - cl, cb - ct);
//
//            child.addView(tv);
//            child.setCardBackgroundColor(mColors.get(id));
//        }
//

    }

    private void showCourseInfoDialog(String courseName, String classroom, String teacher, String classNum) {
        View dialog = mInflater.inflate(R.layout.dialog_course, (ViewGroup) findViewById(R.id.id_dialog_course));
        TextView nameTV = (TextView) dialog.findViewById(R.id.id_dialog_course_name);
        TextView classroomTV = (TextView) dialog.findViewById(R.id.id_dialog_course_classroom);
        TextView teacherTV = (TextView) dialog.findViewById(R.id.id_dialog_course_teacher);
        TextView classNumTV = (TextView) dialog.findViewById(R.id.id_dialog_course_class_num);

        nameTV.setText(courseName);
        classroomTV.setText(classroom);
        teacherTV.setText(teacher);
        classNumTV.setText(classNum);


        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setTitle(getContext().getString(R.string.course_info));
//        暂时取消民院地图功能，导致app卡顿
        builder.setNeutralButton(mContext.getString(R.string.map), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(mContext, MapActivity.class);
                mContext.startActivity(intent);
            }
        });

        builder.setView(dialog);
        builder.setPositiveButton(getContext().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

}
