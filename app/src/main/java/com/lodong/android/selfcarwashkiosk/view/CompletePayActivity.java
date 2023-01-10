package com.lodong.android.selfcarwashkiosk.view;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.bxl.config.editor.BXLConfigLoader;
import com.lodong.android.selfcarwashkiosk.Print.BixolonPrinter;
import com.lodong.android.selfcarwashkiosk.R;
import com.lodong.android.selfcarwashkiosk.databinding.ActivityCompletePayBinding;
import com.lodong.android.selfcarwashkiosk.outApp.TransactionData;
import com.lodong.android.selfcarwashkiosk.util.Util;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CompletePayActivity extends AppCompatActivity {
    private ActivityCompletePayBinding binding;

    private final int REQUEST_PERMISSION = 0;

    private static BixolonPrinter bxlPrinter = null;

    private final int portType = BXLConfigLoader.DEVICE_BUS_USB;
    private final String logicalName = "BK3-3";
    private final String address = "";

    // 영수증 데이터 얻어오기
    byte[] mResData;
    TransactionData trData;
    String mTotAmt = "";
    String mVat = "";
    String mSupAmt = "";
    String mPersonalNo = "";
    String mTraderType = "";
    String mPayType = "";

    // 카드 번호
    String cardNum;

    // 카드 명칭
    String cardCategory;

    // 매입사명
    String purchaseCompany;

    // 단말번호
    String deviceNumber;

    // 가맹번호
    String merchantNumber;

    // 승인번호
    String approvalNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_complete_pay);
        binding.setActivity(this);

        bxlPrinter = new BixolonPrinter(getApplicationContext());

        Thread.setDefaultUncaughtExceptionHandler(new AppUncaughtExceptionHandler());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                }
            }
        }
        setBxlPrinter();

        //집중모드
        Util.hideNavigationView(this);



    }

    public void setBxlPrinter(){
        new Thread(() -> {
            // TODO Auto-generated method stub

            Log.d("TAG", "onClick: 포트 타입 확인" + portType);
            Log.d("TAG", "onClick: 로지컬 네임" + logicalName);
            Log.d("TAG", "onClick: 어드레스 주소" + address);

            if (bxlPrinter.printerOpen(portType, logicalName, address, true)) {

                //finish();
            } else {
                Log.d(TAG, "onClick: Fail to printer open");
                //mHandler.obtainMessage(1, 0, 0, "Fail to printer open!!").sendToTarget();

            }
        }).start();
    }

    public void lastPrint() {

        Intent intent = new Intent();
        String total = intent.getStringExtra("mTotAmt");
        String deviceNo = intent.getStringExtra("mDeviceNo");
        String date=  intent.getStringExtra("date");
        int payType = intent.getIntExtra("payType", 0);


        // 시간 가져오기 2023-01-06 12:50:11


        // 영수증 형식 20230106-01-0004

        if(payType == 1){
            setCardPrint(total, deviceNo, date);
        }else{
            setPointPrint(total, deviceNo, date);
        }

        intentNonPrint();


    }

    public void setCardPrint(String total, String deviceNo, String date){

        String strData =
                " [ 영 수 증 ] \n";

        int alignment, attribute = 0;

        try {
            makeReceiptData();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        alignment = BixolonPrinter.ALIGNMENT_CENTER;
//        attribute = BixolonPrinter.ATTRIBUTE_FONT_A;
        attribute = BixolonPrinter.ATTRIBUTE_BOLD;

        bxlPrinter.printText(strData, alignment, attribute, 2);

        alignment = BixolonPrinter.ALIGNMENT_LEFT;
        attribute = BixolonPrinter.ATTRIBUTE_FONT_A;

        String dateStr = getDate();
        String receiptDate = getReceiptDate(dateStr);

        strData = "\n[매장명]  워시큐브  목포센터/537-87-01157\n" +
                "[주소]    전남  목포시 영산로 792\n" +
                "[대표자]  이름   [TEL] 061-282-3782\n" +
                "[매출일]  " + dateStr +"\n"+
                "[영수증]  " + receiptDate + " \n" +
                "==============================================\n" +
                "   상 품 명       단 가     수 량       금 액\n" +
                "----------------------------------------------\n" +
                "고급세차" + "          13,000 " + "     1 " + "       13,000\n" +
                "----------------------------------------------\n" +
                "합 계  금 액                           13,000\n" +
                "----------------------------------------------\n" +
                "               부가세 과세물품가액     11,700\n" +
                "               부      가     세        1,300\n" +
                "----------------------------------------------\n" +
                "받 을 금액                             13,000\n" +
                "받 은 금액                             13,000\n" +
                "----------------------------------------------\n" +
                "신용  카드                             13,000\n" +
                "----------------------------------------------\n";

        bxlPrinter.printText(strData, alignment, attribute, 1);

        strData = "***  신용승인정보(고객용)  [1]  ***\n";

        alignment = BixolonPrinter.ALIGNMENT_CENTER;

        bxlPrinter.printText(strData, alignment, attribute, 1);



        strData = "카드/매입사: "+ cardCategory + " / " + purchaseCompany + "\n" + /* 데이터 받아오기*/
                "카 드 번 호: " + cardNum + " \n" + /* 데이터 받아오기*/
                "승 인 금 액: 13,000(일시불)\n" +
                "승인/가맹점: " + approvalNumber + " / " + merchantNumber + "\n" + /* 데이터 받아오기*/
                "승 인 일 시: " + date + "\n" + /* 데이터 받아오기*/
                "단말기 번호: " + deviceNumber + "\n" + /* 데이터 받아오기*/
                "     NOTICE:  무서명거래\n" +
                "---------------------------------------------";

        alignment = BixolonPrinter.ALIGNMENT_LEFT;

        bxlPrinter.printText(strData, alignment, attribute, 1);

        bxlPrinter.cutPaper();
    }

    public void setPointPrint(String total, String deviceNo, String date){
        String strData =
                " [ 영 수 증 ] \n";

        int alignment, attribute = 0;

        alignment = BixolonPrinter.ALIGNMENT_CENTER;
//        attribute = BixolonPrinter.ATTRIBUTE_FONT_A;
        attribute = BixolonPrinter.ATTRIBUTE_BOLD;

        bxlPrinter.printText(strData, alignment, attribute, 2);

        alignment = BixolonPrinter.ALIGNMENT_LEFT;
        attribute = BixolonPrinter.ATTRIBUTE_FONT_A;

        String dateStr = getDate();
        String receiptDate = getReceiptDate(dateStr);

        strData = "\n[매장명]  워시큐브  목포센터\n" +
                "[주소]    전남  목포시 영산로 792\n" +
                "[대표자]  이름   [TEL] 061-282-3782\n" +
                "[매출일]  " + dateStr +"\n"+
                "[영수증]  " + receiptDate + " \n" +
                "==============================================\n" +
                "   상 품 명       단 가     수 량       금 액\n" +
                "----------------------------------------------\n" +
                "고급세차" + "          13,000 " + "     1 " + "       13,000\n" +
                "----------------------------------------------\n" +
                "합 계  금 액                           13,000\n" +
                "----------------------------------------------\n" +
                "               부가세 과세물품가액     11,700\n" +
                "               부      가     세        1,300\n" +
                "----------------------------------------------\n" +
                "받 을 금액                             13,000\n" +
                "받 은 금액                             13,000\n" +
                "----------------------------------------------\n" +
                "회 원 카 드                            13,000\n" +
                "----------------------------------------------\n";

        bxlPrinter.printText(strData, alignment, attribute, 1);


        bxlPrinter.cutPaper();

    }



    public String getDate(){

        String str = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss"));
        return str;
    }

    public String getReceiptDate(String str){

        str = str.replace("-", "").substring(0, 8);

        return str;
    }

    public void intentPrint(){

        lastPrint();

    }


    public void intentNonPrint(){
        startActivity(new Intent(this, CarWashProgressActivity.class));
        finish();
    }

    public class AppUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, final Throwable ex) {
            ex.printStackTrace();

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }

    public void makeReceiptData() throws UnsupportedEncodingException {
        Intent intent = getIntent();

        mPayType = intent.getStringExtra("payType");
        mResData = intent.getByteArrayExtra("resData");
        mTotAmt = intent.getStringExtra("totAmt");
        mVat = intent.getStringExtra("VAT");
        mSupAmt = intent.getStringExtra("supplyAmt");
        mPersonalNo = intent.getStringExtra("personalNo");
        mTraderType = intent.getStringExtra("traderType");

        trData = new TransactionData();
        trData.SetData(mResData);

        //StringBuffer cardNum = new StringBuffer();

        // 카드 번호
        cardNum = new String(trData.filler, "EUC-KR");

        // 카드 명칭
        cardCategory = new String(trData.cardCategoryName, "EUC-KR");

        // 매입사명
        purchaseCompany = new String(trData.purchaseCompanyName, "EUC-KR");

        // 단말번호
        deviceNumber = new String(trData.deviceNumber, "EUC-KR");

        // 가맹번호
        merchantNumber = new String(trData.merchantNumber, "EUC-KR");

        // 승인번호
        approvalNumber = new String(trData.approvalNumber, "EUC-KR");


    }


}