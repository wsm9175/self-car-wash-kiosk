package com.lodong.android.selfcarwashkiosk.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;

public class Util {

    public static void hideNavigationView(Activity activity) {
        int currentApiVersion = Build.VERSION.SDK_INT;

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        //this work only for android 4.4+
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT){
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            // Code below is to handle of Volume up or volume down.
            // Without this, after pressing volume buttons, the navigation bar will show up
            // won't hide
            final View decorView = activity.getWindow().getDecorView();
            decorView
                    .setOnSystemUiVisibilityChangeListener(visibility -> {
                        if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0){
                            decorView.setSystemUiVisibility(flags);
                        }
                    });

        }
    }

    public static void startLock(Activity activity){
        activity.startLockTask();
    }

    public static void stopLock(Activity activity){
        activity.stopLockTask();
    }
}
