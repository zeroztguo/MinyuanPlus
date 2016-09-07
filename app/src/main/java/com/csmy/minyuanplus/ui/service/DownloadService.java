package com.csmy.minyuanplus.ui.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.support.DataCleanManager;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.ProgressListener;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePalApplication;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Zero on 16/8/21.
 */
public class DownloadService extends IntentService {

    public static final String UPDATE_URL = "update_url";
    private static final String APK_NAME = "minyuan-plus.apk";
    private NotificationManager manager;
    private NotificationCompat.Builder builder;
    private Notification notification;
//    private ProgressListener listener;

    public static final String MAX = "max";
    public static final String DOWNLOAD = "download";
    public static final String IS_START = "is_start";
    public static final String PROGRESS = "progress";
    public static final String ACTION_DOWNLOAD_UPDATE = "action_download_update";

    private Context mContext;

    public DownloadService() {
        super("DownloadService");
        mContext = this;
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(DOWNLOAD);
//        registerReceiver(downloadReceiver,DOWNLOAD);

    }


    @Override
    protected void onHandleIntent(Intent intent) {
        EventBus.getDefault().register(this);
        final String url = intent.getStringExtra(UPDATE_URL);
//        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        builder = new NotificationCompat.Builder(LitePalApplication.getContext());
//        notification = builder
//                .setContentTitle("正在下载民院+")
//                .setSmallIcon(R.mipmap.notification)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon))
//                .build();
//        manager.notify(1, notification);

        Map<String, Object> map = new HashMap<>();
//        map.put(IS_START, true);
        map.put(MAX, 100000);
        map.put(PROGRESS, 0);

        Intent broadcastIntent = new Intent(mContext, DownloadReceiver.class);
        broadcastIntent.setAction(ACTION_DOWNLOAD_UPDATE);
        broadcastIntent.putExtra(DOWNLOAD, (Serializable) map);
        sendBroadcast(broadcastIntent);

        downloadApk(url);
    }

    private void downloadApk(String url) {
//        OkHttpUtils
//                .get()
//                .url(url)
//                .build()
//                .execute(new FileCallBack(LitePalApplication.getContext().getCacheDir() + "/", APK_NAME) {
//
//                    @Override
//                    public void inProgress(float progress, long total) {
//                        Event.sendIntMessage(Event.UPDATE_APPLICATION, (int) (progress * 100));
//                    }
//
//
//                    @Override
//                    public void onError(Call call, Exception e) {
//                        Logger.d("失败");
//                    }
//
//                    @Override
//                    public void onResponse(File response) {
//
//                    }
//                });


        ProgressListener listener = new ProgressListener() {
            @Override
            public void onProgress(long transferredBytes, long totalSize) {
//                Logger.d("下载进度" + (int) (transferredBytes / totalSize) + "%");
                Logger.d("transferredBytes:" + DataCleanManager.getFormatSize(transferredBytes) + "\n+totalSize:" + totalSize);

                Map<String, Object> map = new HashMap<>();
//                map.put(IS_START, false);
                map.put(MAX, 100000);
                map.put(PROGRESS, 50000);

                Intent broadcastIntent = new Intent(mContext, DownloadReceiver.class);
                broadcastIntent.setAction(ACTION_DOWNLOAD_UPDATE);
                broadcastIntent.putExtra(DOWNLOAD, (Serializable) map);
                sendBroadcast(broadcastIntent);
//                EventBus.getDefault().post(new EventModel<>(Event.UPDATE_APPLICATION, map));

//                notification = builder.setProgress((int) 100000, (int) transferredBytes, false)
//                        .setContentInfo(transferredBytes / 100000 + "%")
//                        .build();
//                manager.notify(1, notification);
            }
        };

        HttpCallback callback = new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                Logger.d("apk下载完成：" + t);
//                String fileName = LitePalApplication.getContext().getCacheDir() + "/" + APK_NAME;
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
//                startActivity(intent);
//                stopSelf();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                Logger.d("apk下载失败：" + strMsg);

            }
        };
        RxVolley.download(LitePalApplication.getContext().getCacheDir() + "/" + APK_NAME
                , url
                , listener
                , callback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {
        switch (eventModel.getEventCode()) {
            case Event.UPDATE_APPLICATION:

//                Map<String, Long> date = (Map<String, Long>) eventModel.getData();
//                long progress = date.get(PROGRESS);
//                long max = date.get(MAX);
//
//                Logger.d("下载进度" + (int) (progress / max) + "%");
//                notification = builder.setProgress((int) max, (int) progress, false)
//                        .setContentInfo(progress + "%")
//                        .build();
//                manager.notify(1, notification);
                break;
        }
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.d("接收到了广播");
            Map<String, Object> map = (Map<String, Object>) intent.getSerializableExtra(DownloadService.DOWNLOAD);
            int max = (int) map.get(DownloadService.MAX);
            int progress = (int) map.get(DownloadService.PROGRESS);
            manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            builder = new NotificationCompat.Builder(LitePalApplication.getContext());
            notification = builder
//                .setProgress(max, progress, false)
//                .setContentInfo(max / progress + "%")
                    .setContentTitle("正在下载民院+")
                    .setSmallIcon(R.mipmap.notification)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon))
                    .build();
            manager.notify(1, notification);
        }
    };
}
