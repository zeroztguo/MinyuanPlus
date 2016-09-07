package com.csmy.minyuanplus.ui.activity;

import android.content.Intent;
import android.net.Uri;

import com.csmy.minyuanplus.model.afterclass.Weixin;
import com.csmy.minyuanplus.ui.fragment.afterclass.WeixinFragment;
import com.facebook.drawee.drawable.ScalingUtils;

public class WeixinActivity extends BaseAfterClassActivity {
    private Weixin mWeixin;

    @Override
    protected void handleIntent(Intent intent) {
        if (intent != null) {
            Weixin weixin = (Weixin) intent.getSerializableExtra(WeixinFragment.class.getSimpleName());
            if (weixin != null) {
                mWeixin = weixin;
                loadData();
            }
        }
    }

    @Override
    protected void loadData() {
        setTitle(mWeixin.getTitle());
        Uri uri = Uri.parse(mWeixin.getPicUrl());
        mSimpleDraweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY);
        mSimpleDraweeView.setImageURI(uri);
        mWebView.loadUrl(mWeixin.getUrl());

    }

    @Override
    protected String getShareMessage() {
        return "【" + mWeixin.getTitle() + "】：" + mWeixin.getUrl() + "(分享至民院+)";
    }
}
