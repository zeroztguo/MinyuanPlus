package com.csmy.minyuanplus.application;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.csmy.minyuanplus.support.API;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.http.RequestQueue;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.LitePalApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * 实现LitePal的Application
 * Created by Zero on 16/5/25.
 */
public class App extends LitePalApplication {
    public static final String TAG = "MyPlus";

    @Override
    public void onCreate() {
        super.onCreate();


        LitePalApplication.initialize(this);

        boolean bmob = cn.bmob.statistics.AppStat.i(API.APP_KEY, null);

        /*
         * 初始化OkHttp
         */
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .followRedirects(false)
                .build();
        OkHttpUtils.initClient(okHttpClient);


        /*
         * 初始化RxVolley缓存位置
         */
        RxVolley.setRequestQueue(RequestQueue.newRequestQueue(getCacheDir()));

        /*
        初始化控制台打印框架Logger
         */
        Logger.init(TAG);

        /*
        初始化图片加载框架Fresco
         */
        Fresco.initialize(this);

        Logger.d(bmob);


    }




    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
