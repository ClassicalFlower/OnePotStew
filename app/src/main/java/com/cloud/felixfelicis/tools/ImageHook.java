package com.cloud.felixfelicis.tools;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import de.robv.android.xposed.XC_MethodHook;


public class ImageHook extends XC_MethodHook {

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        ImageView imageView = (ImageView) param.thisObject;
        checkBitmap(imageView,imageView.getDrawable());
    }

    private static void checkBitmap(Object obj, Drawable drawable) {
        if (!(drawable instanceof BitmapDrawable) || !(obj instanceof View)) {
            return;
        }
        final Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        if (bitmap == null) {
            return;
        }
        final View view = (View) obj;
        int width = view.getWidth();
        int height = view.getHeight();
        if (width > 0 && height > 0) {
            // 图标宽高都大于view带下的2倍以上，则警告
            if (bitmap.getWidth() >= (width << 1) && bitmap.getHeight() >= (height << 1)) {
                inputWarn(width, height, bitmap, new RuntimeException("Bitmap size too large"));
            }else{
                Log.w("ImageHook", "well");
            }
        } else {
            final Throwable stackTrace = new RuntimeException();
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int w = view.getWidth();
                    int h = view.getHeight();
                    if (w > 0 && h > 0) {
                        if (bitmap.getWidth() >= (w << 1)
                                && bitmap.getHeight() >= (h << 1)) {
                            inputWarn(w, h, bitmap, stackTrace);
                        }else{
                            Log.w("ImageHook", "well");
                        }
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    private static void inputWarn(int w, int h, Bitmap bitmap, Throwable stackTrace) {
        String warnInfo = "Bitmap size too large: " +
                "\n real size: (" + bitmap.getWidth() + ',' + bitmap.getHeight() + ')' +
                "\n desired size: (" + w + ',' + h + ')' +
                "\n call stack trace: \n" + Log.getStackTraceString(stackTrace) + '\n';
        Log.w("ImageHook", warnInfo);
    }
}
