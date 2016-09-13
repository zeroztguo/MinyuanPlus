package com.csmy.minyuanplus.support;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.model.NotifyContent;
import com.orhanobut.logger.Logger;

import org.litepal.crud.DataSupport;

/**
 * 消息提示的圆点
 * Created by Zero on 16/8/25.
 */
public class BadgeActionProvider extends ActionProvider {
    private AppCompatTextView mNumTextView;
    private AppCompatImageView mIconView;
    private int clickWhat;
    private OnClickListener onClickListener;
    private FrameLayout mNotifyLayout;

    public BadgeActionProvider(Context context) {
        super(context);
    }

    @Override
    public View onCreateActionView() {
        int size = getContext().getResources().getDimensionPixelSize(
                android.support.design.R.dimen.abc_action_bar_default_height_material);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(size, size);
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.view_badge, null, false);

        view.setLayoutParams(layoutParams);
        mNotifyLayout = (FrameLayout) view.findViewById(R.id.id_badge_layout);
        mNumTextView = (AppCompatTextView) view.findViewById(R.id.id_badge_tv);
        mIconView = (AppCompatImageView) view.findViewById(R.id.id_badge_iv);
        mIconView.setImageResource(R.mipmap.notification);
        setTextInt(getUnreadCount());
        view.setOnClickListener(onViewClickListener);
        return view;
    }

    private View.OnClickListener onViewClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(clickWhat);
            }
        }
    };

    public void setOnClickListener(int what, OnClickListener onClickListener) {
        this.clickWhat = what;
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int what);
    }

//    public void setIcon(int icon) {
//        mIvIcon.setImageResource(icon);
//    }

    public void setTextInt(int i) {
        if (i == 0) {
            mNotifyLayout.setVisibility(View.GONE);
        } else {
            mNotifyLayout.setVisibility(View.VISIBLE);
            mNumTextView.setText(i+"");
        }

    }

    /**
     * @return 未读的消息数
     */
    public static int getUnreadCount() {
        int i = 0;
        for (NotifyContent notifyContent : DataSupport.findAll(NotifyContent.class)) {
            if (!notifyContent.isRead()) {
                i++;
            }
        }
        Logger.d("unread:" + i);

        return i;
    }

}
