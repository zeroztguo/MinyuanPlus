package com.csmy.minyuanplus.ui.view;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.education.EduSchedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Zero on 16/6/13.
 */
public class CourseLayout extends ViewGroup {
    private int width;
    private int height;

    private int[] mColorArray = getResources().getIntArray(R.array.material_design_color);
    private List<Integer> mColors;


    private Context mContext;
    private LayoutInflater mInflater;

    private static final String TAG = "CourseLayout";
//    private int weekDaysNum = 0;
    private static  int week_days_num = 7;
    private static  int sum_class_num = 5;

    public CourseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);

        mColors = new ArrayList<Integer>();

        for(int i = 0;i<mColorArray.length;i++){
            mColors.add(mColorArray[i]);
        }
    }

    public CourseLayout(Context context) {
        this(context,null);
    }

    public CourseLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    public void addCourseViews(List<CourseView> courseViews){
        removeAllViews();
        Collections.shuffle(mColors);
        week_days_num = EduSchedule.getDaysHaveClass();
        sum_class_num = EduSchedule.getNumOfClass();
        for(CourseView cv:courseViews){
            Log.i(TAG, "addCourseViews,name:"+cv.getCourseName()+" id:"+cv.getId());
            final String name = cv.getCourseName();
            final String classroom = cv.getClassroom();
            final String teacher = cv.getTeacher();
            final String classInfo = cv.getBeginClass()+"-"+cv.getEndClass()+"节"+"("+cv.getBeginWeek()+"-"+cv.getEndWeek()+"周)";

            cv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCourseInfoDialog(name,classroom,teacher,classInfo);
                }
            });
            addView(cv);
        }
        requestLayout();
        invalidate();
    }



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for(int i = 0;i<getChildCount();i++){
            CourseView child = (CourseView) this.getChildAt(i);

            String courseName = child.getCourseName();
            String classroom = child.getClassroom();
            int day = child.getDay();
            int beginClass = child.getBeginClass();
            int endClass = child.getEndClass();
            int id = child.getId();

            int cl = width*(day-1)/week_days_num;
            int ct = height*((beginClass-1)/2)/sum_class_num;
            int cr = width*day/week_days_num;
            int cb = height*endClass/2/sum_class_num;
            child.layout(cl,ct,cr,cb);

            float maxCardElevation = child.getMaxCardElevation();
            double cos45 = Math.cos(Math.toRadians(45));
            float cornerRadius = child.getRadius();
            int paddingLR = (int) (maxCardElevation+ (1-cos45)*cornerRadius);
            int paddingTB = (int) (maxCardElevation*1.5+(1-cos45)*cornerRadius);

            TextView tv  = new TextView(mContext);
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setText(courseName+"\n"+"@"+classroom);
            tv.layout(paddingLR,paddingTB,cr-paddingLR,cb-paddingTB);
            tv.layout(paddingLR,paddingTB,cr-cl,cb-ct);

            child.addView(tv);
//            child.setContentPadding(paddingLR,paddingTB,paddingLR,paddingTB);
            child.setCardBackgroundColor(mColors.get(id));
        }

    }

    private void showCourseInfoDialog(String courseName,String classroom,String teacher,String classNum){
        View dialog = mInflater.inflate(R.layout.dialog_course,(ViewGroup) findViewById(R.id.id_dialog_course));
        TextView nameTV = (TextView) dialog.findViewById(R.id.id_dialog_course_name);
        TextView classroomTV = (TextView) dialog.findViewById(R.id.id_dialog_course_classroom);
        TextView teacherTV = (TextView) dialog.findViewById(R.id.id_dialog_course_teacher);
        TextView classNumTV = (TextView) dialog.findViewById(R.id.id_dialog_course_class_num);

        nameTV.setText(courseName);
        classroomTV.setText(classroom);
        teacherTV.setText(teacher);
        classNumTV.setText(classNum);


        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setTitle("课程信息");
        builder.setView(dialog);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }


}
