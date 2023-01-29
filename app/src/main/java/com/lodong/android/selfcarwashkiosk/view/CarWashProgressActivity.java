package com.lodong.android.selfcarwashkiosk.view;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.lodong.android.selfcarwashkiosk.Bluetooth.BluetoothInterface;
import com.lodong.android.selfcarwashkiosk.MainApplication;
import com.lodong.android.selfcarwashkiosk.R;
import com.lodong.android.selfcarwashkiosk.callback.WashListener;
import com.lodong.android.selfcarwashkiosk.databinding.ActivityCarWashProgressBinding;
import com.lodong.android.selfcarwashkiosk.roomdb.DBintialization;
import com.lodong.android.selfcarwashkiosk.roomdb.RoomDBVO;
import com.lodong.android.selfcarwashkiosk.util.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class CarWashProgressActivity extends AppCompatActivity {
    private ActivityCarWashProgressBinding binding;
    Intent intent;
    String payType;
    DBintialization database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_car_wash_progress);
        database = DBintialization.getInstance(this, "database");
        BluetoothInterface.getInstance().settingWashConnectListener(getWashListener());
        new Handler().postDelayed(() -> {
            // 실행 코드
            Log.d(TAG, "handler text 변경");
            binding.textView.setText("앞차 세차가 끝나면 결제화면으로 진입합니다.\n\n뒷문이 닫히기 전에 결제 시 세차가 작동하지 않을 수 있습니다.\n\n주의하여 주십시오.");
        }, 1000 * 20);
        try {
            startWash();
            intent = getIntent();
            payType = intent.getStringExtra("payType");

            Log.d(TAG, "onCreate: payType확인" + payType);

            if (payType.equals("CARD")) {

                saveCardRoomData();
            } else {
                savePointRoomData();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "error : " + e, Toast.LENGTH_SHORT).show();
        }

        //집중모드
        Util.hideNavigationView(this);
    }

    private void startWash() throws Throwable {
        if (!MainApplication.getInstance().isBluetoothConnect()) {
            MainApplication.getInstance().connectBluetooth();
        }
        BluetoothInterface.getInstance().write("a");
    }

    private WashListener getWashListener() {
        Log.d(TAG, "getWashListener");
        return () -> {
            new Thread(() -> {
                int time = 0;
                while (true) {
                    // 코드 작성
                    try {
                        Log.d(TAG, "washListener timer" + time);
                        Thread.sleep(1000);
                        time += 1;
                        if (time >= 22) {
                            runOnUiThread(() -> {
                                startActivity(new Intent(CarWashProgressActivity.this, MainActivity.class));
                                finish();
                            });
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        };
    }

    public void saveCardRoomData() {
        String cardPay = "카드승인";
        String advancedWash = "고급세차";

        int paymentMoney = 13_000;

        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy MM dd", Locale.KOREA);

        Date timeDate = new Date();
        Date date = new Date();

        try {
            String formatString = dFormat.format(date);
            date = dFormat.parse(formatString);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        Log.d(TAG, "saveCardRoomData: time" + timeDate);
        Log.d(TAG, "saveCardRoomData: date" + date);

        //------------------

        RoomDBVO dbvo = new RoomDBVO();

        dbvo.setPaymentType(cardPay);
        dbvo.setWashType(advancedWash);
        dbvo.setMoney(paymentMoney);
        dbvo.setTime(timeDate);
        dbvo.setDate(date);
        dbvo.setUnitPrice(13_000);

        // 거래번호, 포인트 카드키
        database.mainDao().insert(dbvo);

    }

    public void savePointRoomData() {

        String cardPay = "포인트승인";
        String advancedWash = "고급세차";

        int paymentMoney = 13_000;

        LocalDateTime localDate = LocalDateTime.now();

        String parseDate = localDate.format(DateTimeFormatter.ofPattern("HH:mm"));
        String dateStr = localDate.format(DateTimeFormatter.ofPattern("yyyy MM dd"));

        Log.d(MotionEffect.TAG, "onCreate: parseDate" + parseDate);

        DateFormat tFormat = new java.text.SimpleDateFormat("HH:mm");
        DateFormat dFormat = new java.text.SimpleDateFormat("yyyy MM dd");
        Date date = new Date();

        try {
            date = dFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date time = new Date();
        try {
            time = tFormat.parse(parseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //------------------

        RoomDBVO dbvo = new RoomDBVO();

        dbvo.setPaymentType(cardPay);
        dbvo.setWashType(advancedWash);
        dbvo.setMoney(paymentMoney);
        dbvo.setTime(time);
        dbvo.setDate(date);
        dbvo.setUnitPrice(13_000);

        // 거래번호, 포인트 카드키
        database.mainDao().insert(dbvo);
    }
}