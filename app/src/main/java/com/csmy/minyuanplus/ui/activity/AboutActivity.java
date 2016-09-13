package com.csmy.minyuanplus.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.VersionInfo;
import com.csmy.minyuanplus.support.API;
import com.csmy.minyuanplus.support.adapter.DividerItemDecoration;
import com.csmy.minyuanplus.support.util.SnackbarUtil;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.support.util.Util;
import com.google.gson.Gson;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

import butterknife.Bind;
import okhttp3.Call;

/**
 * 关于页面
 */
public class AboutActivity extends BaseActivity {
    @Bind(R.id.id_about_college_plus_rv)
    RecyclerView mCollegePlusRecyclerView;
    @Bind(R.id.id_about_copyright_rv)
    RecyclerView mCopyrightRecyclerView;
    @Bind(R.id.id_base_tool_bar)
    Toolbar mToolbar;
    @Bind(R.id.id_setting_version_tv)
    AppCompatTextView mVersionTextView;

    LinearLayoutManager mCollegePlusLayoutManager;
    private CommonAdapter<String> mCollegePlusAdapter;
    LinearLayoutManager mCopyrightLayoutManager;

    private CommonAdapter<String> mCopyrightAdapter;
    private String[] mCollegePlusTitles;
    private String[] mCopyrightTitles;
    private String[] mCopyrightContents;
    private String mVersionCode;
    //    private String mLatestVersionUrl;
    private String mUpdateMessage;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_about;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        mCollegePlusTitles = new String[]{getString(R.string.app_introduce), getString(R.string.check_update)
                , getString(R.string.feedback), getString(R.string.share_application)};
        mCopyrightTitles = new String[]{getString(R.string.education_news_data), getString(R.string.after_class_data)
        };
        mCopyrightContents = new String[]{getString(R.string.from_college), getString(R.string.from_net)};
        mVersionTextView.setText(getString(R.string.version) + "  " + Util.getVersionName());
        initToolbar();
        initCollegePlusRecyclerView();
        initCopyrightRecyclerView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.about));
    }

    private void initCollegePlusRecyclerView() {
        mCollegePlusLayoutManager = new LinearLayoutManager(this);
        mCollegePlusRecyclerView.setLayoutManager(mCollegePlusLayoutManager);
        mCollegePlusRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mCollegePlusAdapter = new CommonAdapter<String>(this, R.layout.item_setting_text, Arrays.asList(mCollegePlusTitles)) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                AutoUtils.auto(viewHolder.getConvertView());
                return viewHolder;
            }

            @Override
            protected void convert(ViewHolder viewHolder, String item, int position) {
                viewHolder.setText(R.id.id_setting_text_title_actv, item);
                viewHolder.setVisible(R.id.id_setting_text_subtitle_actv, false);
            }
        };
        mCollegePlusAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                switch (position) {
                    case 0:
                        appIntroduce();
                        break;
                    case 1:
                        checkUpdate();
                        break;
                    case 2:
                        feedback();
                        break;
                    case 3:
                        shareApp();
                        break;
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                return false;
            }
        });
        mCollegePlusRecyclerView.setAdapter(mCollegePlusAdapter);
    }

    private void appIntroduce() {
        Intent intent = new Intent(AboutActivity.this, AppIntroduceActivity.class);
        startActivity(intent);
    }


    /**
     * 意见反馈
     */
    private void feedback() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.feedback));
        builder.setMessage(getString(R.string.feedback_message));

        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 分享民院+
     */
    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_message));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)));
    }

    private void checkUpdate() {
        showWaitDialog(this);
        OkHttpUtils
                .get()
                .url(API.APP_CHECK_UPDATE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        dismissWaitDialog();
                        ToastUtil.showShort(AboutActivity.this, getString(R.string.obtain_update_message_fail));
                    }

                    @Override
                    public void onResponse(String response) {
                        dismissWaitDialog();

                        Gson gson = new Gson();
                        VersionInfo versionInfo = gson.fromJson(response, VersionInfo.class);
                        mVersionCode = versionInfo.getVersionCode().trim();
//                        mLatestVersionUrl = versionInfo.getLatestVersionUrl().trim();
                        mUpdateMessage = versionInfo.getUpdateMessage().trim();

                        if (Integer.parseInt(mVersionCode) > Util.getVersionCode()) {
                            showUpdateDialog();
                        } else {
                            SnackbarUtil.showSnackShort(mCopyrightRecyclerView, getString(R.string.already_latest_version));
                        }
                    }
                });
    }


    /**
     * 显示提示更新的dialog
     */
    private void showUpdateDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.prompt));
        builder.setMessage(mUpdateMessage);
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private void initCopyrightRecyclerView() {
        mCopyrightLayoutManager = new LinearLayoutManager(this);
        mCopyrightRecyclerView.setLayoutManager(mCopyrightLayoutManager);
        mCopyrightRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mCopyrightAdapter = new CommonAdapter<String>(this, R.layout.item_setting_text, Arrays.asList(mCopyrightTitles)) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                AutoUtils.auto(viewHolder.getConvertView());
                return viewHolder;
            }

            @Override
            protected void convert(ViewHolder viewHolder, String item, int position) {
                viewHolder.setText(R.id.id_setting_text_title_actv, item);
                viewHolder.setText(R.id.id_setting_text_subtitle_actv, mCopyrightContents[position]);
            }
        };
        mCopyrightRecyclerView.setAdapter(mCopyrightAdapter);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onUserEvent(EventModel eventModel) {

    }
}
