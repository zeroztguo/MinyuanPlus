package com.csmy.minyuanplus.ui.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.csmy.minyuanplus.R;
import com.orhanobut.logger.Logger;

import org.litepal.LitePalApplication;

import java.util.Map;

/**
 * 下载的广播接收者，暂时弃用
 * Created by Zero on 16/8/25.
 */
public class DownloadReceiver extends BroadcastReceiver {
    private NotificationManager manager;
    private NotificationCompat.Builder builder;
    private Notification notification;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadService.ACTION_DOWNLOAD_UPDATE)) {
            Logger.d("接收到了广播");
            Map<String, Object> map = (Map<String, Object>) intent.getSerializableExtra(DownloadService.DOWNLOAD);
            int max = (int) map.get(DownloadService.MAX);
            int progress = (int) map.get(DownloadService.PROGRESS);
            manager = (NotificationManager) context.getSystemService(LitePalApplication.getContext().NOTIFICATION_SERVICE);
            builder = new NotificationCompat.Builder(LitePalApplication.getContext());
            notification = builder
                    .setProgress(max, progress, false)
                    .setContentInfo(progress + "%")
                    .setContentTitle(progress == 100 ? "正在下载民院+" : "下载完成")
                    .setSmallIcon(R.mipmap.notification)
                    .setLargeIcon(BitmapFactory.decodeResource(LitePalApplication.getContext().getResources()
                            , R.mipmap.icon))
                    .build();
            manager.notify(1, notification);
            if (progress == 100) {
                Intent i = new Intent("android.intent.action.VIEW");
                // 添加默认分类
                i.addCategory("android.intent.category.DEFAULT");
                // 设置数据和类型 在文件中
                i.setDataAndType(Uri.parse(map.get(DownloadService.PATH).toString()),
                        "applicationnd.android.package-archive");
                // 如果开启的activity 退出的时候 会回调当前activity的onActivityResult

                context.startActivity(intent);
            }
        }
    }
}
