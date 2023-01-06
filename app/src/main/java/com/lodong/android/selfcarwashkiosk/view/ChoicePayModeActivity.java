package com.lodong.android.selfcarwashkiosk.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.lodong.android.selfcarwashkiosk.R;
import com.lodong.android.selfcarwashkiosk.databinding.ActivityChoicePayModeBinding;
import com.lodong.android.selfcarwashkiosk.outApp.CardActivity;
import com.lodong.android.selfcarwashkiosk.util.TouchTimer;
import com.lodong.android.selfcarwashkiosk.util.Util;

import java.util.Timer;

public class ChoicePayModeActivity extends AppCompatActivity {
    private ActivityChoicePayModeBinding binding;
    private TouchTimer touchTimer;
    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choice_pay_mode);
        binding.setActivity(this);

        settingClickListener();
        //집중모드
        Util.hideNavigationView(this);
    }

    private void settingClickListener(){
        binding.getRoot().setOnClickListener(view -> {
            this.touchTimer.initTime();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //집중모드
        Util.hideNavigationView(this);
        //터치 타이머 세팅
        touchTimer = new TouchTimer(this);
        timer = new Timer();
        timer.schedule(touchTimer, 0,1000);
    }

    public void intentCompletePayActivity(){
        Intent intent = new Intent(this, CardActivity.class);
        intent.putExtra("payMethod",1);
        startActivity(intent);
        timer.cancel();
    }

    public void intentRFIDPay(){
        Intent intent = new Intent(this, CardActivity.class);
        intent.putExtra("payMethod",2);
        startActivity(intent);
        timer.cancel();
    }

}