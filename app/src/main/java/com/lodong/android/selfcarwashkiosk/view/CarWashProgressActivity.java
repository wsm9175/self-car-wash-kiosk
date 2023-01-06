package com.lodong.android.selfcarwashkiosk.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.lodong.android.selfcarwashkiosk.Bluetooth.BluetoothInterface;
import com.lodong.android.selfcarwashkiosk.R;
import com.lodong.android.selfcarwashkiosk.callback.WashListener;
import com.lodong.android.selfcarwashkiosk.databinding.ActivityCarWashProgressBinding;
import com.lodong.android.selfcarwashkiosk.util.Util;

public class CarWashProgressActivity extends AppCompatActivity {
    private ActivityCarWashProgressBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_car_wash_progress);

        BluetoothInterface.getInstance().settingWashConnectListener(getWashListener());

        try {
            startWash();
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "error : " + e, Toast.LENGTH_SHORT).show();
        }
        //집중모드
        Util.hideNavigationView(this);
    }

    private void startWash() throws Throwable {
        BluetoothInterface.getInstance().write("a");
    }

    private WashListener getWashListener(){
        return () -> {
            startActivity(new Intent(CarWashProgressActivity.this, MainActivity.class));
            finish();
        };
    }

}