package com.csmy.minyuanplus.ui.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.csmy.minyuanplus.R;
import com.orhanobut.logger.Logger;

/**
 * Created by Zero on 16/8/25.
 */
public class DownloadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d("接收到了广播");
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.view_download_progress_bar);
        remoteViews.setProgressBar(R.id.id_about_pb, 100, 50, false);
        remoteViews.setTextViewText(R.id.id_about_progress_tv, "99%");

        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder
                .setContentTitle("正在下载民院+")
                .setContent(remoteViews)
                .setSmallIcon(R.mipmap.notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon))
                .build();
        manager.notify(1, notification);
    }
}
