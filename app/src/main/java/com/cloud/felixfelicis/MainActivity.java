package com.cloud.felixfelicis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.cloud.felixfelicis.service.JobHandleService;
import com.cloud.felixfelicis.service.LocalService;
import com.cloud.felixfelicis.service.RemoteService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //进程保活
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            startForegroundService(new Intent(this, LocalService.class));
//        }else {
//            startService(new Intent(this, LocalService.class));
//        }
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            startForegroundService(new Intent(this, RemoteService.class));
//        }else{
//            startService(new Intent(this, RemoteService.class));
//        }
//        startService(new Intent(this, JobHandleService.class));
    }
}
