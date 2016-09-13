package com.csmy.minyuanplus.support.adapter;

/**
 * 成绩页面的StackAdapter
 * Created by Zero on 16/7/3.
 */

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csmy.minyuanplus.R;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GradeAdapter extends StackAdapter<Map<String, String>> {
    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "content";

    public int[] colorArray = new int[]{
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

    private List<Integer> mColors;


    public GradeAdapter(Context context) {
        super(context);
//        int[] array = context.getResources().getIntArray(R.array.chinese_style_color);
        mColors = new ArrayList<>();
        for (int i = 0; i < colorArray.length; i++) {
            mColors.add(colorArray[i]);
        }
        //打乱顺序
        Collections.shuffle(mColors);
        colorArray = null;
    }

    @Override
    public void bindView(Map<String, String> data, int position, CardStackView.ViewHolder holder) {
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

     class ColorItemViewHolder extends CardStackView.ViewHolder {
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

        public void onBind(Map<String, String> data, int position) {
            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), mColors.get(position)), PorterDuff.Mode.SRC_IN);
            mTextTitle.setText(data.get(KEY_TITLE));
            mTextContent.setText(data.get(KEY_CONTENT));
        }
    }

}
