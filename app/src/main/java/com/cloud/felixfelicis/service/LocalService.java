package com.cloud.felixfelicis.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.cloud.felixfelicis.IServiceAidlInterface;
import com.cloud.felixfelicis.R;

public class LocalService extends Service {
    LocalServiceBinder localServiceBinder;
    LocalServiceConnection localServiceConnection;
    public LocalService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localServiceBinder = new LocalServiceBinder();
        localServiceConnection = new LocalServiceConnection();
        LocalService.this.bindService(new Intent(LocalService.this,RemoteService.class),localServiceConnection, Context.BIND_IMPORTANT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String remoteService = createNotificationChannel("com.cloud.felixfelicis.service.LocalService", "LocalService");
            builder = new NotificationCompat.Builder(LocalService.this)
                    .setChannelId(remoteService);
        }else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        builder.setContentTitle("");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentInfo("info");
        builder.setWhen(System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getActivity(LocalService.this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        startForeground(startId,builder.build());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return localServiceBinder;
    }

    /**
     * 用于绑定服务
     */
    public class LocalServiceConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LocalService.this.startService(new Intent(LocalService.this,RemoteService.class));
            LocalService.this.bindService(new Intent(LocalService.this,RemoteService.class),localServiceConnection, Context.BIND_IMPORTANT);
        }
    }

    public class LocalServiceBinder extends IServiceAidlInterface.Stub{
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannel(String id , String channel){
        NotificationChannel notificationChannel = new NotificationChannel(id, channel, NotificationManager.IMPORTANCE_NONE);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        return id;
    }
}