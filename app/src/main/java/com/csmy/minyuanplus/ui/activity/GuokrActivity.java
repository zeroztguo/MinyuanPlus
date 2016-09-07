package com.csmy.minyuanplus.ui.activity;

import android.content.Intent;
import android.net.Uri;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.model.afterclass.GuokrContent;
import com.csmy.minyuanplus.model.afterclass.GuokrContentDetail;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.support.util.Util;
import com.csmy.minyuanplus.ui.fragment.afterclass.GuokrFragment;
import com.facebook.drawee.drawable.ScalingUtils;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class GuokrActivity extends BaseAfterClassActivity {
    private String url;
    private GuokrContentDetail guokrContentDetail;


    @Override
    protected void loadData() {
        OkHttpUtils.get()
                .url(url)
                .tag(this)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showShort(GuokrActivity.this, getString(R.string.guokr_news_load_fail));
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            Gson gson = new Gson();
                            JSONObject jsonObj = new JSONObject(response);
                            GuokrContent guokrContent = gson.fromJson(jsonObj.toString(), GuokrContent.class);
                            guokrContentDetail = guokrContent.getResult();

//                            mTiTleTextView.setText(guokrContentDetail.getTitle());
                            setTitle(guokrContentDetail.getTitle());
                            mWebView.loadDataWithBaseURL("file:///android_asset/", "<link rel=\"stylesheet\" type=\"text/css\" href=\"guokr.css\" />" + guokrContentDetail.getContent(), "text/html", "utf-8", null);
//
                            if (!Util.isStringNull(guokrContentDetail.getSmall_image())) {
                                Uri uri = Uri.parse(guokrContentDetail.getSmall_image());
                                mSimpleDraweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
                                mSimpleDraweeView.setImageURI(uri);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected String getShareMessage() {
        return "【" + guokrContentDetail.getTitle() + "】：" + guokrContentDetail.getUrl() + "(分享至民院+)";
    }

    @Override
    protected void handleIntent(Intent intent) {
        if (null != intent) {
            url = intent.getStringExtra(GuokrFragment.class.getSimpleName());
            loadData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }

}
