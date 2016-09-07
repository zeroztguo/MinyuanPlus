package com.csmy.minyuanplus.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.support.education.EduLogin;
import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.education.PersonalInfo;
import com.csmy.minyuanplus.support.SettingConfig;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.ui.activity.AboutActivity;
import com.csmy.minyuanplus.ui.activity.SettingActivity;
import com.csmy.minyuanplus.ui.activity.UserInfoActivity;
import com.csmy.minyuanplus.ui.view.CustomColorView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Zero on 16/8/16.
 */
public class MoreFragment extends BaseFragment {
    @Bind(R.id.id_more_lv)
    ListView mListView;
    @Bind(R.id.id_more_user_icon_iv)
    SimpleDraweeView mUserIconImageView;
    @Bind(R.id.id_more_user_name_actv)
    AppCompatTextView mUserNameTextView;


    private CommonAdapter mAdapter;
    private String[] mTitles;
    private int[] mIcons = {R.mipmap.theme, R.mipmap.setting, R.mipmap.about};
    private int mThemeIndex;


    public static MoreFragment newInstance() {
        return new MoreFragment();
    }


    @Override
    protected void initView(View view, Bundle saveInstanceState) {
        mUserIconImageView.getHierarchy().setPlaceholderImage(SettingConfig.userIconArray[SettingConfig.getUserIconIndex()]);
        init();
    }

    @OnClick(R.id.id_more_user_info_app_bar_layout)
    void intent() {
        if (!EduLogin.isEducationLogined())
            return;
        Intent intent = new Intent(getHoldingActivity(), UserInfoActivity.class);
        startActivity(intent);
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//
//    }

    private void init() {
        mTitles = new String[]{getHoldingActivity().getString(R.string.theme), getHoldingActivity().getString(R.string.setting), getHoldingActivity().getString(R.string.about)};
        mAdapter = new CommonAdapter<String>(getContext(), R.layout.item_more, Arrays.asList(mTitles)) {
            @Override
            protected void convert(ViewHolder viewHolder, String item, int position) {
                AppCompatTextView textView = viewHolder.getView(R.id.id_more_title_tv);
                AutoUtils.autoTextSize(textView);
                textView.setText(item);
                AppCompatImageView imageView = viewHolder.getView(R.id.id_more_small_icon_iv);
                imageView.setBackgroundResource(mIcons[position]);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                AutoUtils.auto(view);
                return view;
            }
        };

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        showSetThemeDialog();
                        break;
                    case 1:
                        intent = new Intent(getHoldingActivity(), SettingActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(getHoldingActivity(), AboutActivity.class);
                        startActivity(intent);
                }
            }
        });

        mListView.setAdapter(mAdapter);
        /*
        已登录设置用户信息,未登录设置跳转登录界面监听
         */
        if (EduLogin.isEducationLogined()) {
            PersonalInfo personalInfo = DataSupport.findAll(PersonalInfo.class).get(0);
            mUserNameTextView.setText(personalInfo.getName());
        } else {
            mUserNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Event.sendEmptyMessage(Event.SWITCH_LOGIN_PAGE);
                }
            });
        }
    }

    /**
     * 主题设置
     */
    private void showSetThemeDialog() {
        RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(getHoldingActivity()).inflate(R.layout.item_setting_head, (ViewGroup) getHoldingActivity().findViewById(R.id.id_setting_user_icon_rv));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getHoldingActivity(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        Integer[] themeColorArray = SettingConfig.themeColorArray;
        final List<CustomColorView> colorViews = new ArrayList<>();
        mThemeIndex = SettingConfig.getThemeIndex();
        com.zhy.adapter.recyclerview.CommonAdapter<Integer> adapter = new com.zhy.adapter.recyclerview.CommonAdapter<Integer>(getContext(), R.layout.item_more_theme, Arrays.asList(themeColorArray)) {
            @Override
            protected void convert(com.zhy.adapter.recyclerview.base.ViewHolder holder, Integer integer, final int position) {
                final CustomColorView colorView = holder.getView(R.id.id_more_theme_color_view);
                colorViews.add(colorView);
                colorView.setCircleColor(getResources().getColor(integer.intValue()));
                if (mThemeIndex == position) {
                    colorView.setDrawRight(true);
                }
                colorView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (CustomColorView view : colorViews) {
                            view.setDrawRight(false);
                        }
                        colorView.setDrawRight(true);
                        mThemeIndex = position;
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);

        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getHoldingActivity().getString(R.string.theme));
        builder.setPositiveButton(getHoldingActivity().getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SettingConfig.setThemeIndex(mThemeIndex);
                ToastUtil.showShort(getContext(), getHoldingActivity().getString(R.string.theme_setting_success));
                //重启Activity
                getHoldingActivity().recreate();
            }
        });
        builder.setView(recyclerView);
        builder.setNegativeButton(getHoldingActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_more;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {
        switch (eventModel.getEventCode()) {
            case Event.EDUCATION_LOGIN_SUCCESS:
                init();
                break;
            case Event.UPDATE_USER_ICON:
                /*
                设置用户头像
                 */
                mUserIconImageView.getHierarchy().setPlaceholderImage(SettingConfig.userIconArray[SettingConfig.getUserIconIndex()]);
                break;
        }
    }
}
