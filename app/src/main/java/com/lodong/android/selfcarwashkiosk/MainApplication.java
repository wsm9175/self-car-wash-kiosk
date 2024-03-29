package com.lodong.android.selfcarwashkiosk;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Process;
import android.system.ErrnoException;
import android.util.Log;
import com.lodong.android.selfcarwashkiosk.Bluetooth.BluetoothInterface;
import com.lodong.android.selfcarwashkiosk.callback.ConnectBluetoothListener;
import com.lodong.android.selfcarwashkiosk.callback.ConnectSerialListener;
import com.lodong.android.selfcarwashkiosk.serial.SerialManager;

public class MainApplication extends Application {
    private static final String TAG = MainApplication.class.getSimpleName();
    private static MainApplication mInstance;
    private BluetoothInterface bluetoothInterface;
    public SerialManager serialManager;

    private int reconnectSerialCount = 0;
    private int reconnectBluetoothCount = 0;

    private boolean isSerialConnect;
    private boolean isBluetoothConnect;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        try {
            connectDevice();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static MainApplication getInstance() {
        return mInstance;
    }

    public void connectDevice() throws ErrnoException {
        connectBluetooth();
        settingSerial();
    }

    public void settingSerial() throws ErrnoException {
        Log.d(TAG, "settingSerial: 세팅 시리얼");
        if(!isSerialConnect){
            serialManager = SerialManager.getInstance(getApplicationContext());
            serialManager.setConnectSerialListener(getConnectSerialListener());
            serialManager.settingSerial();
            serialManager.connectSensor();

            serialManager.rejectCard();
            }
    }

    public void connectBluetooth() {
        Log.d(TAG, "connectBluetooth");
        if(!isBluetoothConnect){
            bluetoothInterface = BluetoothInterface.getInstance();
            bluetoothInterface.setConnectListener(getConnectListener());
            bluetoothInterface.connectBluetoothWithHC06();
        }
    }

    private ConnectSerialListener getConnectSerialListener() {
        return new ConnectSerialListener() {
            @Override
            public void onSuccess() {
                isSerialConnect = true;
                reconnectSerialCount = 0;
            }

            @Override
            public void onFailed() throws ErrnoException {
                Log.d(TAG, "onFailed : connect serial failed");
                if (reconnectSerialCount <= 5) {
                    isSerialConnect = false;
                    reconnectSerialCount++;
                    Log.d(TAG, "reconnect try " + reconnectSerialCount);
                    //Toast.makeText(getApplicationContext(), "reconnect try " + reconnectSerialCount, Toast.LENGTH_SHORT).show();
                    settingSerial();
                } else {
                    restart();
                    //Toast.makeText(getApplicationContext(), "Connect serial failed", Toast.LENGTH_SHORT).show();
                    //Process.sendSignal(Process.myPid(), Process.SIGNAL_KILL);
                }
            }
        };
    }

    private ConnectBluetoothListener getConnectListener() {
        return new ConnectBluetoothListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "connect success");
                isBluetoothConnect = true;
                BluetoothInterface.getInstance().setReconnect(false);
                reconnectBluetoothCount = 0;
            }

            @Override
            public void onFailed()  {
                Log.d(TAG, "onFailed: connect bluetooth failed");
                if(reconnectBluetoothCount <= 25){
                    Log.d(TAG, "reconnect try " + reconnectBluetoothCount);
                    isBluetoothConnect = false;
                    BluetoothInterface.getInstance().setConnect(false);
                    BluetoothInterface.getInstance().setConnectedThread(null);
                    //BluetoothInterface.getInstance().setReconnect(true);
                    reconnectBluetoothCount++;
                    //Toast.makeText(getApplicationContext(), "reconnect try " + reconnectBluetoothCount, Toast.LENGTH_SHORT).show();
                    connectBluetooth();
                    //Toast.makeText(getApplicationContext(), "Connect bluetooth failed", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "not connect");
                    //Process.sendSignal(Process.myPid(), Process.SIGNAL_KILL);
                }else{
                    //restart();
                }
            }
        };
    }
    public void restart(){
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        startActivity(mainIntent);
        System.exit(0);
    }

    public boolean isBluetoothConnect() {
        return isBluetoothConnect;
    }
}
