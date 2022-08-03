package com.cloud.felixfelicis.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

public class AppUtils {
    public static boolean isWorkService(Context context, String serviceName){
        boolean isWork = false;
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(128);
        if (runningServices.size() <= 0){
            return false;
        }

        for (ActivityManager.RunningServiceInfo info : runningServices) {
            String className = info.service.getClassName();
            if (serviceName.equals(className)){
                isWork =  true;
                break;
            }
        }
        return isWork;
    }
}
