package com.lodong.android.selfcarwashkiosk.serial;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.system.ErrnoException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.LongDef;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.lodong.android.selfcarwashkiosk.callback.ConnectBluetoothListener;
import com.lodong.android.selfcarwashkiosk.callback.ConnectSerialListener;
import com.lodong.android.selfcarwashkiosk.callback.RfidPayListener;
import com.lodong.android.selfcarwashkiosk.callback.WatchConnectListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SerialManager implements SerialInputOutputManager.Listener {
    private static SerialManager instance;
    private final String TAG = SerialManager.class.getSimpleName();
    private Context context;
    private RfidPayListener rfidPayListener;

    //센서 관련 변수
    private SerialInputOutputManager usbIoManager;
    private UsbSerialPort port;
    private static final String ACTION_USB_PERMISSION =
            "com.lodong.android.pressuregagealarm.USB_PERMISSION";
    private UsbManager usbManager;
    private PendingIntent permissionIntent;

    private ConnectSerialListener connectSerialListener;

    private int nowDataPosition = 0;

    private byte[] newDate = new byte[6];

    private final byte[] SETTING_PRICE = {(byte) 0xF1, (byte) 0x0A, (byte) 0x00, (byte) 0x13, (byte) 0x00, (byte) 0xF2};

    //카드를 태그할때
    private final byte[] CARD_TAG = {(byte) 0xF1, (byte) 0x0B, (byte) 0x00, (byte) 0x13, (byte) 0x00, (byte) 0xF2};

    // 잔액부족시
    private final byte[] CARD_SHOTAGE = {(byte) 0xF1, (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xF2};

    // 4. 리더기 태그 거부상태
    private final byte[] CARD_REJECT_TAG = {(byte) 0xF1, (byte) 0x0C, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xF2};

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication

                        }
                    } else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };

    private SerialManager(Context context) {
        this.context = context;
    }

    public static SerialManager getInstance(Context context) {
        if (instance == null) {
            instance = new SerialManager(context);
        }
        return instance;
    }

    public void settingSerial() {
        this.usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        permissionIntent = PendingIntent.getBroadcast(this.context, 0, new Intent(ACTION_USB_PERMISSION), 0 | PendingIntent.FLAG_IMMUTABLE);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(usbReceiver, filter);
    }

    public void connectSensor() throws ErrnoException {
        Log.d(TAG, "settingSensor");

        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(this.usbManager);
        if (availableDrivers.isEmpty()) {
            Log.d(TAG, "usb serial null");
            return;
        }
        UsbSerialDriver connectSerialDriver = null;
        for (UsbSerialDriver serialDriver : availableDrivers) {
            Log.d(TAG, serialDriver.getDevice().getDeviceName());
            if (serialDriver.getDevice().getDeviceName().contains("/dev/bus/usb/001/")) {
                connectSerialDriver = serialDriver;
                break;
            }
        }

        //usb가 총 2개 연결 되므로 사이즈 체크
        if (availableDrivers.size() < 2) {
            Log.d(TAG, "availableDrivers.size() != 2");
            connectSerialListener.onFailed();
            return;
        }

        UsbDeviceConnection connection = this.usbManager.openDevice(connectSerialDriver.getDevice());

        if (connection == null) {
            return;
        }
        port = connectSerialDriver.getPorts().get(0);
        try {
            Log.d(TAG, "connectSensor: try안에 진입" + connectSerialDriver.getDevice().getDeviceId());
            Log.d(TAG, "connectSensor: connection 확인" + connection);
            port.open(connection);
            port.setParameters(19200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            Log.d(TAG, "connectSensor: 포트 오픈" + UsbSerialPort.STOPBITS_1);
        } catch (IOException e) {
            connectSerialListener.onFailed();
            e.printStackTrace();
        }

        try {
            usbIoManager = new SerialInputOutputManager(port, this);
            usbIoManager.start();
            connectSerialListener.onSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            connectSerialListener.onFailed();
            Toast.makeText(context, "연결상태를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "usb start");
    }

    public void settingPrice() throws Throwable {
        try {
            port.write(SETTING_PRICE, 100);
            Log.d(TAG, "write");
        } catch (IOException e) {
            connectSerialListener.onFailed();
            e.printStackTrace();
        }
    }

    // 태그 거부
    public void rejectCard() {
        try {
            Log.d(TAG, "RejectCard: 태그 거부하겠다.");
            port.write(CARD_REJECT_TAG, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewData(byte[] data) {
        //Log.d(TAG, "read : " + new String(data));
        Log.d(TAG, "onNewData: " + new String(data));
        Log.d(TAG, "data size = " + data.length);
        Log.d(TAG, "nowDataPosition = " + nowDataPosition);
        for(Byte b : data){
            Log.d(TAG, "data : " + b);
        }

        /*
        * 정면 기준 오른쪽
        * */
        for(Byte d : data){
            newDate[nowDataPosition] = d;
            if (nowDataPosition == newDate.length - 1) {
                //판별 결제 성공 or 실패
                if (Arrays.equals(newDate, CARD_TAG)) {
                    rfidPayListener.onSuccess();
                } else if (Arrays.equals(newDate, CARD_SHOTAGE)) {
                    rfidPayListener.onFailed();
                }
                nowDataPosition = 0;
                this.newDate = new byte[6];
            } else {
                nowDataPosition++;
            }
        }
    }

    public void setConnectSerialListener(ConnectSerialListener connectSerialListener) {
        this.connectSerialListener = connectSerialListener;
    }

    @Override
    public void onRunError(Exception e) {

    }

    public void setRfidPayListener(RfidPayListener rfidPayListener) {
        this.rfidPayListener = rfidPayListener;
    }
}