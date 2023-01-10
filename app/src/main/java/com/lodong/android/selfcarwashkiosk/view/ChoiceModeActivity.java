package com.lodong.android.selfcarwashkiosk.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lodong.android.selfcarwashkiosk.R;
import com.lodong.android.selfcarwashkiosk.databinding.ActivityChoiceModeBinding;
import com.lodong.android.selfcarwashkiosk.util.TouchTimer;
import com.lodong.android.selfcarwashkiosk.util.Util;

import java.util.Timer;


public class ChoiceModeActivity extends AppCompatActivity {
    private ActivityChoiceModeBinding binding;
    private TouchTimer touchTimer;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choice_mode);
        binding.setActivity(this);

        settingClickListener();

    }

    private void settingClickListener(){
        binding.getRoot().setOnClickListener(view -> {
            this.touchTimer.initTime();
        });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timer.cancel();
    }

    public void intentChoicePayModeActvity(){
        startActivity(new Intent(this, ChoicePayModeActivity.class));
        timer.cancel();
    }




}