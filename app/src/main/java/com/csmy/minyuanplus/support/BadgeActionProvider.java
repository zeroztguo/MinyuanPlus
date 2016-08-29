package com.csmy.minyuanplus.support;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.model.NotifyContent;
import com.csmy.minyuanplus.ui.view.CustomColorView;
import com.orhanobut.logger.Logger;

import org.litepal.crud.DataSupport;

/**
 * Created by Zero on 16/8/25.
 */
public class BadgeActionProvider extends ActionProvider {
    private AppCompatImageView mIvIcon;
//    private AppCompatTextView mTvBadge;
    private CustomColorView mColorView;
    private int clickWhat;
    private OnClickListener onClickListener;

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
        mIvIcon = (AppCompatImageView) view.findViewById(R.id.id_badge_iv);
        mColorView = (CustomColorView) view.findViewById(R.id.id_badge_color_view);
        mIvIcon.setImageResource(R.mipmap.notification);
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

    public void setIcon(int icon) {
        mIvIcon.setImageResource(icon);
    }

    public void setTextInt(int i) {
        if(i == 0){
            mColorView.setVisibility(View.GONE);
        }else{
            mColorView.setVisibility(View.VISIBLE);
            mColorView.setTextInt(i);
        }

    }

    /**
     * @return 未读的消息数
     */
    public static int getUnreadCount(){
        int i = 0;
        for (NotifyContent notifyContent : DataSupport.findAll(NotifyContent.class)) {
            if(!notifyContent.isRead()){
                i++;
            }
        }
        Logger.d("unread:"+i);

        return i;
    }

}
