package com.lodong.android.selfcarwashkiosk.util;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lodong.android.selfcarwashkiosk.view.MainActivity;

public class TouchHandler extends Handler {
    private Activity activity;
    public TouchHandler(Activity activity){
        this.activity = activity;
    }
    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        Bundle bundle = msg.getData();
        int lastTime = bundle.getInt("value");
        Log.d("handler",  String.valueOf(lastTime));

        if(lastTime == 0){
            Intent i = new Intent(activity, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(i);
            Toast.makeText(activity, "시간이 경과되어 메인화면으로 이동합니다.", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }
}
