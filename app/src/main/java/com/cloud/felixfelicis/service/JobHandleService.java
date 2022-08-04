package com.cloud.felixfelicis.service;

import android.app.ActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.cloud.felixfelicis.utils.AppUtils;

import java.util.List;

public class JobHandleService extends JobService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleJob(getJobInfo());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        //真正唤醒操作
        if (!AppUtils.isWorkService(this,"com.cloud.felixfelicis.service.LocalService")) {
            startService(new Intent(this, LocalService.class));
        }
        if (!AppUtils.isWorkService(this,"com.cloud.felixfelicis.service.RemoteService")) {
            startService(new Intent(this, RemoteService.class));
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    public void scheduleJob(JobInfo job) {
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(job);
    }

    private JobInfo getJobInfo() {
        JobInfo.Builder builder = new JobInfo.Builder(0x0001, new ComponentName(this, JobHandleService.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            builder.setMinimumLatency(1000);
        }else {
            builder.setPeriodic(1000);
        }
        builder.setPersisted(true);
        builder.setRequiresCharging(false);
        builder.setRequiresDeviceIdle(false);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        return builder.build();
    }

}
