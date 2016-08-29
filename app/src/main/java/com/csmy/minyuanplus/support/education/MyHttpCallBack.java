package com.csmy.minyuanplus.support.education;

import java.util.Map;

/**
 * Created by Zero on 16/7/1.
 */
public abstract class MyHttpCallBack {

    public void onSuccess(Map<String,String> headers,int respCode,String response){

    }

    public void onFailure(Map<String,String> headers,int respCode){

    }

    public void onFailure(String error){

    }



}
