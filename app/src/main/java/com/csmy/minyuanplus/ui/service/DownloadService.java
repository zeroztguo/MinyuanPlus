package com.csmy.minyuanplus.ui.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.csmy.minyuanplus.R;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

/**
 * Created by Zero on 16/8/21.
 */
public class DownloadService extends IntentService {

    public static final String UPDATE_URL = "update_url";
    private static final String APK_NAME = "minyuan-plus.apk";
    private NotificationManager manager;
    private NotificationCompat.Builder builder;
    private Notification notification;

    public DownloadService() {
        super("DownloadService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        final String url = intent.getStringExtra(UPDATE_URL);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(getBaseContext());
        notification = builder
                .setContentTitle("正在下载民院+")
                .setSmallIcon(R.mipmap.notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon))
                .build();
        manager.notify(1, notification);

        downloadApk(url);
    }

    private void downloadApk(String url) {
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new FileCallBack(getCacheDir().getAbsolutePath() + "/apk/", APK_NAME) {

                    @Override
                    public void inProgress(float progress, long total) {
                        Logger.d("progress:" + progress + " total" + total);
                        notification = builder.setProgress((int)total, (int) progress, false)
                                .setContentInfo((int) (100 * progress) + "%")
                                .build();
                        manager.notify(1, notification);
                    }


                    @Override
                    public void onError(Call call, Exception e) {
                        Logger.d("失败");
                    }

                    @Override
                    public void onResponse(File response) {
                        Logger.d(response.getAbsolutePath());
                        String fileName = getCacheDir().getAbsolutePath() + "/apk/" + APK_NAME;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
                        startActivity(intent);
                        stopSelf();
                    }
                });
    }
}
