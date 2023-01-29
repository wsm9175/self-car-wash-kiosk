package com.lodong.android.selfcarwashkiosk.Bluetooth;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.lodong.android.selfcarwashkiosk.callback.ConnectBluetoothListener;
import com.lodong.android.selfcarwashkiosk.callback.WashListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private final String TAG = ConnectedThread.class.getSimpleName();
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private WashListener washListener;
    private ConnectBluetoothListener connectBluetoothListener;
    private final String WASH_CLEAR = "clear";
    private final String WASH_START = "c";
    private final String WASH_END_RESPONSE = "d";
    private final String START_WASH = "a";

    public ConnectedThread(BluetoothSocket socket) {
        Log.d(TAG, "ConnectedThread 생성");
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                Log.d(TAG, "connect onfailed in" + "connectThread");
                connectBluetoothListener.onFailed();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    @Override
    public void run() {
        Log.d(TAG, "run 실행");
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                // Read from the InputStream.
                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI activity.
                String readed = new String(buffer, 0, bytes);
                // 세차 끝나는 경우 관련
                if (readed.equals(WASH_CLEAR) && BluetoothInterface.isWashing) {
                    BluetoothInterface.isWashing = false;
                    write(WASH_END_RESPONSE);
                    washListener.onSuccess();
                } else if (readed.equals(WASH_CLEAR)) {
                    write(WASH_END_RESPONSE);
                }
                //세차 시작 관련
               /* if (!BluetoothInterface.isReceiveStart && BluetoothInterface.isWashing) {
                    write(START_WASH);
                }*/
                if (readed.equals(WASH_START)) {
                    BluetoothInterface.isReceiveStart = true;
                }
                Log.d(TAG, "run: " + readed);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "connect onfailed in" + "run");
                connectBluetoothListener.onFailed();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(String input) throws Exception {
        Log.d(TAG, "write : " + input);
        byte[] bytes = input.getBytes();           //converts entered String into bytes
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.d(TAG, "connect onfailed in" + "write");
            connectBluetoothListener.onFailed();
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
        }
    }

    public void setWashing(boolean washing) {
        BluetoothInterface.isWashing = washing;
    }

    public void setWashListener(WashListener washListener) {
        this.washListener = washListener;
    }

    public void setConnectBluetoothListener(ConnectBluetoothListener connectBluetoothListener) {
        this.connectBluetoothListener = connectBluetoothListener;
    }

    public void setReceiveStart(boolean receiveStart) {
        BluetoothInterface.isReceiveStart = receiveStart;
    }

}
