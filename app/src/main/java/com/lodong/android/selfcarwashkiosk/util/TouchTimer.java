package com.lodong.android.selfcarwashkiosk.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.lodong.android.selfcarwashkiosk.view.MainActivity;

import java.util.TimerTask;

public class TouchTimer extends TimerTask {
    private Activity activity;
    private final int LIMIT_TIME = 45;
    private int time = 0;

    private TouchHandler touchHandler;

    public TouchTimer(Activity activity) {
        this.activity = activity;
        touchHandler = new TouchHandler(activity);
    }

    @Override
    public void run() {
        // 제한시간동안 터치가 없을 시 종료
        Log.d("touch timer : ", String.valueOf(time));
        int lastTime = LIMIT_TIME - time++;

        Message message = touchHandler.obtainMessage();

        Bundle bundle = new Bundle();
        bundle.putInt("value", lastTime);
        message.setData(bundle);
        touchHandler.sendMessage(message);
        if (lastTime == 0) {
            this.cancel();
        }
    }

    public void initTime() {
        this.time = 0;
    }
}
