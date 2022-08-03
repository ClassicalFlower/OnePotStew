package com.cloud.felixfelicis.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

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
        Notification.Builder builder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(LocalService.this, "LOCAL");
        }else {
            builder = new Notification.Builder(LocalService.this);
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
}