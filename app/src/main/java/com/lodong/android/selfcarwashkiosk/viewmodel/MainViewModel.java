package com.lodong.android.selfcarwashkiosk.viewmodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lodong.android.selfcarwashkiosk.Bluetooth.BluetoothInterface;
import com.lodong.android.selfcarwashkiosk.MainApplication;
import com.lodong.android.selfcarwashkiosk.callback.ConnectBluetoothListener;
import com.lodong.android.selfcarwashkiosk.callback.ConnectSerialListener;
import com.lodong.android.selfcarwashkiosk.R;
import com.lodong.android.selfcarwashkiosk.serial.SerialManager;

import java.lang.ref.WeakReference;

public class MainViewModel extends ViewModel {
    private final String TAG = MainViewModel.class.getSimpleName();
    private WeakReference<Activity> mActivity;
    private MutableLiveData<Boolean> isConnectSerial = new MutableLiveData<>();
    private MutableLiveData<Boolean> isConnectBluetooth = new MutableLiveData<>();

    public MainViewModel(){}

    public void setParent(Activity parent){
        this.mActivity = new WeakReference<>(parent);
    }
}
