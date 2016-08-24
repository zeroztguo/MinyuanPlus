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

public class GradeFailStackAdapter extends StackAdapter<Grade> {

    public static int[] TEST_DATAS = new int[]{
            R.color.red,
            R.color.color_2,
            R.color.color_4,
            R.color.red,
            R.color.color_2,
            R.color.color_4,
            R.color.red,
            R.color.color_2,
            R.color.color_4,
            R.color.red,
            R.color.color_2,
            R.color.color_4,
            R.color.red,
            R.color.color_2,
            R.color.color_4,
            R.color.red,
            R.color.color_2,
            R.color.color_4,
            R.color.red,
            R.color.color_2,
            R.color.color_4,
            R.color.red,
            R.color.color_2,
            R.color.color_4
    };

    public GradeFailStackAdapter(Context context) {
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
            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), TEST_DATAS[position]), PorterDuff.Mode.SRC_IN);
            mTextTitle.setText(data.getCourseName() + "\n" + data.getLevel());

            mTextContent.setText(
                    data.getCourseName() + "(" + data.getCourseType() + ")" + "\n" +
                            "学分：" + data.getGpa() + "\n" +
                            "成绩：" + data.getLevel() + "\n");

        }
    }


}
