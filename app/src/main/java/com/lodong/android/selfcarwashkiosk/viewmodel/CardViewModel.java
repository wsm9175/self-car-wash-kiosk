package com.lodong.android.selfcarwashkiosk.viewmodel;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.util.Printer;
import android.widget.Toast;

import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.lifecycle.ViewModel;

import com.lodong.android.selfcarwashkiosk.callback.RfidPayListener;
import com.lodong.android.selfcarwashkiosk.outApp.CardActivity;
import com.lodong.android.selfcarwashkiosk.roomdb.DBintialization;
import com.lodong.android.selfcarwashkiosk.roomdb.RoomDBVO;
import com.lodong.android.selfcarwashkiosk.serial.SerialManager;
import com.lodong.android.selfcarwashkiosk.view.CarWashProgressActivity;
import com.lodong.android.selfcarwashkiosk.view.CompletePayActivity;
import com.lodong.android.selfcarwashkiosk.view.MainActivity;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CardViewModel extends ViewModel {

    private WeakReference<Activity> mActivity;

    private SerialManager serialManager;

    public CardViewModel(){}

    public void setParent(Activity parent){
        this.mActivity = new WeakReference<>(parent);
    }

    DBintialization database;

    public void settingSerial(){
        Log.d("settingSerial", "settingSerial: 세팅 시리얼");
        serialManager = SerialManager.getInstance(mActivity.get());
        serialManager.setRfidPayListener(rfidPayListener());
    }
    public void connectSensor() throws Throwable {
        Log.d(TAG, "connectSensor: 커넥트 센서");
        serialManager.settingPrice();
    }
    public void RejectCard(){
        Log.d("태그거부상태 카드", "RejectCard: rejectCard");
        serialManager.rejectCard();
    }

    public RfidPayListener rfidPayListener(){
        return new RfidPayListener() {
            @Override
            public void onSuccess() {

                // 불능 후 next intent
                database = DBintialization.getInstance(mActivity.get(), "database");

                saveRoomData();
                
                serialManager.rejectCard();
                Log.d(TAG, "결제 성공");
                intentCarWashProgress();
            }

            @Override
            public void onFailed() {
                // 불능 후 상태 초기화
                serialManager.rejectCard();
                mActivity.get().runOnUiThread(() -> Toast.makeText(mActivity.get(), "잔액이 부족합니다.", Toast.LENGTH_LONG).show());
                Log.d(TAG, "실패");
                goToMain();
            }
        };
    }

    private void intentCarWashProgress() {
//        mActivity.get().startActivity(new Intent(mActivity.get(), CarWashProgressActivity.class));
//        mActivity.get().finish();

        mActivity.get().startActivity(new Intent(mActivity.get(), CompletePayActivity.class));

    }

    private void goToMain(){
        mActivity.get().startActivity(new Intent( mActivity.get(), MainActivity.class));
        mActivity.get().finish();
    }

    public void saveRoomData(){

        String cardPay = "포인트승인";
        String advancedWash = "고급세차";

        int paymentMoney = 13_000;

        LocalDateTime localDate = LocalDateTime.now();

        String parseDate = localDate.format(DateTimeFormatter.ofPattern("HH:mm"));
        String dateStr = localDate.format(DateTimeFormatter.ofPattern("yyyy MM dd"));

        Log.d(MotionEffect.TAG, "onCreate: parseDate" + parseDate);

        DateFormat tFormat = new java.text.SimpleDateFormat("HH:mm");
        DateFormat dFormat = new java.text.SimpleDateFormat("yyyy MM dd");
        Date date =  new Date();

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
