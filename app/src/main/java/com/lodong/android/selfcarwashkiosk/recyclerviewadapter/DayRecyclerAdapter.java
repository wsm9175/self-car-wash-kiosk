package com.lodong.android.selfcarwashkiosk.recyclerviewadapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lodong.android.selfcarwashkiosk.R;
import com.lodong.android.selfcarwashkiosk.callback.OnItemListener;

import java.util.ArrayList;

// 달력을 눌렀을때 나오는 한줄 달력
public class DayRecyclerAdapter extends RecyclerView.Adapter<DayRecyclerAdapter.DayViewHolder> {

    ArrayList<String> dayList;
    OnItemListener onItemListener;
    Context context;

    public DayRecyclerAdapter(ArrayList<String> dayList, OnItemListener onItemListener, Context context) {
        this.dayList = dayList;
        this.onItemListener = onItemListener;
        this.context = context;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.day_recycler, parent, false);

        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {

        String day = String.valueOf(dayList.get(position));

        if(day.equals("")){
            holder.selectDay.setVisibility(View.GONE);
        }else {
            // gone처리만 해놓으니 사라지는 현상이 발견되서 값이 존재하면 visible이 되도록함
            holder.selectDay.setVisibility(View.VISIBLE);
        }

        holder.selectDay.setText(day);

        int saturdayColor = ContextCompat.getColor(context, R.color.saturday);
        int sundayColor = ContextCompat.getColor(context, R.color.sunday);

        if((position + 1) % 7 == 0) {// 토요일
            holder.selectDay.setTextColor(saturdayColor);
        }else if( position == 0 || position % 7 == 0){ // 일요일
            holder.selectDay.setTextColor(sundayColor);
        }else {
            holder.selectDay.setTextColor(Color.BLACK);
        }


        //-

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 이미 위에 이벤트가 있어버려서 해당 아이템 이벤트로 못들어온것임
                onItemListener.onItemClick(day);

            }
        });

    }

    @Override
    public int getItemCount() {


        return dayList.size();
    }

    public class DayViewHolder extends RecyclerView.ViewHolder{

        TextView selectDay;

        public DayViewHolder(View itemView){
            super(itemView);

            selectDay = itemView.findViewById(R.id.selectDay);

        }
    }



}
