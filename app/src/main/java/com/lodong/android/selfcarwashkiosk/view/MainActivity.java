package com.lodong.android.selfcarwashkiosk.view;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.system.ErrnoException;
import android.util.Log;
import android.view.View;

import com.lodong.android.selfcarwashkiosk.MainApplication;
import com.lodong.android.selfcarwashkiosk.R;
import com.lodong.android.selfcarwashkiosk.databinding.ActivityMainBinding;
import com.lodong.android.selfcarwashkiosk.util.Util;
import com.lodong.android.selfcarwashkiosk.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);
        viewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MainViewModel.class);
        viewModel.setParent(this);



    }

    @Override
    protected void onResume() {
        super.onResume();
        //집중모드
        Util.hideNavigationView(this);
        //Lock모드
        //Util.startLock(this);
    }


    public void intentChoiceModeActivity(){
        startActivity(new Intent(this, ChoicePayModeActivity.class));
    }

    public void intentSalesInquiry(){
        startActivity(new Intent(this, ManagerMonth.class));
    }
}