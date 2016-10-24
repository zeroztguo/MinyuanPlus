package com.csmy.minyuanplus.ui.fragment.education;

import android.os.Bundle;
import android.view.View;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.event.EventTag;
import com.csmy.minyuanplus.model.education.AcademicYear;
import com.csmy.minyuanplus.support.adapter.GradeAdapter;
import com.csmy.minyuanplus.support.education.EduGrade;
import com.csmy.minyuanplus.support.education.EduInfo;
import com.csmy.minyuanplus.support.education.EduRxVolley;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.UpDownAnimatorAdapter;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

import static com.csmy.minyuanplus.R.string.term;

/**
 * Created by Zero on 16/9/22.
 */

public class GradeGpaFragment extends BaseFragment {
    @Bind(R.id.id_grade_gpa_spinner)
    MaterialSpinner mSpinner;
    @Bind(R.id.id_grade_gpa_csv)
    CardStackView mCardStackView;
    private GradeAdapter mStackAdapter;
    private List<Map<String,String>> mDatas;

//    @Bind(R.id.id_grade_gpa_ll)
//    AutoLinearLayout mGpaLayout;
//    @Bind(R.id.id_grade_gpa_tv)
//    AppCompatTextView mGpaTextView;
//    @Bind(R.id.id_grade_gpa_prompt_tv)
//    AppCompatTextView mGpaPromptTextView;

    @Override
    protected void initView(View view, Bundle saveInstanceState) {
        mDatas = new ArrayList<>();
        mStackAdapter = new GradeAdapter(getContext());
        mCardStackView.setAdapter(mStackAdapter);
        mCardStackView.setAnimatorAdapter(new UpDownAnimatorAdapter(mCardStackView));
        initSpinner();

    }

    public static GradeGpaFragment newInstance() {
        return new GradeGpaFragment();
    }

    /**
     * 初始化学年和学期选择器
     */
    private void initSpinner() {
        EduGrade.saveGpaAcadamicYear(EduInfo.getCurrentAcademicYear());
        EduGrade.saveGpaTerm(EduInfo.getCurrentTerm());
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
        String[] terms = new String[9];

        for (int i = 0; i < 3; i++) {
            terms[2 * i] = EduInfo.getSchoolYear(ayList.get(i) + " ") + getHoldingActivity().getString(R.string.the) + 1 + getHoldingActivity().getString(term);
            terms[2 * i + 1] = EduInfo.getSchoolYear(ayList.get(i) + " ") + getHoldingActivity().getString(R.string.the) + 2 + getHoldingActivity().getString(term);
        }
        terms[6] = "大一学年";
        terms[7] = "大二学年";
        terms[8] = "大三学年";

        mSpinner.setItems(terms);
        //设置spinner初始选择索引
        mSpinner.setSelectedIndex(2 * tag - 1 + Integer.valueOf(EduInfo.getCurrentTerm()));
//        mSpinner.setSelectedIndex(0);
        mSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//                view.setBackgroundColor(colorWhite);
                String gradeAcademicYear = "";
                String gradeTerm;
                if (position < 6) {
                    int term = (position) % 2 + 1;
                    if (term == 2) {
                        gradeAcademicYear = ayList.get((position - 1) / 2).toString().trim();
                    } else {
                        gradeAcademicYear = ayList.get((position) / 2).toString().trim();
                    }
                    gradeTerm = String.valueOf(term).trim();
                    EduGrade.saveGpaTerm(gradeTerm);
                } else {
                    EduGrade.saveGpaTerm("");
                    switch (position) {
                        case 6:
                            gradeAcademicYear = ayList.get(0).toString().trim();
                            break;
                        case 7:
                            gradeAcademicYear = ayList.get(1).toString().trim();
                            break;
                        case 8:
                            gradeAcademicYear = ayList.get(2).toString().trim();
                            break;
                    }
                }


                EduGrade.saveGpaAcadamicYear(gradeAcademicYear);

                Logger.d("学年:" + EduGrade.getGpaAcadamicYear() + " 学期:" + EduGrade.getGpaTerm());
            }
        });

    }

    @OnClick(R.id.id_query_grade_gpa_btn)
    void queryGpa() {
        EventTag.saveCurrentTag(EventTag.QUERY_GRADE_GPA);
        EduRxVolley.enterEducationHome();
        showWaitDialog();

        mCardStackView.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {
        switch (eventModel.getEventCode()) {
            case Event.EDUCATION_QUERY_GRADE_GPA_SUCCESS:
                dismissWaitDialog();
                mCardStackView.setVisibility(View.VISIBLE);
                Map<String,String> data = new HashMap<>();
                data.put(GradeAdapter.KEY_TITLE,mSpinner.getText()+getString(R.string.gpa)
                        +"\n"+EduGrade.getGradeGpa());
                data.put(GradeAdapter.KEY_CONTENT,"需要查询总平均学分绩点请前往成绩统计页面~");
                mDatas.clear();
                mDatas.add(data);
                mStackAdapter.updateData(mDatas);
                break;
            case Event.EDUCATION_QUERY_GRADE_GPA_FAIL:
                dismissWaitDialog();
                ToastUtil.showShort(getContext(), getString(R.string.gpa_query_fail));
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_grade_gpa;
    }
}
