package com.lodong.android.selfcarwashkiosk.roomdb;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface MainDao {

    @Insert(onConflict = REPLACE)
    void insert(RoomDBVO mainData);

    @Delete
    void delete(RoomDBVO mainData);

    //@Query("UPDATE CARWASH_INFO SET paymentType = :text WHERE ")
    //void update();

    //@Update
    //void userUpdate(RoomDBVO mainData);

    @Query("SELECT * FROM CARWASH_INFO")
    List<RoomDBVO> getAll();

    // 15를 선택하면 15에 해당하는 값들을 조회하도록함
    // 전체 조회
    @Query("SELECT * FROM CARWASH_INFO WHERE date = :date")
    List<RoomDBVO> getPaymentInfo(Date date);

    // 월매출
    @Query("SELECT SUM(money) FROM CARWASH_INFO WHERE DATE BETWEEN :start AND :end")
    String getSumMoney(Date start, Date end);

    // 오늘 매출
    @Query("SELECT SUM(money) FROM CARWASH_INFO WHERE DATE = :date")
    String getNowMoney(Date date);

    // 해당날짜의 일일 매출
    @Query("SELECT SUM(money) FROM CARWASH_INFO WHERE DATE = :date")
    String getDateMoney(Date date);

}
