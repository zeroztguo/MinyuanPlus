package com.csmy.minyuanplus.support.adapter;

/**
 * ATM页面StackAdapter，弃用
 * Created by Zero on 16/7/3.
 */

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.model.college.ATM;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.support.util.Util;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import org.litepal.crud.DataSupport;

import okhttp3.Call;

public class ATMAdapter extends StackAdapter<ATM> {

    private static Context mContext;

    public ATMAdapter(Context context) {
        super(context);
        mContext = context;
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

        public void onBind(final ATM data, int position) {
            mTextTitle.setText(data.getInfo());
            Uri uri = Uri.parse(data.getImgUrl());
            mContent.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
            if (data.getImg() == null || data.getImg().length <= 0) {
                OkHttpUtils
                        .get()
                        .url(data.getImgUrl())
                        .build()
                        .execute(new BitmapCallback() {
                            @Override
                            public void onError(Call call, Exception e) {
                                ToastUtil.showShort(mContext,mContext.getString(R.string.atm_pic_load_fail));
                            }

                            @Override
                            public void onResponse(Bitmap response) {
                                byte[] bytes = Util.Bitmap2Bytes(response);
                                ContentValues values = new ContentValues();
                                values.put("img", bytes);
                                DataSupport.update(ATM.class, values, data.getId());
                                mContent.setImageBitmap(response);
                                Logger.d("从网络加载ATM图片~~~~~~~~~成功");
                            }
                        });
                Logger.d("从网络加载ATM图片");
            } else {
                mContent.setImageBitmap(Util.Bytes2Bitmap(data.getImg()));
                Logger.d("从本地加载ATM图片~~~~~~~~~");
            }
        }
    }

    private void obtainATMImage(String url) {

    }

}
