package com.lodong.android.selfcarwashkiosk.Bluetooth;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.system.ErrnoException;
import android.util.Log;


import com.lodong.android.selfcarwashkiosk.callback.ConnectBluetoothListener;
import com.lodong.android.selfcarwashkiosk.callback.WashListener;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothInterface {
    private static BluetoothInterface instance;
    private boolean isConnect = false;
    private ConnectedThread connectedThread;
    private BluetoothDevice device;
    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;

    //-----------------------------------------------------여기 주소 바꾸기---------------------
    //private final String HC06_ADDRESS = "E0:5A:1B:5F:8E:1E"; //1번
    private final String HC06_ADDRESS = "E0:5A:1B:60:58:9E"; // 2번

    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectBluetoothListener connectListener;

    private BluetoothInterface() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothInterface getInstance() {
        if (instance == null) {
            instance = new BluetoothInterface();
        }

        return instance;
    }

    public void setConnectListener(ConnectBluetoothListener connectListener) {
        this.connectListener = connectListener;

    }

    @SuppressLint("MissingPermission")
    public void connectBluetoothWithHC06() {
        this.device = btAdapter.getRemoteDevice(HC06_ADDRESS);
        // create & connect socket
        try {
            this.btSocket = createBluetoothSocket(device);
            this.btSocket.connect();
            isConnect = true;
        } catch (Exception e) {
            isConnect = false;
            e.printStackTrace();
            connectListener.onFailed();
        }
        if (isConnect) {
            connectedThread = new ConnectedThread(btSocket);
            connectedThread.setConnectBluetoothListener(connectListener);
            connectedThread.start();
            connectListener.onSuccess();
        }
    }

    @SuppressLint("MissingPermission")
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws ErrnoException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            connectListener.onFailed();
        }
        try {
            return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
        } catch (IOException e) {
            e.printStackTrace();
            connectListener.onFailed();
            return null;
        }
    }

    public void write(String command) throws Throwable {
        if (isConnect) {
            connectedThread.write(command);
            connectedThread.setWashing(true);
        } else {
            Log.d(TAG, "connect: false");
            connectListener.onFailed();
        }
    }

    public void settingWashConnectListener(WashListener washListener) {
        connectedThread.setWashListener(washListener);
    }

}
