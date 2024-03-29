package com.lodong.android.selfcarwashkiosk.view;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.lodong.android.selfcarwashkiosk.R;
import com.lodong.android.selfcarwashkiosk.databinding.ActivityManagerDayMainBinding;
import com.lodong.android.selfcarwashkiosk.callback.OnItemListener;
import com.lodong.android.selfcarwashkiosk.recyclerviewadapter.DayRecyclerAdapter;
import com.lodong.android.selfcarwashkiosk.recyclerviewadapter.PaymentRecyclerAdapter;
import com.lodong.android.selfcarwashkiosk.roomdb.DBintialization;
import com.lodong.android.selfcarwashkiosk.roomdb.RoomDBVO;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManagerDayActivity extends AppCompatActivity implements OnItemListener {

    ActivityManagerDayMainBinding binding;

    LocalDate selectedDate;

    // 날짜
    String date;

    // 월의 마지막일
    int maximumDate;

    // 해당 날짜의 요일
    String weekStr;

    // 1일의 요일
    String firstWeekDay;
    List<RoomDBVO> dataList = new ArrayList<>();
    Activity context;
    DBintialization database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_day_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager_day_main);
        selectedDate = LocalDate.now();

        database = DBintialization.getInstance(this, "database");

        getDate();

        date = date.replace("년", "").replace("월", "").replace("일", "");

        DateFormat format = new SimpleDateFormat("yyyy MM dd");

        Date d=  new Date();

        try {
            d = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }



        dataList = database.mainDao().getPaymentInfo(d);

        setRoomData();

        // 리사이클러뷰 설정
        setDateRecyclerView();

        setPaymentRecyclerView();

        binding.preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ManagerMonth.class));
            }
        });

    }

    public void setRoomData(){


        DateFormat format = new SimpleDateFormat("yyyy MM dd");

        Date d =  new Date();

        try {
            d = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dataList.clear();
        dataList.addAll(database.mainDao().getPaymentInfo(d));

    }

    public void getDate(){

        // 들어오자마자 클릭한 날짜 가져오기
        Intent intent = getIntent();

        // 날짜
        date = intent.getStringExtra("date");



        // 해당날짜의 마지막 일수
        maximumDate = Integer.parseInt(intent.getStringExtra("maximumDate"));

        // 날짜의 요일 구하기
        weekStr = intent.getStringExtra("weekStr");

        // 토요일과 일요일
        firstWeekDay = intent.getStringExtra("first");

        binding.nowDate.setText(date);

    }

    public void setDateRecyclerView(){

        //selectedDate-> 현재날짜에 맞게 고칠것
        //String[] arr = date.split("-");

        date = date.replace("년", " ").replace("월", " ").replace("일", "");

        String[] dateArr = date.split(" ");

        String s = "0";

        if(Integer.parseInt(dateArr[2]) < 10){
            s = "0" + dateArr[2];
        }else{
            s = dateArr[2];
        }


        String dateStr = dateArr[0] + "-" + dateArr[1] + "-" + s;

        LocalDate fDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);

        ArrayList<String> dayList = daysInMonthArray(fDate);

        DayRecyclerAdapter adapter = new DayRecyclerAdapter(dayList, ManagerDayActivity.this, this);

        binding.dayRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.dayRecycler.setAdapter(adapter);

        String[] arr = date.replace("년", "").replace("월", "").replace("일", "").split(" ");

        int move = 0;

        Log.d(TAG, "setDateRecyclerView: 첫번째 요일 확인하기"+  firstWeekDay);

        switch (firstWeekDay){
            case "월요일":
                move = 0;
                break;
            case"화요일":
                move = 1;
                break;
            case "수요일":
                move = 2;
                break;
            case "목요일":
                move = 3;
                break;
            case "금요일":
                move = 4;
                break;
            case "토요일":
                move = 5;
                break;
            case "일요일":
                move = 6;
                break;
        }

        binding.dayRecycler.scrollToPosition(move+Integer.parseInt(arr[2]));

    }


    public void setPaymentRecyclerView(){

        PaymentRecyclerAdapter adapter = new PaymentRecyclerAdapter(dataList, this, database, date);

        binding.paymentRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        binding.paymentRecycler.setAdapter(adapter);

    }

    private ArrayList<String> daysInMonthArray(LocalDate date){
        ArrayList<String> dayList = new ArrayList<>();

        @SuppressLint({"NewApi", "LocalSuppress"}) YearMonth yearMonth = YearMonth.from(date);


        @SuppressLint({"NewApi", "LocalSuppress"}) int lastDay = yearMonth.lengthOfMonth();

        @SuppressLint({"NewApi", "LocalSuppress"}) LocalDate firstDay = date.withDayOfMonth(1);

        Log.d(TAG, "daysInMonthArray: " + date);

        LocalDate lastDate = selectedDate.withDayOfMonth(lastDay);

        @SuppressLint({"NewApi", "LocalSuppress"}) int dayOfWeek = firstDay.getDayOfWeek().getValue();

        for(int i = 1; i < 42; i++){
            if(i <= dayOfWeek || i > lastDay + dayOfWeek){ // 앞쪽

                dayList.add("");

            }else{
                dayList.add(String.valueOf(i - dayOfWeek));
            }
        }

        return  dayList;
    }

    @Override
    public void onItemClick(String dayText) {

        String[] dateArr = date.replace("년", "").replace("월", "").replace("일", "").split(" ");

        String fixDate = dateArr[0] +" "+ dateArr[1] +" "+ dayText;

        DateFormat format = new SimpleDateFormat("yyyy MM dd");

        Date d =  new Date();

        try {
            d = format.parse(fixDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dataList.clear();
        dataList.addAll(database.mainDao().getPaymentInfo(d));

        // 숫자 하나 날아옴

        binding.paymentRecycler.getAdapter().notifyDataSetChanged();


    }
}