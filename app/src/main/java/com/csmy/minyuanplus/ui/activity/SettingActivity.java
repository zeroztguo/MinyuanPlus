package com.csmy.minyuanplus.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.support.education.EduLogin;
import com.csmy.minyuanplus.model.Setting;
import com.csmy.minyuanplus.model.education.AcademicYear;
import com.csmy.minyuanplus.model.education.Course;
import com.csmy.minyuanplus.model.education.PersonalInfo;
import com.csmy.minyuanplus.support.DataCleanManager;
import com.csmy.minyuanplus.support.SettingConfig;
import com.csmy.minyuanplus.support.adapter.DividerItemDecoration;
import com.csmy.minyuanplus.support.util.SnackbarUtil;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.ui.BaseToolbarView;
import com.csmy.minyuanplus.ui.view.CustomImageView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity implements BaseToolbarView {
    @Bind(R.id.id_setting_preference_rv)
    RecyclerView mPrefRecyclerView;
    @Bind(R.id.id_setting_basic_rv)
    RecyclerView mBasicRecyclerView;
    @Bind(R.id.id_base_tool_bar)
    Toolbar mToolbar;
    @Bind(R.id.id_setting_quit_account_tv)
    AppCompatTextView mQuitAccountTextView;

    MultiItemTypeAdapter<Setting> mBasicAdapter;
    ItemViewDelegate<Setting> mTextDelegate;
    ItemViewDelegate<Setting> mSwitchDelegate;
    LinearLayoutManager mPrefLayoutManager;
    LinearLayoutManager mBasicLayoutManager;
    CommonAdapter mPrefAdapter;

    List<Setting> mBasicDatas;

    String[] mPrefTitles;
    String[] mBasicTitles;
    String[] mBasicSubTitles;
    String[] mLanguages;
    boolean[] mIsSwitchArray = {true, false};
    AlertDialog mUserIconDialog;
    int mUserIconIndex;


    @Override
    protected void initView(Bundle savedInstanceState) {
        mPrefTitles = new String[]{getString(R.string.language), getString(R.string.head)};
        mBasicTitles = new String[]{getString(R.string.save_flow_mode), getString(R.string.clear_cache)};
        mBasicSubTitles = new String[]{getString(R.string.only_wifi), getString(R.string.clear_app_cache)};
        mLanguages = new String[]{getString(R.string.zh_simple), getString(R.string.zh_tw), getString(R.string.en)};
        if (EduLogin.isEducationLogined()) {
            mQuitAccountTextView.setVisibility(View.VISIBLE);
        } else {
            mQuitAccountTextView.setVisibility(View.GONE);
        }
        initToolbar();
        initPreferenceRecyclerView();
        initBasicRecyclerView();
    }

    /**
     * 退出帐号
     */
    @OnClick(R.id.id_setting_quit_account_tv)
    void quitAccount() {
        DataSupport.deleteAll(AcademicYear.class);
        DataSupport.deleteAll(Course.class);
        DataSupport.deleteAll(PersonalInfo.class);
        EduLogin.setEducationLogin(false);

        finish();
        Intent it = new Intent(SettingActivity.this, MainActivity.class);
        //清空任务栈确保当前打开activity为前台任务栈栈顶
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(it);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initPreferenceRecyclerView() {
        mPrefLayoutManager = new LinearLayoutManager(this);
        mPrefRecyclerView.setLayoutManager(mPrefLayoutManager);
        mPrefRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mPrefAdapter = new CommonAdapter<String>(this, R.layout.item_setting_text, Arrays.asList(mPrefTitles)) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                holder.setText(R.id.id_setting_text_title_actv, s);
                holder.setVisible(R.id.id_setting_text_subtitle_actv, false);
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                AutoUtils.auto(viewHolder.getConvertView());
                return viewHolder;
            }
        };
        mPrefAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                switch (position) {
                    case 0:
                        showSwitchLanguageDialog();
                        break;
                    case 1:
                        showSetUserIconDialog();
                        break;
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                return false;
            }
        });
        mPrefRecyclerView.setAdapter(mPrefAdapter);
    }


    private void showSwitchLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.language));
        final int[] selected = new int[1];
        builder.setSingleChoiceItems(mLanguages, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selected[0] = which;
            }
        });
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (selected[0]) {
                    case 0:
                        switchLanguage(SettingConfig.ZH_SIMPLE);
                        break;
                    case 1:
                        switchLanguage(SettingConfig.ZH_TW);
                        break;
                    case 2:
                        switchLanguage(SettingConfig.EN);
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    /**
     * 选择语言
     *
     * @param language
     */
    private void switchLanguage(String language) {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals(SettingConfig.ZH_SIMPLE)) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (language.equals(SettingConfig.ZH_TW)) {
            config.locale = Locale.TRADITIONAL_CHINESE;
        } else if (language.equals(SettingConfig.EN)) {
            config.locale = Locale.ENGLISH;
        } else {
            config.locale = Locale.getDefault();
        }
        resources.updateConfiguration(config, dm);

        SettingConfig.setLanguage(language);

        ToastUtil.showShort(SettingActivity.this, getString(R.string.language_setting_success));
        finish();

        Intent it = new Intent(SettingActivity.this, MainActivity.class);

        //清空任务栈确保当前打开activity为前台任务栈栈顶

        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(it);
    }


    private void showSetUserIconDialog() {
        final List<CustomImageView> customImageViews = new ArrayList<>();

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.head);

        RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(this).inflate(R.layout.item_setting_head, (ViewGroup) findViewById(R.id.id_setting_user_icon_rv));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        final Integer[] imgs = SettingConfig.getUserIconArray();
        mUserIconIndex = SettingConfig.getUserIconIndex();
        CommonAdapter<Integer> adapter = new CommonAdapter<Integer>(this, R.layout.item_setting_user_icon, Arrays.asList(imgs)) {
            @Override
            protected void convert(ViewHolder holder, Integer integer, final int position) {
                final CustomImageView imageView = holder.getView(R.id.id_setting_user_icon_iv);
                imageView.setSrc(BitmapFactory.decodeResource(getResources(), integer.intValue()));
                if (mUserIconIndex == position) {
                    imageView.setDrawRight(true);
                }
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < customImageViews.size(); i++) {
                            customImageViews.get(i).setDrawRight(false);
                        }
                        customImageViews.get(position).setDrawRight(true);
                        mUserIconIndex = position;
                    }
                });
                customImageViews.add(imageView);
            }
        };


        recyclerView.setAdapter(adapter);

        builder.setView(recyclerView);
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SettingConfig.setUserIconIndex(mUserIconIndex);
                dialog.dismiss();
                SnackbarUtil.showWithNoAction(mBasicRecyclerView, getString(R.string.head_setting_success));
                Event.sendEmptyMessage(Event.UPDATE_USER_ICON);
            }
        });

        mUserIconDialog = builder.create();
        mUserIconDialog.show();
    }

    private void initBasicRecyclerView() {
        mBasicLayoutManager = new LinearLayoutManager(this);
        mBasicRecyclerView.setLayoutManager(mBasicLayoutManager);
        mBasicRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mBasicDatas = new ArrayList<>();
        for (int i = 0; i < mBasicTitles.length; i++) {
            Setting setting = new Setting();
            setting.setTitle(mBasicTitles[i]);
            setting.setSubTitle(mBasicSubTitles[i]);
            setting.setSwitch(mIsSwitchArray[i]);
            mBasicDatas.add(setting);
        }

        mTextDelegate = new ItemViewDelegate<Setting>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_setting_text;
            }

            @Override
            public boolean isForViewType(Setting item, int position) {
                return !item.isSwitch();
            }


            @Override
            public void convert(ViewHolder holder, Setting setting, int position) {
                AppCompatTextView titleTextView = holder.getView(R.id.id_setting_text_title_actv);
                titleTextView.setText(setting.getTitle());

                AppCompatTextView subtitleTextView = holder.getView(R.id.id_setting_text_subtitle_actv);
                subtitleTextView.setText(setting.getSubTitle());

                switch (position) {
                    case 1:
                        try {
                            subtitleTextView.setText(subtitleTextView.getText() + "   " + DataCleanManager.getCacheSize(getBaseContext()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
        mSwitchDelegate = new ItemViewDelegate<Setting>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_setting_text_switch;
            }

            @Override
            public boolean isForViewType(Setting item, int position) {
                return item.isSwitch();
            }

            @Override
            public void convert(ViewHolder holder, Setting setting, int position) {
                AppCompatTextView titleTextView = holder.getView(R.id.id_setting_text_switch_title_actv);
                titleTextView.setText(setting.getTitle());

                AppCompatTextView subtitleTextView = holder.getView(R.id.id_setting_text_switch_subtitle_actv);
                subtitleTextView.setText(setting.getSubTitle());

                SwitchCompat switchButton = holder.getView(R.id.id_setting_text_switch_switch);
                switch (position) {
                    case 0:
                        switchButton.setChecked(SettingConfig.isSaveFlow());
                        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                SettingConfig.setSaveFlow(isChecked);
                            }
                        });
                        break;

                }
            }
        };

        mBasicAdapter = new SettingAdapter(this, mBasicDatas);

        mBasicAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                switch (position) {
                    case 1:
                        DataCleanManager.cleanAllCache(getBaseContext());
                        SnackbarUtil.showWithNoAction(mBasicRecyclerView, getString(R.string.clear_cache_success));
                        mBasicAdapter.notifyItemChanged(1);
                        break;
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                return false;
            }
        });
        mBasicRecyclerView.setAdapter(mBasicAdapter);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_setting;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    public void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.setting));
    }

    private class SettingAdapter extends MultiItemTypeAdapter<Setting> {


        public SettingAdapter(Context context, List<Setting> datas) {
            super(context, datas);
            addItemViewDelegate(mTextDelegate);
            addItemViewDelegate(mSwitchDelegate);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
            AutoUtils.auto(viewHolder.getConvertView());
            return viewHolder;
        }
    }
}
