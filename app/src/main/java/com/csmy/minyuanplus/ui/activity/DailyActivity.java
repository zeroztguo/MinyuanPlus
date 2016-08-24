package com.csmy.minyuanplus.ui.activity;

import android.content.Intent;
import android.net.Uri;

import com.csmy.minyuanplus.model.afterclass.DailyContent;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.support.util.Util;
import com.facebook.drawee.drawable.ScalingUtils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;


public class DailyActivity extends BaseAfterClassActivity {
    private String url;
    private DailyContent dailyContent;


    private static final String DAILY_CONTENT = "http://news-at.zhihu.com/api/4/news/";


    @Override
    protected void handleIntent(Intent intent) {
        if (null != intent) {
            String id = intent.getStringExtra("daily");
            url = DAILY_CONTENT + id;
            loadData();
        }
    }

    @Override
    protected void loadData() {
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.show(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        dailyContent = gson.fromJson(response, DailyContent.class);
                        if(!Util.isStringNull(dailyContent.getTitle())){
                            mTiTleTextView.setText(dailyContent.getTitle());
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
        return "【"+dailyContent.getTitle()+"】："+dailyContent.getShare_url()+"(分享至民院+)";
    }


}
