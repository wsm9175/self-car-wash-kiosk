package com.lodong.android.selfcarwashkiosk.broadcast;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.lodong.android.selfcarwashkiosk.view.MainActivity;

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompleteReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "onReceive: " + action);
        Toast.makeText(context.getApplicationContext(), "부팅완료" + action, Toast.LENGTH_SHORT).show();
        if (action.equals("android.intent.action.BOOT_COMPLETED")){
            startActivity(context);
        }
    }

    static void startActivity(Context context) {
        Toast.makeText(context, "startActivity", Toast.LENGTH_LONG).show();
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return;
    }
}
