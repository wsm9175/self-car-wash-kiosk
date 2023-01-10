package com.lodong.android.selfcarwashkiosk.recyclerviewadapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lodong.android.selfcarwashkiosk.R;
import com.lodong.android.selfcarwashkiosk.roomdb.DBintialization;
import com.lodong.android.selfcarwashkiosk.roomdb.RoomDBVO;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PaymentRecyclerAdapter extends RecyclerView.Adapter<PaymentRecyclerAdapter.viewHolder> {

    List<RoomDBVO> payment;
    Context context;
    DBintialization database;
    String date;

    public PaymentRecyclerAdapter(List<RoomDBVO> payment, Context context, DBintialization database, String date) {
        this.payment = payment;
        this.context = context;
        this.database = database;
        this.date = date;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.payment_recycler, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        Log.d(TAG, "onBindViewHolder: 어댑터 들어옴어댑터 들어옴어댑터 들어옴어댑터 들어옴어댑터 들어옴");

        final RoomDBVO room = payment.get(position);
        database = DBintialization.getInstance(context, "database");

        // 그 날짜에 해당하는 결과를 띄워줘야 한다.

        String pay = room.getPaymentType();

        if(pay.equals("카드승인")){

            holder.paymentType.setImageResource(R.drawable.card_acknowledgment);
            holder.paymentType_tv.setText("카드승인");

        }else if(pay.equals("포인트승인")){

            holder.paymentType.setImageResource(R.drawable.point_acknowledgment);
            holder.paymentType_tv.setText("포인트승인");

        }

        String washType = room.getWashType();

        if(washType.equals("일반세차")){
            holder.washType_tv.setText("일반 세차");
        }else if(washType.equals("고급세차")){
            holder.washType_tv.setText("고급 세차");
        }

        // 이형태로 저장할것
        // 14:35
        Date paymentTimeStr = room.getTime();

        Log.d(TAG, "onBindViewHolder: 시간 확인하기" + room.getTime());

        DateFormat sdFormat = new SimpleDateFormat("HH:mm");


        String time = sdFormat.format(paymentTimeStr);

        if(time == null || time == ""){
            holder.paymentTime.setText("해당정보 없음");
        }else {
            holder.paymentTime.setText(time);
        }


        // 3번째 숫자 마다 콤마 붙이기
        // 500,000
        int paymentMoney = room.getMoney();
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String formattedStringPrice = myFormatter.format(paymentMoney);

        holder.paymentMoney.setText(formattedStringPrice);

    }

    @Override
    public int getItemCount() {

        Log.d(TAG, "getItemCount: " + payment.size());
        return payment.size();

    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ImageView paymentType;
        TextView paymentType_tv;
        TextView washType_tv;
        TextView paymentTime;
        TextView paymentMoney;

        public viewHolder(View itemView) {
            super(itemView);

            paymentType = itemView.findViewById(R.id.paymentType);
            paymentType_tv = itemView.findViewById(R.id.paymentType_tv);
            washType_tv = itemView.findViewById(R.id.washType_tv);
            paymentTime = itemView.findViewById(R.id.paymentTime);
            paymentMoney = itemView.findViewById(R.id.paymentMoney);

        }
    }


}
