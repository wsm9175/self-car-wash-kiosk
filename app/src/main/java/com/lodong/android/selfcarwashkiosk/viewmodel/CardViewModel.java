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

        Intent intent = new Intent(mActivity.get(), CompletePayActivity.class);
        intent.putExtra("payType", "POINT");

        mActivity.get().startActivity(intent);

    }

    private void goToMain(){
        mActivity.get().startActivity(new Intent( mActivity.get(), MainActivity.class));
        mActivity.get().finish();
    }


}
