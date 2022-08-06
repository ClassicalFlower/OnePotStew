package com.cloud.felixfelicis.tools;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class ActivityCostTimeAspectJ {

    @Around("execution(* android.app.Activity.setContentView(..))")
    public void getSetContentViewTime(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        String name = signature.toShortString();
        long millis = System.currentTimeMillis();
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        Log.i(name,"cost:" + (System.currentTimeMillis() - millis));
    }
}
