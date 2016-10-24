package com.csmy.minyuanplus.ui.fragment.education;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.support.education.EduGrade;
import com.csmy.minyuanplus.support.education.EduRxVolley;
import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.event.EventTag;
import com.csmy.minyuanplus.model.education.GradeCourseStatistical;
import com.csmy.minyuanplus.model.education.GradeInfoStatistical;
import com.csmy.minyuanplus.model.education.PersonalInfo;
import com.csmy.minyuanplus.support.adapter.GradeAdapter;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;
import com.csmy.minyuanplus.support.util.ToastUtil;
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
 * 成绩统计
 * Created by Zero on 16/7/11.
 */
public class GradeStatisticalFragment extends BaseFragment {
    @Bind(R.id.id_grade_statistical_btn)
    AppCompatButton mGradeStatisticalBtn;
    @Bind(R.id.id_grade_statistical_csv)
    CardStackView mCardStackView;

    private List<Map<String, String>> mDatas;
    private GradeAdapter mAdapter;

    public static GradeStatisticalFragment newInstance() {
        return new GradeStatisticalFragment();
    }

    @Override
    protected void initView(View view, Bundle saveInstanceState) {
        mDatas = new ArrayList<>();
        mAdapter = new GradeAdapter(getContext());
        mCardStackView.setAdapter(mAdapter);
    }


    @OnClick(R.id.id_grade_statistical_btn)
    void statistical() {
        gradeStatistical();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_grade_statistical;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {
        switch (eventModel.getEventCode()) {
            case Event.EDUCATION_GRADE_STATISTICAL_SUCCESS:
                dismissWaitDialog();
//                mOptionViews = new ArrayList<>();
                List datas = EduGrade.getGradeStatistical();
                for (int i = 0; i < datas.size(); i++) {
                    Map<String, String> data = new HashMap<>();
                    if (i == 0) {
                        GradeInfoStatistical info = (GradeInfoStatistical) datas.get(i);
                        PersonalInfo personalInfo = DataSupport.findAll(PersonalInfo.class).get(0);
                        data.put(GradeAdapter.KEY_TITLE, personalInfo.getClassInfo() + " " + personalInfo.getName() + "\n" + "绩点：" + info.getGpaAverage());
                        data.put(GradeAdapter.KEY_CONTENT, "所选学分：" + info.getCreditSelect() + "\n" +
                                "获得学分：" + info.getCreditObtain() + "\n" +
                                "重修学分：" + info.getCreditRestudy() + "\n" +
                                "正考未通过学分：" + info.getCreditFail() +
                                "\n\n（提示：绩点为所有已修课程的平均学分绩点，如需查询学年或学期绩点请前往绩点查询页面）");
                    } else {
                        String prompt = "";
                        GradeCourseStatistical course = (GradeCourseStatistical) datas.get(i);
                        String courseProperty = course.getCourseProperty();
                        if (courseProperty.equals("公共艺术")) {
                            prompt += "\n\n（提示：在7分的公共任选课中必须有2分公共艺术学分）";
                        } else if (courseProperty.equals("人文社科")) {
                            prompt += "\n\n（提示：在7分的公共任选课中人文社科类不作要求，所以还需学分为负）";
                        } else if (courseProperty.equals("公共任选课")) {
                            prompt += "\n\n（提示：公共任选课包括人文社科和公共艺术两类课程）";
                        }
                        data.put(GradeAdapter.KEY_TITLE, courseProperty);
                        data.put(GradeAdapter.KEY_CONTENT, "学分要求：" + course.getCreditRequire() + "\n" +
                                "获得学分：" + course.getCreditObtain() + "\n" +
                                "未通过学分：" + course.getCreditFail() + "\n" +
                                "还需学分：" + course.getCreditNeed() + prompt);
                    }
                    mDatas.add(data);

                }
                mAdapter.updateData(mDatas);
                mGradeStatisticalBtn.setVisibility(View.GONE);

                //手动回收
                mDatas = null;
                break;
            case Event.EDUCATION_GRADE_STATISTICAL_FAIL:
                dismissWaitDialog();
                ToastUtil.showShort(getContext(), "查询成绩统计失败，请再试一次...");
                break;
        }
    }


    /**
     * 查询成绩统计
     */
    private void gradeStatistical() {
        EventTag.saveCurrentTag(EventTag.GRADE_STATISTICAL);
        EduRxVolley.enterEducationHome();
        showWaitDialog();
    }
}
