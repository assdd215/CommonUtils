package com.luzeping.aria.commonutils.utils;

import android.Manifest;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.nio.ByteBuffer;

public class Device {

    private static DeviceInfo info = new DeviceInfo();

    public static void setUp(Context context) {
        if (info == null)
            info = new DeviceInfo();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        info.screenWidth = manager.getDefaultDisplay().getWidth();
        info.screenHeight = manager.getDefaultDisplay().getHeight();
    }

    /**
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static Bitmap image2Bitmap(Image image){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();
            return bitmap;
        }
        return null;
    }

    /**
     * 判断通知选项是否开启
     * @param context
     * @return
     */
    public static boolean isNotificationOpen(Context context){
        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(),"enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)){
            final String[] names = flat.split(":");
            for (int i = 0;i < names.length;i++){
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null){
                    if (TextUtils.equals(pkgName,cn.getPackageName()))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取屏幕高度
     * @return
     */
    public static int getScreenHeight() {
        return info.screenHeight;
    }

    /**
     * 获取屏幕高度
     * @return
     */
    public static int getScreenWidth() {
        return info.screenWidth;
    }

    static class DeviceInfo {
        /**
         * 屏幕高度
         */
        private int screenHeight = 0;
        /**
         * 屏幕宽度
         */
        private int screenWidth = 0;
    }

}
