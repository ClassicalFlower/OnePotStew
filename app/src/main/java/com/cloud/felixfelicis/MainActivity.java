package com.cloud.felixfelicis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Choreographer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloud.felixfelicis.service.JobHandleService;
import com.cloud.felixfelicis.service.LocalService;
import com.cloud.felixfelicis.service.RemoteService;
import com.cloud.felixfelicis.viewer.VerticalScrollFrameLayout;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private VerticalScrollFrameLayout vs;
    private TextView mFps;
    private long mStartFrameTime = 0;
    private int mFrameCount = 0;
    /**
     * 单次计算FPS使用160毫秒
     */
    private static final long MONITOR_INTERVAL = 160L;
    private static final long MONITOR_INTERVAL_NANOS = MONITOR_INTERVAL * 1000L * 1000L;
    /**
     * 设置计算fps的单位时间间隔1000ms,即fps/s;
     */
    private static final long MAX_INTERVAL = 1000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //无限滚动
        vs = findViewById(R.id.vl_frame_layout_login_bg);
        mFps = findViewById(R.id.tv_fps);
        ImageView imageView = findViewById(R.id.iv_hook);
        final BitmapDrawable bd = (BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.img_loop_bg);
        vs.post(() -> {
            if (bd != null) {
                vs.setSrcBitmap(bd.getBitmap());
                vs.startScroll();
            }
        });

        imageView.setImageBitmap(bd.getBitmap());
        //进程保活
//        startService(new Intent(this, LocalService.class));
//        startService(new Intent(this, RemoteService.class));
//        startService(new Intent(this, JobHandleService.class));

//        getFPS();
    }

    /**
     * 获取帧率
     * 可以registerActivityLifecycleCallbacks监听所有activity
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void getFPS() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return;
        }
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                if (mStartFrameTime == 0) {
                    mStartFrameTime = frameTimeNanos;
                }
                long interval = frameTimeNanos - mStartFrameTime;
                if (interval > MONITOR_INTERVAL_NANOS) {
                    double fps = (((double) (mFrameCount * 1000L * 1000L)) / interval) * MAX_INTERVAL;
                    Log.d(TAG, String.valueOf(fps));
                    mFps.setText(String.valueOf(fps));
                    mFrameCount = 0;
                    mStartFrameTime = 0;
                } else {
                    ++mFrameCount;
                }
                Choreographer.getInstance().postFrameCallback(this);
            }
        });
    }
}
