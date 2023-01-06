package com.lodong.android.selfcarwashkiosk.recyclerviewadapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lodong.android.selfcarwashkiosk.R;
import com.lodong.android.selfcarwashkiosk.callback.OnItemListener;
import com.lodong.android.selfcarwashkiosk.roomdb.DBintialization;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

// 달력
public class CalendarRecyclerAdapter extends RecyclerView.Adapter<CalendarRecyclerAdapter.viewHolder> {

    ArrayList<String> dayList;
    OnItemListener onItemListener;
    Context context;
    DBintialization database;
    List<String> sumMoney;


    public CalendarRecyclerAdapter(ArrayList<String> dayList, OnItemListener onItemListener, Context context, DBintialization database, List<String> sumMoney) {
        this.dayList = dayList;
        this.onItemListener = onItemListener;
        this.context = context;
        this.database = database;
        this.sumMoney = sumMoney;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.item_recycler_calendar, parent, false);

        return new viewHolder(view);
    }

    @SuppressLint({"ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        try{

            int sum = Integer.parseInt(sumMoney.get(position));
            DecimalFormat format = new DecimalFormat("###,###");
            String formatStr = format.format(sum);

            holder.dayMoney.setText(formatStr);

        }catch (Exception e){
            holder.dayMoney.setText("0");
        }

        String day = dayList.get(position);

        if(dayList.get(position).equals("")){

            holder.dayTextView.setVisibility(View.GONE);
            holder.dayMoney.setVisibility(View.GONE);
        }

        holder.dayTextView.setText(dayList.get(position));

        int saturdayColor = ContextCompat.getColor(context, R.color.saturday);
        int sundayColor = ContextCompat.getColor(context, R.color.sunday);

        if((position + 1) % 7 == 0) {// 토요일
            holder.dayTextView.setTextColor(saturdayColor);
        }else if( position == 0 || position % 7 == 0){ // 일요일
            holder.dayTextView.setTextColor(sundayColor);
        }

        // 날짜 클릭 이벤트
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemListener.onItemClick(day);

            }
        });

    }

    @Override
    public int getItemCount() {

        Log.d(TAG, "getItemCount: daylist 사이즈" + dayList.size());
        return dayList.size();

    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;
        TextView dayMoney;

        public viewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.date_recycler);
            dayMoney = itemView.findViewById(R.id.daymoney);
        }

        public void onBind(String display){


        }
    }
}
