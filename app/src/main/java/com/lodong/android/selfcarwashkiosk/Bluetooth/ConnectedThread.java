package com.lodong.android.selfcarwashkiosk.Bluetooth;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.lodong.android.selfcarwashkiosk.callback.ConnectBluetoothListener;
import com.lodong.android.selfcarwashkiosk.callback.WashListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private WashListener washListener;
    private ConnectBluetoothListener connectBluetoothListener;

    private boolean isWashing;
    private final String WASH_CLEAR = "clear";

    public ConnectedThread(BluetoothSocket socket) {
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
                if (readed.equals(WASH_CLEAR) && isWashing) {
                    isWashing = false;
                    washListener.onSuccess();
                }
                Log.d(TAG, "run: " + readed);
            } catch (IOException e) {
                e.printStackTrace();
                connectBluetoothListener.onFailed();
                break;
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(String input) throws Exception {
        byte[] bytes = input.getBytes();           //converts entered String into bytes
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
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
        isWashing = washing;
    }

    public void setWashListener(WashListener washListener) {
        this.washListener = washListener;
    }

    public void setConnectBluetoothListener(ConnectBluetoothListener connectBluetoothListener) {
        this.connectBluetoothListener = connectBluetoothListener;
    }
}
