package com.lodong.android.selfcarwashkiosk.outApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lodong.android.selfcarwashkiosk.R;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class ResultActivity extends AppCompatActivity
{
    Intent mIntent;
    byte[] mResData;
    TransactionData trData;
    String mTotAmt = "";
    String mVat = "";
    String mSupAmt = "";
    String mPersonalNo = "";
    String mTraderType = "";
    String mPayType = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getIntentData();
        parseData();
        try
        {
            makeReceipt();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    void getIntentData()
    {
        mIntent = getIntent();
        mPayType = mIntent.getStringExtra("PayType");
        mResData = mIntent.getByteArrayExtra("resData");
        mTotAmt = mIntent.getStringExtra("totAmt");
        mVat = mIntent.getStringExtra("VAT");
        mSupAmt = mIntent.getStringExtra("supplyAmt");
        mPersonalNo = mIntent.getStringExtra("personalNo");
        mTraderType = mIntent.getStringExtra("traderType");
    }

    void parseData()
    {
        trData = new TransactionData();
        trData.SetData(mResData);
    }

    void makeReceipt() throws UnsupportedEncodingException
    {
        StringBuffer sb1 = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        StringBuffer sb3 = new StringBuffer();
        StringBuffer sb4 = new StringBuffer();

        TextView tv = findViewById(R.id.headerText);

        if(mPayType.equals("CARD"))
        {
            if(Arrays.equals(trData.transferCode, "0210".getBytes()))
                tv.setText("[신용 승인]");
            else if(Arrays.equals(trData.transferCode, "0430".getBytes()))
                tv.setText("[신용 승인 취소]");
            /////////////////////////////////////////////////////////////
            sb1.append("카드번호 : ");
            sb1.append(new String(trData.filler, "EUC-KR"));
            sb1.append("\n");

            sb1.append("카드명칭 : ");
            sb1.append(new String(trData.cardCategoryName, "EUC-KR"));
            sb1.append("\n");

            sb1.append("매입사명 : ");
            sb1.append(new String(trData.purchaseCompanyName, "EUC-KR"));
            /////////////////////////////////////////////////////////////
            sb2.append("단말번호 : ");
            sb2.append(new String(trData.deviceNumber, "EUC-KR"));
            sb2.append("\n");

            sb2.append("가맹번호 : ");
            sb2.append(new String(trData.merchantNumber, "EUC-KR"));
            sb2.append("\n");

            sb2.append("승인일시 : ");
            sb2.append(new String(trData.transferDate, "EUC-KR"));
            sb2.append("\n");

            sb2.append("승인번호 : ");
            sb2.append(new String(trData.approvalNumber, "EUC-KR"));
            sb2.append("\n");

            sb2.append("거래번호 : ");
            sb2.append(new String(trData.transactionUniqueNumber, "EUC-KR"));
            /////////////////////////////////////////////////////////////
            sb3.append("승인금액 : ");
            sb3.append(Util.formatCurrency(mTotAmt) + "원");
            sb3.append("\n");

            sb3.append("부가세액 : ");
            sb3.append(Util.formatCurrency(mVat) + "원");
            sb3.append("\n");

            sb3.append("공급가액 : ");
            sb3.append(Util.formatCurrency(mSupAmt) + "원");
            sb3.append("\n");

            sb3.append("잔액 : ");        // 선불카드인 경우만 출력
            sb3.append(Util.formatCurrency(new String(trData.balance, "EUC-KR")) + "원");
            /////////////////////////////////////////////////////////////
            sb4.append(new String(trData.message1,"EUC-KR"));
            sb4.append("\n");
            sb4.append(new String(trData.message2,"EUC-KR"));
            sb4.append("\n");
            sb4.append(new String(trData.notice1,"EUC-KR"));
            sb4.append("\n");
            sb4.append(new String(trData.notice2,"EUC-KR"));
            /////////////////////////////////////////////////////////////

            tv = findViewById(R.id.text1);
            tv.setText(sb1.toString());
            tv = findViewById(R.id.text2);
            tv.setText(sb2.toString());
            tv = findViewById(R.id.text3);
            tv.setText(sb3.toString());
            tv = findViewById(R.id.text4);
            tv.setText(sb4.toString());
        }
        else if(mPayType.equals("CASH"))
        {
            if(Arrays.equals(trData.transferCode, "0210".getBytes()))
                tv.setText("[현금영수증 승인]");
            else if(Arrays.equals(trData.transferCode, "0430".getBytes()))
                tv.setText("[현금영수증 승인 취소]");

            /////////////////////////////////////////////////////////////
            sb1.append("고객정보 : ");
            sb1.append(mPersonalNo);
            /////////////////////////////////////////////////////////////
            sb2.append("단말번호 : ");
            sb2.append(new String(trData.deviceNumber, "EUC-KR"));
            sb2.append("\n");

            sb2.append("승인일시 : ");
            sb2.append(new String(trData.transferDate, "EUC-KR"));
            sb2.append("\n");

            sb2.append("승인번호 : ");
            sb2.append(new String(trData.approvalNumber, "EUC-KR"));
            sb2.append("\n");

            sb2.append("거래번호 : ");
            sb2.append(new String(trData.transactionUniqueNumber, "EUC-KR"));
            /////////////////////////////////////////////////////////////
            sb3.append("승인금액 : ");
            sb3.append(Util.formatCurrency(mTotAmt) + "원");
            if(mTraderType.equals("0"))
                sb3.append("[소득공제]");
            else if(mTraderType.equals("1"))
                sb3.append("[지출증빙]");
            sb3.append("\n");

            sb3.append("부가세액 : ");
            sb3.append(Util.formatCurrency(mVat) + "원");
            sb3.append("\n");

            sb3.append("공급가액 : ");
            sb3.append(Util.formatCurrency(mSupAmt) + "원");
            sb3.append("\n");

            /////////////////////////////////////////////////////////////
            sb4.append(new String(trData.message1,"EUC-KR"));
            sb4.append("\n");
            sb4.append(new String(trData.message2,"EUC-KR"));
            sb4.append("\n");
            sb4.append(new String(trData.notice1,"EUC-KR"));
            sb4.append("\n");
            sb4.append(new String(trData.notice2,"EUC-KR"));
            /////////////////////////////////////////////////////////////

            tv = findViewById(R.id.text1);
            tv.setText(sb1.toString());
            tv = findViewById(R.id.text2);
            tv.setText(sb2.toString());
            tv = findViewById(R.id.text3);
            tv.setText(sb3.toString());
            tv = findViewById(R.id.text4);
            tv.setText(sb4.toString());
        }
        else if(mPayType.equals("ZERO"))
        {

        }
        else if(mPayType.equals("KAKAO"))
        {

        }
        else if(mPayType.equals("WEALI"))
        {

        }
        else if(mPayType.equals("LPAY"))
        {

        }
    }

    public void mOnClick(View v)
    {
        switch (v.getId())
        {
            case R.id.closeBtn:
                finish();
                break;
        }
    }


}
