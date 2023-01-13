package com.lodong.android.selfcarwashkiosk.roomdb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Date;

// 룸 디비

@Entity(tableName = "CARWASH_INFO")
public class RoomDBVO implements Serializable
{

    @PrimaryKey(autoGenerate = true)
    private long id;

    // 거래번호
    @ColumnInfo(name = "transactionNumber")
    private String transactionNumber;

    // 시간
    @ColumnInfo(name = "time")
    @TypeConverters({TimestampConverter.class})
    private Date time;

    // 금액
    @ColumnInfo(name = "money")
    private int money;

    // 세차타입
    @ColumnInfo(name = "washType")
    private String washType;

    // 세차기계
    @ColumnInfo(name = "washMachine")
    private int washMachine;

    // 결제타입
    @ColumnInfo(name = "paymentType")
    private String paymentType;

    // 포인트카드키
    @ColumnInfo(name = "pointCardKey")
    private String pointCardKey;


    // 단가
    @ColumnInfo(name = "unitPrice")
    private int unitPrice;

    // 결제날짜
    @ColumnInfo(name = "date")
    @TypeConverters({TimestampConverter.class})
    private Date date;


    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getWashType() {
        return washType;
    }

    public void setWashType(String washType) {
        this.washType = washType;
    }

    public int getWashMachine() {
        return washMachine;
    }

    public void setWashMachine(int washMachine) {
        this.washMachine = washMachine;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPointCardKey() {
        return pointCardKey;
    }

    public void setPointCardKey(String pointCardKey) {
        this.pointCardKey = pointCardKey;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(int unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
