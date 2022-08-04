package com.cloud.felixfelicis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;

import com.cloud.felixfelicis.service.JobHandleService;
import com.cloud.felixfelicis.service.LocalService;
import com.cloud.felixfelicis.service.RemoteService;
import com.cloud.felixfelicis.viewer.VerticalScrollFrameLayout;

public class MainActivity extends AppCompatActivity {

    private VerticalScrollFrameLayout vs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //无限滚动
        vs = findViewById(R.id.vl_frame_layout_login_bg);
        final BitmapDrawable bd = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.img_loop_bg);
        vs.post(new Runnable() {
            @Override
            public void run() {
                if (bd != null) {
                    vs.setSrcBitmap(bd.getBitmap());
                    vs.startScroll();
                }
            }
        });
        //进程保活
//        startService(new Intent(this, LocalService.class));
//        startService(new Intent(this, RemoteService.class));
//        startService(new Intent(this, JobHandleService.class));
    }
}
