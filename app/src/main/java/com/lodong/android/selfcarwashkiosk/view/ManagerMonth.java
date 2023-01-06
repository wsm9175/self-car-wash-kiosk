package com.lodong.android.selfcarwashkiosk.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lodong.android.selfcarwashkiosk.R;
import com.lodong.android.selfcarwashkiosk.databinding.ActivityManagerMonthBinding;
import com.lodong.android.selfcarwashkiosk.callback.OnItemListener;
import com.lodong.android.selfcarwashkiosk.recyclerviewadapter.CalendarRecyclerAdapter;
import com.lodong.android.selfcarwashkiosk.roomdb.DBintialization;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManagerMonth extends AppCompatActivity implements OnItemListener {

    ActivityManagerMonthBinding binding;

    LocalDate selectedDate;

    List<String> sumMoney;
    Activity context;
    DBintialization database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_month);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manager_month);

        selectedDate = LocalDate.now();

        database = DBintialization.getInstance(this, "database");
        sumMoney = new ArrayList<>();

        setDateMoney(selectedDate);

        getSumMoney(selectedDate);
        setTodayMoney();

        setMonthView();

        binding.preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate = selectedDate.minusMonths(1);
                getSumMoney(selectedDate);
                setMonthView();
                setDateMoney(selectedDate);
            }
        });

        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate = selectedDate.plusMonths(1);

                getSumMoney(selectedDate);
                setMonthView();
                setDateMoney(selectedDate);

            }
        });

    } // onCreate

    //월 설정
    // 날짜 타입 설정
    @SuppressLint("NewApi")
    private String monthYearFromData(LocalDate date){
        @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월");

        return date.format(formatter);
    }

    @SuppressLint("NewApi")
    private String yearMonthFromDate(LocalDate date){

        @SuppressLint({"NewApi", "LocalSuppress"}) DateTimeFormatter formatter=  DateTimeFormatter.ofPattern("yyyy년 MM월");
        return date.format(formatter);
    }

    // 오늘 매출
    public void setTodayMoney(){

        DateFormat format = new SimpleDateFormat("yyyy MM dd");

        String selectStr = String.valueOf(selectedDate).replace("-", " ");

        Date d = new Date();
        try {
            d = format.parse(selectStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String str = database.mainDao().getNowMoney(d);


        if(str == null){
            str = "0";
        }

        int paymentMoney = Integer.parseInt(str);
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String formattedStringPrice = myFormatter.format(paymentMoney);

        binding.todayMoney.setText(formattedStringPrice);
    }


    // 일별 총합계 출력
    public void setDateMoney(LocalDate selectedDate){

        sumMoney.clear();

        String select = String.valueOf(selectedDate);


        String[] selectArr = select.replace("-", " ").split(" ");
        int max = Integer.parseInt(getMaximumDate());
        @SuppressLint({"NewApi", "LocalSuppress"}) YearMonth yearMonth = YearMonth.from(selectedDate);

        @SuppressLint({"NewApi", "LocalSuppress"}) int lastDay = yearMonth.lengthOfMonth();

        @SuppressLint({"NewApi", "LocalSuppress"}) LocalDate firstDay = selectedDate.withDayOfMonth(1);

        LocalDate lastDate = selectedDate.withDayOfMonth(lastDay);

        @SuppressLint({"NewApi", "LocalSuppress"}) int dayOfWeek = firstDay.getDayOfWeek().getValue();

        int start = 0;

        switch (dayOfWeek){
            case 7:
                start = -6;
                break;
            case 6:
                start = -5;
                break;
            case 5:
                start = -4;
                break;
            case 4:
                start = -3;
                break;
            case 3:
                start = -2;
                break;
            case 2:
                start = -1;
                break;
            case 1:
                start = 0;
        }


        // 현재 데이트를 알아내고 거기서

        for(int i = start; i <= max; i++){
            String date = selectArr[0] + " " + selectArr[1] + " " + i;

            Date d = new Date();
            DateFormat format = new SimpleDateFormat("yyyy MM dd");
            try {
                d = format.parse(date);
            }catch (Exception e){

            }
            if(i+7 <= dayOfWeek || i+7 > lastDay + dayOfWeek){ // 앞쪽

                sumMoney.add("0");

            }else{

                if(database.mainDao().getDateMoney(d) == null || database.mainDao().getDateMoney(d).equals("")){

                    sumMoney.add("0");

                }else{

                    sumMoney.add(database.mainDao().getDateMoney(d));

                }

            }

        } // for문끝

    }

    // 월매출
    public void getSumMoney(LocalDate selectedDate){

        String select = String.valueOf(selectedDate);
        String[] selectArr = select.replace("-", " ").split(" ");

        String startDate = selectArr[0] + " " + selectArr[1] + " 1";
        String endDate = selectArr[0] + " " + selectArr[1] + " " + getMaximumDate();


        DateFormat format = new SimpleDateFormat("yyyy MM dd");

        Date startD = new Date();
        Date endD = new Date();

        try {
            startD = format.parse(startDate);
            endD = format.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Thu Dec 01 00:00:00 GMT 2022

        String monthMoney = database.mainDao().getSumMoney(startD, endD);

        if(monthMoney==null){
            monthMoney = "0";
        }

        int paymentMoney = Integer.parseInt(monthMoney);
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String formattedStringPrice = myFormatter.format(paymentMoney);

        binding.monthMoney.setText(formattedStringPrice);

    }

    // 화면설정
    private void setMonthView(){

        binding.tvMonth.setText(monthYearFromData(selectedDate));
        // 현재 날짜 가져오기
        binding.textView.setText(getNowDate());

        // 해당 월 날짜 가져오기
        ArrayList<String> dayList = daysInMonthArray(selectedDate);

        // 어댑터 데이터 적용
        CalendarRecyclerAdapter adapter = new CalendarRecyclerAdapter(dayList, ManagerMonth.this, this, database, sumMoney);

        RecyclerView.LayoutManager manager = new GridLayoutManager(getApplicationContext(), 7);

        // 레이아웃 적용
        binding.caleandar.setLayoutManager(manager);
        binding.caleandar.setAdapter(adapter);

    }

    private String getNowDate(){

        java.util.Date today = new java.util.Date();
        SimpleDateFormat formatTime = new SimpleDateFormat("yyyy년 MMM dd일 EEE요일", Locale.KOREAN);
        String todayString = formatTime.format(today);

        return todayString;
    }


    private ArrayList<String> daysInMonthArray(LocalDate date){
        ArrayList<String> dayList = new ArrayList<>();

        @SuppressLint({"NewApi", "LocalSuppress"}) YearMonth yearMonth = YearMonth.from(date);

        @SuppressLint({"NewApi", "LocalSuppress"}) int lastDay = yearMonth.lengthOfMonth();

        @SuppressLint({"NewApi", "LocalSuppress"}) LocalDate firstDay = selectedDate.withDayOfMonth(1);

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
        // 클릭했을때 해당주차에 해당하는 주차를 넘기고 액티비티 이동

        try{

            String yearMonDay = yearMonthFromDate(selectedDate) + " " + dayText + "일";

            String dateDay = getDateDay(yearMonDay);

            // 첫째날의 요일 구하기
            String firstWeekDay = getHolidayDate(yearMonDay);

            Intent intent = new Intent(this, ManagerDayActivity.class);
            intent.putExtra("date", yearMonDay);
            intent.putExtra("maximumDate", getMaximumDate());
            intent.putExtra("weekStr", dateDay);
            intent.putExtra("first", firstWeekDay);
            startActivity(intent);

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "해당날짜는 비어있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 클릭한 날자의 마지막 일수 가져오기
    public String getMaximumDate(){
        String yearDay = yearMonthFromDate(selectedDate);

        Calendar cal = Calendar.getInstance();

        String[] arr = yearDay.replace("월", " ").replace("년", "").split(" ");

        cal.set(Integer.parseInt(arr[0]), Integer.parseInt(arr[1])-1, 1);

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        String maximumDate = sdf.format(cal.getTime());


        return maximumDate;
    }

    // 클릭한 날짜의 요일 구하기
    public static String getDateDay(String yearMonDay){

        String weekStr = "";

        String[] str = yearMonDay.replace("년", "").replace("월", "").replace("일", "").split(" ");

        int[] intArr = new int[str.length];

        for (int i = 0; i < str.length; i++) {
            intArr[i] = Integer.parseInt(str[i]);
        }


        LocalDate date = LocalDate.of(intArr[0], intArr[1], intArr[2]);

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayOfWeekNumber = dayOfWeek.getValue();

        switch (dayOfWeekNumber){
            case 1:
                weekStr = "월요일";
                break;
            case 2:
                weekStr = "화요일";
                break;
            case 3:
                weekStr = "수요일";
                break;
            case 4:
                weekStr = "목요일";
                break;
            case 5:
                weekStr = "금요일";
                break;
            case 6:
                weekStr = "토요일";
                break;
            case 7:
                weekStr=  "일요일";
                break;

        }

        return weekStr;
    }

    public String getHolidayDate(String yearMonDay){
        String weekStr = "";

        String[] str = yearMonDay.replace("년", "").replace("월", "").replace("일", "").split(" ");

        int[] intArr = new int[str.length];

        for (int i = 0; i < str.length; i++) {
            intArr[i] = Integer.parseInt(str[i]);
        }


        LocalDate date = LocalDate.of(intArr[0], intArr[1], 1);

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayOfWeekNumber = dayOfWeek.getValue();

        switch (dayOfWeekNumber){
            case 1:
                weekStr = "월요일";
                break;
            case 2:
                weekStr = "화요일";
                break;
            case 3:
                weekStr = "수요일";
                break;
            case 4:
                weekStr = "목요일";
                break;
            case 5:
                weekStr = "금요일";
                break;
            case 6:
                weekStr = "토요일";
                break;
            case 7:
                weekStr=  "일요일";
                break;

        }

        return weekStr;
    }

    public int get(String yearMonDay){
        int week = 0;

        String[] str = yearMonDay.replace("년", "").replace("월", "").replace("일", "").split(" ");

        int[] intArr = new int[str.length];

        for (int i = 0; i < str.length; i++) {
            intArr[i] = Integer.parseInt(str[i]);
        }


        LocalDate date = LocalDate.of(intArr[0], intArr[1], 1);

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        int dayOfWeekNumber = dayOfWeek.getValue();

        switch (dayOfWeekNumber){
            case 1:
                week = 1;
                break;
            case 2:
                week = 2;
                break;
            case 3:
                week = 3;
                break;
            case 4:
                week = 4;
                break;
            case 5:
                week = 5;
                break;
            case 6:
                week = 6;
                break;
            case 7:
                week =  7;
                break;

        }

        return week;
    }


}
