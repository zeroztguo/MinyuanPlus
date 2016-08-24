package com.csmy.minyuanplus.support.adapter;

/**
 * Created by Zero on 16/7/3.
 */

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.model.education.Grade;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;

import java.util.Random;

public class GradeStackAdapter extends StackAdapter<Grade> {

    public static int[] TEST_DATAS = new int[]{


            R.color.color_1,
            R.color.color_2,
            R.color.color_3,
            R.color.color_4,
            R.color.color_5,
            R.color.color_6,
            R.color.color_7,
            R.color.color_8,
            R.color.color_9,
            R.color.color_10,
            R.color.color_11,
            R.color.color_12,
            R.color.color_13,
            R.color.color_14,
            R.color.color_15,
            R.color.color_16,
            R.color.color_17,
            R.color.color_18,
            R.color.color_19,
            R.color.color_20,
            R.color.color_21,
            R.color.color_22,
            R.color.color_23,
            R.color.color_24,
            R.color.color_25,
            R.color.color_26
    };


    public GradeStackAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindView(Grade data, int position, CardStackView.ViewHolder holder) {
        if (holder instanceof ColorItemViewHolder) {
            ColorItemViewHolder h = (ColorItemViewHolder) holder;
            h.onBind(data, position);
        }
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view;
        view = getLayoutInflater().inflate(R.layout.list_card_item, parent, false);
        return new ColorItemViewHolder(view);
//        }
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.list_card_item;
    }

    static class ColorItemViewHolder extends CardStackView.ViewHolder {
        View mLayout;
        View mContainerContent;
        TextView mTextTitle;
        TextView mTextContent;

        public ColorItemViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            mTextTitle = (TextView) view.findViewById(R.id.text_list_card_title);
            mTextContent = (TextView) view.findViewById(R.id.text_list_card_content);
        }

        @Override
        public void onItemExpand(boolean b) {
            mContainerContent.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void onBind(Grade data, int position) {
            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), TEST_DATAS[new Random().nextInt(TEST_DATAS.length)]), PorterDuff.Mode.SRC_IN);
            mTextTitle.setText(data.getCourseName() + "\n" + data.getLevel());

            mTextContent.setText(data.getAcademicYear() + "第" + data.getTerm() + "学期" + "\n" +
                    data.getCourseName() + "(" + data.getCourseType() + ")" + "\n" +
                    "绩点：" + data.getGpa() + "\n" +
                    "平时成绩：" + data.getOrdinaryLevel() + "\n" +
                    "期末成绩：" + data.getTerminalLevel() + "\n" +
                    "总成绩：" + data.getLevel() + "\n");

        }
    }

}
