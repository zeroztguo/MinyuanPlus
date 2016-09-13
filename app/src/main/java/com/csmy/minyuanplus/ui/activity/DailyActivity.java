package com.csmy.minyuanplus.ui.activity;

import android.content.Intent;
import android.net.Uri;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.model.afterclass.DailyContent;
import com.csmy.minyuanplus.support.API;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.support.util.Util;
import com.csmy.minyuanplus.ui.fragment.afterclass.DailyFragment;
import com.facebook.drawee.drawable.ScalingUtils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * 知乎日报内容页
 */
public class DailyActivity extends BaseAfterClassActivity {
    private String url;
    private DailyContent dailyContent;


    @Override
    protected void handleIntent(Intent intent) {
        if (null != intent) {
            String id = intent.getStringExtra(DailyFragment.class.getSimpleName());
            url = API.ZHIHU_DAILY_CONTENT + id;
            loadData();
        }
    }

    @Override
    protected void loadData() {
        OkHttpUtils.get()
                .url(url)
                .tag(this)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showShort(DailyActivity.this, getString(R.string.zhihu_daily_load_fail));
                    }

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        dailyContent = gson.fromJson(response, DailyContent.class);
                        if (!Util.isStringNull(dailyContent.getTitle())) {
                            setTitle(dailyContent.getTitle());
                        }
                        mWebView.loadDataWithBaseURL("file:///android_asset/", "<link rel=\"stylesheet\" type=\"text/css\" href=\"dailycss.css\" />" + dailyContent.getBody(), "text/html", "utf-8", null);

                        Uri uri = Uri.parse(dailyContent.getImage());
                        mSimpleDraweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
                        mSimpleDraweeView.setImageURI(uri);
                    }
                });
    }

    @Override
    protected String getShareMessage() {
        return "【" + dailyContent.getTitle() + "】：" + dailyContent.getShare_url() + "(分享至民院+)";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

}
