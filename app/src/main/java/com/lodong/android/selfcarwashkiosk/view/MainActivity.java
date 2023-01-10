package com.lodong.android.selfcarwashkiosk.view;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.lodong.android.selfcarwashkiosk.outApp.Util.insertBytes;
import static com.lodong.android.selfcarwashkiosk.outApp.Util.recreatePacketByApprovalFormat;
import static com.lodong.android.selfcarwashkiosk.outApp.Util.recreatePacketBySubFuncFormat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.system.ErrnoException;
import android.util.Log;
import android.view.View;

import com.lodong.android.selfcarwashkiosk.MainApplication;
import com.lodong.android.selfcarwashkiosk.R;
import com.lodong.android.selfcarwashkiosk.databinding.ActivityMainBinding;
import com.lodong.android.selfcarwashkiosk.util.Util;
import com.lodong.android.selfcarwashkiosk.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    Intent actionMain = new Intent(Intent.ACTION_MAIN);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);
        viewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MainViewModel.class);
        viewModel.setParent(this);

        Log.d(TAG, "onCreate: 메인들어옴");


        connectReapeat();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //집중모드
        Util.hideNavigationView(this);
        //Lock모드
        //Util.startLock(this);
    }


    public void intentChoiceModeActivity(){
        startActivity(new Intent(this, ChoicePayModeActivity.class));
    }

    public void intentSalesInquiry(){
        startActivity(new Intent(this, ManagerMonth.class));
    }

    public void connectReapeat(){

        Log.d(ContentValues.TAG, "connectReapeat: connectReapeat들어옴");

        byte[] RequestTelegram = makeSubFunc("UC");
        ComponentName componentName = new ComponentName("com.ksnet.kscat_a","com.ksnet.kscat_a.PaymentIntentActivity");
        actionMain = new Intent(Intent.ACTION_MAIN);
        actionMain.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        actionMain.addCategory(Intent.CATEGORY_LAUNCHER);
        actionMain.setComponent(componentName);
        actionMain.putExtra("Telegram", RequestTelegram);
        actionMain.putExtra("TelegramLength", RequestTelegram.length);
        startActivityForResult(actionMain, 0);

        Log.d(TAG, "connectReapeat: 연결됨");

    }

    public byte[] makeSubFunc(String transType)
    {
        byte[] packet = transType.getBytes();

        if(transType.equals("S3"))
        {
            packet = insertBytes(packet, packet.length, "R".getBytes());             // (1) 기기구분
            packet = insertBytes(packet, packet.length, "         ".getBytes());             // (9) 여유필드
            packet = recreatePacketBySubFuncFormat(packet);
        }
        else if(transType.equals("S4"))
        {
            packet = insertBytes(packet, packet.length, "   ".getBytes());             // (3) 여유필드
            packet = recreatePacketBySubFuncFormat(packet);
        }

        else if (transType.equals("LT")){
            packet = insertBytes(packet, packet.length, "   ".getBytes());             // (3) filler
            packet = recreatePacketBySubFuncFormat(packet);
        }

        else if (transType.equals("UC")){
            packet = insertBytes(packet, packet.length, "   ".getBytes());             // (3) filler
            packet = recreatePacketBySubFuncFormat(packet);
        }

        else if(transType.equals("DW"))
        {
            // 필수 : 단말기번호, 사업자 번호
            packet = insertBytes(packet, packet.length, "DPT0TEST05".getBytes());                   // (10) 단말기번호
            packet = insertBytes(packet, packet.length, "    ".getBytes());                         // ( 4) 업체 정보
            packet = insertBytes(packet, packet.length, "            ".getBytes());                 // (12) 거래일련번호
            packet = insertBytes(packet, packet.length, "1208197322".getBytes());                   // (10) 사업자번호
            packet = insertBytes(packet, packet.length, "                         ".getBytes());    // (25) 여유필드

            packet = recreatePacketByApprovalFormat(packet);

        }

        else if(transType.equals("ST"))
        {
            packet = insertBytes(packet, packet.length, "                    ".getBytes());         // (9) 여유필드
            packet = recreatePacketBySubFuncFormat(packet);
        }

        return packet;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (resultCode == RESULT_OK ){

            Log.d(TAG, "onActivityResult: 괜찮음");
            
        }else{

            Log.d(TAG, "onActivityResult: 실패");
            
        }
        
    }
}