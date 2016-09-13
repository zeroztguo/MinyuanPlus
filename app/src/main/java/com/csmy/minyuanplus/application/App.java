package com.csmy.minyuanplus.application;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;

import com.csmy.minyuanplus.support.SettingConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.http.RequestQueue;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.LitePalApplication;

import java.util.Locale;
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

        initLanguage();
    }

    /**
     * 初始化语言
     */
    private void initLanguage() {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        String language = SettingConfig.getLanguage();
        if (language.equals(SettingConfig.ZH_SIMPLE)) {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        } else if (language.equals(SettingConfig.ZH_TW)) {
            config.locale = Locale.TRADITIONAL_CHINESE;
        } else if (language.equals(SettingConfig.EN)) {
            config.locale = Locale.ENGLISH;
        } else {
            config.locale = Locale.getDefault();
        }
        resources.updateConfiguration(config, dm);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
