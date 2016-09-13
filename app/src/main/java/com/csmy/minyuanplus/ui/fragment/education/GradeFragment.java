package com.csmy.minyuanplus.ui.fragment.education;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.LinearLayout;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.event.EventTag;
import com.csmy.minyuanplus.model.education.AcademicYear;
import com.csmy.minyuanplus.model.education.Grade;
import com.csmy.minyuanplus.support.adapter.GradeAdapter;
import com.csmy.minyuanplus.support.education.EduGrade;
import com.csmy.minyuanplus.support.education.EduInfo;
import com.csmy.minyuanplus.support.education.EduRxVolley;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.loopeer.cardstack.CardStackView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Zero on 16/6/28.
 */
public class GradeFragment extends BaseFragment {
    @Bind(R.id.id_grade_csv)
    CardStackView mCardStackView;
    @Bind(R.id.id_grade_spinner)
    MaterialSpinner mSpinner;
    @Bind(R.id.id_query_grade_layout)
    LinearLayout mQueryGradeLayout;
    @Bind(R.id.id_query_grade_fab)
    FloatingActionButton mResetFab;

    private GradeAdapter mStackAdapter;
    private List<Map<String, String>> mDatas;


    public static GradeFragment newInstance() {
        return new GradeFragment();
    }

    @Override
    protected void initView(View view, Bundle saveInstanceState) {
        mStackAdapter = new GradeAdapter(getContext());
        mCardStackView.setAdapter(mStackAdapter);
        initSpinner();

        mResetFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResetFab.setVisibility(View.GONE);
                mCardStackView.setVisibility(View.GONE);
                mQueryGradeLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @OnClick(R.id.id_query_grade_btn)
    void doClick() {
        queryTermGrade();
    }

//    @OnClick(R.id.id_query_grade_fab)
//    void operate() {
//
//    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {
        switch (eventModel.getEventCode()) {
            case Event.EDUCATION_QUERY_GRADE_SUCCESS:
                dismissWaitDialog();
                mQueryGradeLayout.setVisibility(View.GONE);
                mResetFab.setVisibility(View.VISIBLE);
                mCardStackView.setVisibility(View.VISIBLE);
                mDatas = new ArrayList<>();
                List<Grade> gradeList = EduGrade.getGrade();
                if (gradeList.size() > 0) {
                    for (Grade grade : gradeList) {
                        Map<String, String> data = new HashMap<>();
                        data.put(GradeAdapter.KEY_TITLE, grade.getCourseName() + "\n" + grade.getLevel());
                        data.put(GradeAdapter.KEY_CONTENT, grade.getAcademicYear() + getHoldingActivity().getString(R.string.the) + grade.getTerm() + getHoldingActivity().getString(R.string.term) + "\n" +
                                grade.getCourseName() + "(" + grade.getCourseType() + ")" + "\n" +
                                getHoldingActivity().getString(R.string.gpa) + grade.getGpa() + "\n" +
                                getHoldingActivity().getString(R.string.ordinary_level) + grade.getOrdinaryLevel() + "\n" +
                                getHoldingActivity().getString(R.string.terminal_level) + grade.getTerminalLevel() + "\n" +
                                getHoldingActivity().getString(R.string.level) + grade.getLevel() + "\n");
                        mDatas.add(data);
                    }
                } else {
                    Map<String, String> data = new HashMap<>();
                    data.put(GradeAdapter.KEY_TITLE, getHoldingActivity().getString(R.string.no_grade));
                    data.put(GradeAdapter.KEY_CONTENT, getHoldingActivity().getString(R.string.query_other_term));
                    mDatas.add(data);
                }
                mStackAdapter.updateData(mDatas);

                break;
            case Event.EDUCATION_QUERY_GRADE_FAIL:
                dismissWaitDialog();
                ToastUtil.showShort(getContext(), getString(R.string.query_grade_fail_try_again));
                break;
        }
    }

    /**
     * 查询学期成绩
     */
    private void queryTermGrade() {

        EventTag.saveCurrentTag(EventTag.QUERY_GRADE);
        EduRxVolley.enterEducationHome();
        showWaitDialog();
    }


    /**
     * 初始化学期选择器
     */
    private void initSpinner() {
        EduGrade.saveGradeAcadamicYear(EduInfo.getCurrentAcademicYear());
        EduGrade.saveGradeTerm(EduInfo.getCurrentTerm());
        final List ayList = new ArrayList<>();
        //学年集合
        List<AcademicYear> ays = DataSupport.findAll(AcademicYear.class);
        int tag = 0;
        for (int i = 0; i < ays.size(); i++) {
            String ay = ays.get(i).getAcademicYear();
            ayList.add(ay);
            //赋值tag用来初始化spinner选择索引
            if (ay.equals(EduInfo.getCurrentAcademicYear())) {
                tag = i;
            }
        }
        String[] terms = new String[6];

        for (int i = 0; i < 3; i++) {
            terms[2 * i] = EduInfo.getSchoolYear(ayList.get(i) + " ") + getHoldingActivity().getString(R.string.the) + 1 + getHoldingActivity().getString(R.string.term);
            terms[2 * i + 1] = EduInfo.getSchoolYear(ayList.get(i) + " ") + getHoldingActivity().getString(R.string.the) + 2 + getHoldingActivity().getString(R.string.term);
        }
        mSpinner.setItems(terms);
        //设置spinner初始选择索引
        mSpinner.setSelectedIndex(2 * tag - 1 + Integer.valueOf(EduInfo.getCurrentTerm()));
//        mSpinner.setSelectedIndex(0);
        mSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//                view.setBackgroundColor(colorWhite);
                int term = (position) % 2 + 1;
                String gradeAcademicYear;
                String gradeTerm;
                if (term == 2) {
                    gradeAcademicYear = ayList.get((position - 1) / 2).toString().trim();
                } else {
                    gradeAcademicYear = ayList.get((position) / 2).toString().trim();
                }
                gradeTerm = String.valueOf(term).trim();
                EduGrade.saveGradeAcadamicYear(gradeAcademicYear);
                EduGrade.saveGradeTerm(gradeTerm);
            }
        });

    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_grade;
    }
}
