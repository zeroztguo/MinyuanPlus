package com.csmy.minyuanplus.ui.fragment.education;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.education.EduGrade;
import com.csmy.minyuanplus.education.EduRxVolley;
import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.event.EventTag;
import com.csmy.minyuanplus.model.education.Grade;
import com.csmy.minyuanplus.support.adapter.GradeAdapter;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.UpDownAnimatorAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Zero on 16/7/12.
 */
public class GradeFailFragment extends BaseFragment{
    @Bind(R.id.id_grade_fail_csv)
    CardStackView mCardStackView;
    @Bind(R.id.id_query_grade_fail_btn)
    AppCompatButton mQueryGradeFailBtn;


    GradeAdapter mStackAdapter;
    private List<Map<String,String>> mDatas;




    public static GradeFailFragment newInstance(){return new GradeFailFragment();}

    @Override
    protected void initView(View view, Bundle saveInstanceState) {
        mStackAdapter = new GradeAdapter(getContext());
        mCardStackView.setAdapter(mStackAdapter);
        mCardStackView.setAnimatorAdapter(new UpDownAnimatorAdapter(mCardStackView));
    }

    @OnClick(R.id.id_query_grade_fail_btn)
    void doClick(){
        queryGradeFail();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {
        switch (eventModel.getEventCode()){
            case Event.EDUCATION_QUERY_GRADE_FAIL_SUCCESS:
                dismissWaitDialog();
                mDatas = new ArrayList<>();
                mQueryGradeFailBtn.setVisibility(View.GONE);

                List<Grade> gradeList =  EduGrade.getGradeFail();
                if(gradeList.size()>0){
                    for (Grade grade : gradeList) {
                        Map<String,String> data = new HashMap<>();
                        data.put(GradeAdapter.KEY_TITLE,grade.getCourseName() + "\n" + grade.getLevel());
                        data.put(GradeAdapter.KEY_CONTENT,
                                grade.getCourseName() + "(" + grade.getCourseType() + ")" + "\n" +
                                        "学分：" + grade.getGpa() + "\n" +
                                        "成绩：" + grade.getLevel() + "\n");
                        mDatas.add(data);
                    }
                }else{
                    Map<String,String> data = new HashMap<>();
                    data.put(GradeAdapter.KEY_TITLE,"你至今没有未通过的课程！");
                    data.put(GradeAdapter.KEY_CONTENT,"不错不错，前途一片光明");
                    mDatas.add(data);
                }

                mStackAdapter.updateData(mDatas);
                break;
            case Event.EDUCATION_QUERY_GRADE_FAIL_FAIL:
                dismissWaitDialog();
                ToastUtil.show("查询未通过成绩失败，请再试一次...");
                break;
        }
    }

    /**
     * 查询未通过成绩
     */
    private void queryGradeFail(){
        EventTag.saveCurrentTag(EventTag.QUERY_GRADE_FAIL);
        EduRxVolley.enterEducationHome();
        showWaitDialog();
    }



    @Override
    protected int getLayoutId() {
        return R.layout.fragment_grade_fail;
    }
}
