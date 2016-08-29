package com.csmy.minyuanplus.support.education;

import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Response;

/**
 * Created by Zero on 16/7/13.
 */
public abstract class EduCallback extends Callback<String> {
    @Override
    public String parseNetworkResponse(Response response) throws Exception {
        return response.body().toString();
    }

}
