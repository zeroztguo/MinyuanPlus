package com.csmy.minyuanplus.support.adapter;

/**
 * Created by Zero on 16/7/3.
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.model.ATM;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;

public class ATMAdapter extends StackAdapter<ATM> {

    public ATMAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindView(ATM data, int position, CardStackView.ViewHolder holder) {
        if (holder instanceof ColorItemViewHolder) {
            ColorItemViewHolder h = (ColorItemViewHolder) holder;
            h.onBind(data, position);
        }
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view;
        view = getLayoutInflater().inflate(R.layout.list_card_atm_item, parent, false);
        return new ColorItemViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.list_card_atm_item;
    }

    static class ColorItemViewHolder extends CardStackView.ViewHolder {
        TextView mTextTitle;
        SimpleDraweeView mContent;
        View mContainerContent;

        public ColorItemViewHolder(View view) {
            super(view);
            mContainerContent = view.findViewById(R.id.id_atm_container_layout);
            mTextTitle = (TextView) view.findViewById(R.id.id_atm_info_tv);
            mContent = (SimpleDraweeView) view.findViewById(R.id.id_atm_img_sdv);
        }

        @Override
        public void onItemExpand(boolean b) {
            mContainerContent.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void onBind(ATM data, int position) {
            mTextTitle.setText(data.getInfo());
        }
    }

}
