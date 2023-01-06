package com.lodong.android.selfcarwashkiosk.roomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

// 룸 데이터 초기화 클래스
@Database(entities = {RoomDBVO.class}, version = 2, exportSchema = false)
@TypeConverters({TimestampConverter.class})
public abstract class DBintialization extends RoomDatabase {

    private static DBintialization database;

    private static String DATABASE_NAME = "database";

    public synchronized static DBintialization getInstance(Context context,String db_name)
    {
        if(database == null){
            database = Room.databaseBuilder(context.getApplicationContext(), DBintialization.class, db_name).allowMainThreadQueries().fallbackToDestructiveMigration().build();

        }

        return database;
    }

    public abstract MainDao mainDao();


}
