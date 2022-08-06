package com.cloud.felixfelicis;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.cloud.felixfelicis.tools.ImageHook;

import de.robv.android.xposed.DexposedBridge;
import de.robv.android.xposed.XC_MethodHook;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            DexposedBridge.hookAllConstructors(ImageView.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    ImageView imageView = (ImageView) param.thisObject;
                    Class<?> aClass = imageView.getClass();
                    DexposedBridge.findAndHookMethod(aClass, "setImageBitmap", Bitmap.class, new ImageHook());
                }
            });
        } catch (Throwable e) {
            Log.e("App", "hook failed:" + e);
        }
    }
}
