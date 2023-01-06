package com.lodong.android.selfcarwashkiosk.outApp;

import static android.content.ContentValues.TAG;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.lodong.android.selfcarwashkiosk.R;
import com.lodong.android.selfcarwashkiosk.view.CarWashProgressActivity;
import com.lodong.android.selfcarwashkiosk.view.CompletePayActivity;
import com.lodong.android.selfcarwashkiosk.view.MainActivity;
import com.lodong.android.selfcarwashkiosk.viewmodel.CardViewModel;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CardActivity extends AppCompatActivity {
    private Intent mIntent;
    private byte[] mRequestTelegram;
    private String mDeviceNo = "AT0356011A";
    private String mTotAmt = "";
    private String mVat = "";
    private String mSupAmt = "";
    private TransactionData trData;
    private ImageView iv;

    private CardViewModel viewModel;

    int tempPayMethod = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_card);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(CardViewModel.class);
        viewModel.setParent(this);

        Log.d(TAG, "onCreate: 카드 액티비티 확인");
        //payMethod = 1 - 카드, payMethod = 2 - RFID 결제
        int payMethod = getIntent().getIntExtra("payMethod", 0);

        tempPayMethod = payMethod;

        //집중모드
        com.lodong.android.selfcarwashkiosk.util.Util.hideNavigationView(this);
        if (payMethod == 1) {
            cardPay();
        } else if (payMethod == 2) {
            viewModel.settingSerial();
            try {
                viewModel.connectSensor();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            //접근 오류 처리
            intentCarWashProgress();
        }
    }

    void cardPay() {
        // IC 승인
        iv = findViewById(R.id.imageView);

        Log.d("승인요청", "승인요청승인요청승인");
        makeTelegramIC("1");
        ComponentName componentName = new ComponentName("com.ksnet.kscat_a", "com.ksnet.kscat_a.PaymentIntentActivity");
        mIntent = new Intent(Intent.ACTION_MAIN);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mIntent.setComponent(componentName);
        mIntent.putExtra("Telegram", mRequestTelegram);
        mIntent.putExtra("TelegramLength", mRequestTelegram.length);
        String str = Util.HexDump.dumpHexString(mRequestTelegram);
        Log.d(TAG, "dumpHex : " + str);
        startActivityForResult(mIntent, 0);
    }

    private void makeTelegramIC(String ApprCode) {
        ByteBuffer bb = ByteBuffer.allocate(4096);

        bb.put((byte) 0x02);                                                 // STX
        bb.put("IC".getBytes());                                            // 거래구분
        // bb.put("MS".getBytes());                                         // 거래구분
        bb.put("01".getBytes());                                            // 업무구분

        if (ApprCode.equals("1"))
            bb.put("0200".getBytes());                                      // 전문구분
        else if (ApprCode.equals("0"))
            bb.put("0420".getBytes());                                      // 전문구분
        bb.put("N".getBytes());                                             // 거래형태

        Log.d(TAG, "makeTelegramIC: 거래형태 확인" + "N".getBytes(StandardCharsets.UTF_8));

        bb.put(mDeviceNo.getBytes());                                        // 단말기번호

        Log.d(TAG, "makeTelegramIC: 단말기 번호 확인" + mDeviceNo.getBytes());

        for (int i = 0; i < 4; i++) bb.put(" ".getBytes());                     // 업체정보
        for (int i = 0; i < 12; i++) bb.put(" ".getBytes());                     // 전문일련번호
        // bb.put("K".getBytes());                                          // POS Entry Mode   // MS
        bb.put("S".getBytes());                                             // POS Entry Mode   // IC
        for (int i = 0; i < 20; i++) bb.put(" ".getBytes());                     // 거래 고유 번호
        for (int i = 0; i < 20; i++) bb.put(" ".getBytes());                     // 암호화하지 않은 카드 번호
        bb.put("9".getBytes());                                             // 암호화여부
        bb.put("################".getBytes());
        bb.put("################".getBytes());
        for (int i = 0; i < 40; i++) bb.put(" ".getBytes());                     // 암호화 정보
        // bb.put("4330280486944821=17072011025834200000".getBytes());      // Track II - MS
        // bb.put("123456789012345612345=8911           ".getBytes());      // Track II - App카드
        for (int i = 0; i < 37; i++) bb.put(" ".getBytes());                     // Track II - IC
        bb.put(Util.FS);                                                    // FS

        bb.put("00".getBytes());                         // 할부개월

        String totAmt = "13000";
        Util.CalcTax tax = new Util.CalcTax();
        tax.setConfig(Long.parseLong(totAmt), 10, 0);

        bb.put(Util.stringToAmount(totAmt, 12).getBytes());           // 총금액
        mTotAmt = Util.stringToAmount(totAmt, 12);

        bb.put(Util.stringToAmount(String.valueOf(tax.getServiceAmt()), 12).getBytes());    // 봉사료
        bb.put(Util.stringToAmount(String.valueOf(tax.getVAT()), 12).getBytes());           // 세금
        mVat = Util.stringToAmount(String.valueOf(tax.getVAT()), 12);
        bb.put(Util.stringToAmount(String.valueOf(tax.getSupplyAmt()), 12).getBytes());     // 공급금액
        mSupAmt = Util.stringToAmount(String.valueOf(tax.getSupplyAmt()), 12);
        bb.put("000000000000".getBytes());                                  // 면세금액
        bb.put("AA".getBytes());                                            // Working Key Index
        for (int i = 0; i < 16; i++) bb.put("0".getBytes());                     // 비밀번호

        String getDate = "";

        if (ApprCode.equals("1")) {
            bb.put("            ".getBytes());                              // 원거래승인번호
            bb.put("      ".getBytes());                                    // 원거래승인일자
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String orgDate = sdf.format(date);

            getDate = orgDate;

            Log.d(TAG, "now Date : " + orgDate);
            String orgApprNo = String.format("%-12s", "12411789012");

            bb.put(orgApprNo.getBytes());                                   // 원거래승인번호
            bb.put(orgDate.getBytes());                                     // 원거래승인일자
        }
        //for(int i=0; i<163; i++) bb.put(" ".getBytes());                    // 사용자정보 ~ DCC 환율조회 Data
        for (int i = 0; i < 15; i++) bb.put(" ".getBytes());

        bb.put("TESTTEST                      ".getBytes());

        for (int i = 0; i < 118; i++) bb.put(" ".getBytes());

        // EMV Data Length(4 bytes)
        // EMV Data
        //bb.put(" ".getBytes());                                             // 전자서명 유무
        bb.put("N".getBytes());

        //bb.put("S".getBytes());                                              // 전자서명 유무
        //bb.put("83".getBytes());                                          // 전자서명 암호화 Key Index


        //for(int i=0; i<16; i++) bb.put("0".getBytes());                   // 제품코드 및 버전        // KN1512021C000002
        //bb.put("KSPS2SP210600051".getBytes());
        //bb.put("0108".getBytes());
        //bb.put(String.format("%04d",  encBmpData.length()).getBytes());   // 전자서명 길이          // 0248
        //bb.put(encBmpData.getBytes());                                    // 전자서명              // 716634346E5567636D7737777643756E7666596D797934554647657A38764A784B2F7744545657554F72586341586A6954365441594E6E6F692B69412B572F49316B6D7072326744716C4B4B2F75624D6C6E684E6F346F4B7A54413757314578774F5975746E5A726759547166357244466238356B37516D50484D3057416B59547153755959432F71326D414E6B613042543841555A4B795556544179685341464C327442565857772F396A4C34554F306574594C696B54596535794C486858437A4568756B6A434448766D6F4B3449694D6D32753570507739654B442F564D387A312F594D6A3966787A4D396A6753435A6F6B76773D3D

        //bb.put(Util.toByte("716634346E5567636D7737777643756E7666596D797934554647657A38764A784B2F7744545657554F72586341586A6954365441594E6E6F692B69412B572F49316B6D7072326744716C4B4B2F75624D6C6E684E6F346F4B7A54413757314578774F5975746E5A726759547166357244466238356B37516D50484D3057416B59547153755959432F71326D414E6B613042543841555A4B795556544179685341464C327442565857772F396A4C34554F306574594C696B54596535794C486858437A4568756B6A434448766D6F4B3449694D6D32753570507739654B442F564D387A312F594D6A3966787A4D396A6753435A6F6B76773D3D"));
        //bb.put(Util.toByte("74497A564939432B776A634E727144784C48574D74682F43756442764F5139304F5243514A47546B594B546B4A6446756E42634E513764492F61704F32564D314F43794B305352494E5A4747757333576D523774324C413277784D7337314B7954676470744F392F576C593D"));

        bb.put((byte) 0x03);                                                 // ETX
        bb.put((byte) 0x0D);                                                 // CR

        byte[] telegram = new byte[bb.position()];
        bb.rewind();
        bb.get(telegram);

        getInfo(totAmt, mDeviceNo, getDate);

        mRequestTelegram = new byte[telegram.length + 4];
        String telegramLength = String.format("%04d", telegram.length);
        System.arraycopy(telegramLength.getBytes(), 0, mRequestTelegram, 0, 4);
        System.arraycopy(telegram, 0, mRequestTelegram, 4, telegram.length);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");

        trData = new TransactionData();
//
        if (resultCode == RESULT_OK && data != null) {

            Log.d(TAG, "onActivityResult-1");
            Toast.makeText(this, "성공", Toast.LENGTH_LONG).show();

            // 여기에서 영수증 출력함
            //--------------------------------------------------------------------------------------------------------------------------------------------------


            byte[] recvByte = data.getByteArrayExtra("responseTelegram");
            // Log.e("KSCAT_INTENT_RESULT", HexDump.dumpHexString(recvByte));
            // Util.byteTo20ByteLog(recvByte, "");
            Log.e("Recv Telegram \n", Util.HexDump.dumpHexString(recvByte));

            String str = Util.HexDump.dumpHexString(recvByte);

            // 승인번호 승인일자 가져오기
            byte[] apprNo = new byte[12];
            System.arraycopy(recvByte, 94, apprNo, 0, 12);
            byte[] apprDate = new byte[6];
            System.arraycopy(recvByte, 49, apprDate, 0, 6);

            Log.d(TAG, "결제 intent 수행");
            Intent intent = new Intent(CardActivity.this, ResultActivity.class);
            intent.putExtra("PayType", "CARD");
            intent.putExtra("resData", recvByte);
            intent.putExtra("totAmt", mTotAmt);
            intent.putExtra("VAT", mVat);
            intent.putExtra("supplyAmt", mSupAmt);
            //startActivity(intent);
            intentCarWashProgress();
        } else if (resultCode == RESULT_CANCELED) {
            if (data != null) {
                Log.d(TAG, "onActivityResult-canceled");
//                finish();
                Log.e("result", String.valueOf(data.getIntExtra("result", 1)));
                byte[] recvByte = data.getByteArrayExtra("responseTelegram");
                Log.e("recvByte : \n", Util.HexDump.dumpHexString(recvByte));
                trData.SetData(recvByte);

                try {
                    String csMessage1 = new String(trData.message1, "EUC-KR");
                    String csMessage2 = new String(trData.message2, "EUC-KR");
                    String csNotice1 = new String(trData.notice1, "EUC-KR");
                    String csNotice2 = new String(trData.notice2, "EUC-KR");

                    String msg = csMessage1 + "\n" + csMessage2 + "\n" + csNotice1 + "\n" + csNotice2;


                   /* new android.app.AlertDialog.Builder(this).setTitle("KSCAT_TEST").setMessage(msg).setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();*/
                    String str = Util.HexDump.dumpHexString(recvByte);
                    Toast.makeText(getApplicationContext(), "결제 실패했습니다.", Toast.LENGTH_LONG).show();
                    goToMain();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "결제 실패했습니다.", Toast.LENGTH_LONG).show();
                    goToMain();
                }
            } else {
                Toast.makeText(this, "앱 호출 실패", Toast.LENGTH_LONG).show();
                goToMain();
            }
        }
    }

    public void getInfo(String tAmont, String deviceNo, String date){

        Log.d(TAG, "getInfo: 총합계" + tAmont);
        Log.d(TAG, "getInfo: 디바이스 넘버" + deviceNo);
        Log.d(TAG, "getInfo: 데이트" + date);

    }

    private void intentCarWashProgress() {
        /*
        원래 이 코드임
        startActivity(new Intent(this, CarWashProgressActivity.class));
        finish();
        */
       
        // 오류처리 할때사용, onActivityResult 에서 결제성공했을때 사용



        Intent intent = new Intent(this, CompletePayActivity.class);
        intent.putExtra("mTotAmt", mTotAmt);
        intent.putExtra("mDeviceNo", mDeviceNo);
        intent.putExtra("date", getDate());
        intent.putExtra("payType", tempPayMethod);
        startActivity(intent);

    }

    private void goToMain(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public String getDate(){

        String str = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss"));
        return str;
    }
}
 