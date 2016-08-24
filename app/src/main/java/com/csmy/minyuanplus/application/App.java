package com.csmy.minyuanplus.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.http.RequestQueue;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.LitePalApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Zero on 16/5/25.
 */
public class App extends LitePalApplication{
    private static android.app.Application _instance;
    public static final String TAG = "CollegePlus";
//    public String[] nohttpTitleList;
//    public String[] cacheTitle;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        /**
         * 初始化OkHttp
         */
//        CookieJarImpl cookieJar1 = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L,TimeUnit.MILLISECONDS)
                .followRedirects(false)
                .build();
        OkHttpUtils.initClient(okHttpClient);

        RxVolley.setRequestQueue(RequestQueue.newRequestQueue(getCacheDir()));
        Logger.init(TAG);

        Fresco.initialize(this);

        App.getInstance();

    }

    public  static Application getInstance(){
        return _instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }





}
