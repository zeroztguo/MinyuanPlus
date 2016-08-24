package com.csmy.minyuanplus.ui.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.event.EventModel;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.Call;

/**
 * Created by Zero on 16/8/21.
 */
public class DownloadService extends IntentService {
    private Context mContext;


    public static final String UPDATE_URL = "update_url";

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext = this.getBaseContext();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String url = intent.getStringExtra(UPDATE_URL);
        Event.sendIntMessage(Event.DOWNLOAD_PROGRESS_UPDATE, 666);


        OkHttpUtils
                .get()
                .url("https://coding.net/u/zeroztguo/p/CollegePlus/git/raw/master/latest_version_info.txt")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.view_download_progress_bar);
                        remoteViews.setProgressBar(R.id.id_about_pb, 100, 50, false);
                        remoteViews.setTextViewText(R.id.id_about_progress_tv,"99%");

//                        Intent intent = new Intent(mContext,MainActivity.class);
//                        PendingIntent pi = PendingIntent.getActivity(mContext,1,intent,0);

                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
                        Notification notification = builder
                                .setContentTitle("正在下载民院+")
                                .setContent(remoteViews)
//                                .setContentIntent(pi)
                                .setSmallIcon(R.mipmap.notification)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon))
                                .build();
                        manager.notify(1, notification);

                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {
    }
}
