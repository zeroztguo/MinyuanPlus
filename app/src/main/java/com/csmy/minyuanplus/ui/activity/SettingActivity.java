package com.csmy.minyuanplus.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.model.Setting;
import com.csmy.minyuanplus.support.DataCleanManager;
import com.csmy.minyuanplus.support.SettingConfig;
import com.csmy.minyuanplus.support.adapter.DividerItemDecoration;
import com.csmy.minyuanplus.support.util.SnackbarUtil;
import com.csmy.minyuanplus.ui.BaseToolbarView;
import com.csmy.minyuanplus.ui.view.CustomImageView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;

public class SettingActivity extends BaseActivity implements BaseToolbarView{
    @Bind(R.id.id_setting_preference_rv)
    RecyclerView mPrefRecyclerView;
    @Bind(R.id.id_setting_basic_rv)
    RecyclerView mBasicRecyclerView;
    @Bind(R.id.id_base_tool_bar)
    Toolbar mToolbar;

    MultiItemTypeAdapter<Setting> mBasicAdapter;
    ItemViewDelegate<Setting> mTextDelegate;
    ItemViewDelegate<Setting> mSwitchDelegate;
    LinearLayoutManager mPrefLayoutManager;
    LinearLayoutManager mBasicLayoutManager;
    CommonAdapter mPrefAdapter;

    List<Setting> mBasicDatas;

    String[] mPrefTitles = {"语言", "头像"};

    String[] mBasicTitles = {"省流量模式", "清空缓存"};
    String[] mBasicSubTitles = {"仅在WI-FI环境下才会自动加载课余页面图片和视频", "清理民院+的缓存"};
    boolean[] mIsSwitchArray = {true, false};
    AlertDialog mUserIconDialog;
    int mUserIconIndex;


    @Override
    protected void initView(Bundle savedInstanceState) {
        initToolbar();
        initPreferenceRecyclerView();
        initBasicRecyclerView();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initPreferenceRecyclerView() {
        mPrefLayoutManager = new LinearLayoutManager(this);
        mPrefRecyclerView.setLayoutManager(mPrefLayoutManager);
        mPrefRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        mPrefAdapter = new CommonAdapter<String>(this,R.layout.item_setting_text, Arrays.asList(mPrefTitles)) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                holder.setText(R.id.id_setting_text_title_actv, s);
                holder.setVisible(R.id.id_setting_text_subtitle_actv,false);
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
                switch (position){
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

    private void showSetUserIconDialog(){
        final List<CustomImageView> customImageViews = new ArrayList<>();

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("头像");

        RecyclerView recyclerView = (RecyclerView) LayoutInflater.from(this).inflate(R.layout.item_setting_head, (ViewGroup) findViewById(R.id.id_setting_user_icon_rv));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4);
        recyclerView.setLayoutManager(gridLayoutManager);
        final Integer[] imgs = SettingConfig.getUserIconArray();
        mUserIconIndex = SettingConfig.getUserIconIndex();
        CommonAdapter<Integer> adapter = new CommonAdapter<Integer>(this,R.layout.item_setting_user_icon,Arrays.asList(imgs)) {
            @Override
            protected void convert(ViewHolder holder, Integer integer, final int position) {
                final CustomImageView imageView = holder.getView(R.id.id_setting_user_icon_iv);
                imageView.setSrc(BitmapFactory.decodeResource(getResources(),integer.intValue()));
                if(mUserIconIndex == position){
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
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SettingConfig.setUserIconIndex(mUserIconIndex);
                dialog.dismiss();
                SnackbarUtil.showWithNoAction(mBasicRecyclerView,"头像设置成功");
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
                titleTextView.setText(setting.getTitle() );

                AppCompatTextView subtitleTextView = holder.getView(R.id.id_setting_text_subtitle_actv);
                subtitleTextView.setText(setting.getSubTitle() );

                switch (position){
                    case 1:
                        subtitleTextView.setText(subtitleTextView.getText()+"   "+ DataCleanManager.getCacheSize(getBaseContext()));
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
                titleTextView.setText(setting.getTitle() );

                AppCompatTextView subtitleTextView = holder.getView(R.id.id_setting_text_switch_subtitle_actv);
                subtitleTextView.setText(setting.getSubTitle() );

                SwitchCompat switchButton = holder.getView(R.id.id_setting_text_switch_switch);
                switch (position){
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
                switch (position){
                    case 1:
                        DataCleanManager.cleanAllCache(getBaseContext());
                        SnackbarUtil.showWithNoAction(mBasicRecyclerView,"清除缓存成功");
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
        getSupportActionBar().setTitle("设置");
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
