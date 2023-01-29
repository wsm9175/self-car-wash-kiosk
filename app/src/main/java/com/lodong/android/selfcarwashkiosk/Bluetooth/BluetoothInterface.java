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
    static boolean isWashing;
    static boolean isReceiveStart = true;
    private boolean isReconnect = false;
    private boolean isConnect = false;
    private ConnectedThread connectedThread;
    private BluetoothDevice device;
    private BluetoothAdapter btAdapter;
    private BluetoothSocket btSocket;
    private WashListener washListener;

    //-----------------------------------------------------여기 주소 바꾸기---------------------
    //private final String HC06_ADDRESS = "E0:5A:1B:5F:8E:1E"; //1번
    private final String HC06_ADDRESS = "E0:5A:1B:60:58:9E"; // 2번
    //private final String HC06_ADDRESS = "B8:D6:1A:AC:00:FE"; // test

    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final String WASH_CLEAR = "clear";
    private final String WASH_START = "c";
    private final String WASH_END_RESPONSE = "d";
    private final String START_WASH = "a";

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
        Log.d(TAG, "connectBluetoothWithHC06");
        this.device = btAdapter.getRemoteDevice(HC06_ADDRESS);
        // create & connect socket
        try {
            this.btSocket = createBluetoothSocket(device);
            this.btSocket.connect();
            isConnect = true;
            if (isConnect) {
                Log.d(TAG, "thread 생성");
                connectedThread = new ConnectedThread(btSocket);
                connectedThread.setConnectBluetoothListener(connectListener);
                if (washListener != null) {
                    connectedThread.setWashListener(washListener);
                }
                connectedThread.start();
                connectListener.onSuccess();
            }
        } catch (Exception e) {
            isConnect = false;
            e.printStackTrace();
            if (!isReconnect) {
                Log.d(TAG, "connect onfailed in" + "connectBluetoothWithHC06");
                connectListener.onFailed();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws ErrnoException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            Log.d(TAG, "connect onfailed in" + "createBluetoothSocket");
            connectListener.onFailed();
        }
        try {
            return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "connect onfailed in" + "createBluetoothSocket2");
            connectListener.onFailed();
            return null;
        }
    }

    public void write(String command) throws Throwable {
        if (command.equals(START_WASH)) {
            isWashing = true;
            isReceiveStart = false;
        }
        if (isConnect) {
            Log.d(TAG, "write command in ble: " + command);
            if (command.equals(START_WASH)) {
                new Thread(() -> {
                    // TODO Auto-generated method stub
                    while (true) {
                        try {
                            if (isWashing && !isReceiveStart && connectedThread != null) {
                                connectedThread.write(command);
                            }else if(isReceiveStart || !isWashing){
                                break;
                            }
                            Thread.sleep(5000L);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "connect onfailed in" + "write1");
                            connectListener.onFailed();
                            break;
                        }
                    }
                }).start();
            } else {
                connectedThread.write(command);
            }
        } else {
            Log.d(TAG, "connect: false");
            Log.d(TAG, "connect onfailed in" + "write2");
            connectListener.onFailed();
        }
    }

    public void settingWashConnectListener(WashListener washListener) {
        this.washListener = washListener;
        if (connectedThread != null) {
            connectedThread.setWashListener(washListener);
        }
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public void setReconnect(boolean reconnect) {
        isReconnect = reconnect;
    }

    public void setConnectedThread(ConnectedThread connectedThread) {
        this.connectedThread = connectedThread;
    }
}
