package com.quki.example.realm.realmexample;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import io.realm.Realm;

/**
 * Created by quki on 2016-05-31.
 */
public class MainService extends Service implements Runnable {

    private Handler mHandler;
    private static final int UPDATE_TIME_INTERVAL = 10000; //millisecond,
    private int count = 1;

    private Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mHandler.postDelayed(this, 1); // call run(), count mode on
        Toast.makeText(getApplicationContext(), "onCreate", Toast.LENGTH_SHORT).show();
        realm = Realm.getInstance(getApplicationContext());
        setServiceNotification();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "onStartCmd", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT).show();
        mHandler.removeCallbacks(this); // stop run()
        stopForeground(true);
    }

    @Override
    public void run() {
        Toast.makeText(getApplicationContext(), count * 10 + "", Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(this, UPDATE_TIME_INTERVAL);
        Data mData = new Data();
        mData.setName("my data made by service" + count);
        mData.setNumber(count);
        realm.beginTransaction();
        realm.copyToRealm(mData);
        realm.commitTransaction();
        count++;
    }

    protected void setServiceNotification() {
        PendingIntent invokeActivity =
                PendingIntent.getActivity(
                        this
                        , 0
                        , new Intent(this, MainActivity.class)
                        , PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Service on")
                .setContentText("Hello Service")
                .setContentIntent(invokeActivity);
        Notification mNotification = mBuilder.build();
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotification.priority = Notification.PRIORITY_MAX;
        startForeground(1,mNotification);
    }
}
