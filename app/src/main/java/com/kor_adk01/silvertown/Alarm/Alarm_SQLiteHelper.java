package com.kor_adk01.silvertown.Alarm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Alarm_SQLiteHelper extends SQLiteOpenHelper {
    public Alarm_SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {//테이블 생성함수
        String sql = "CREATE TABLE Myalarm(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "am_pm TEXT, " +
                "hour TEXT, " +
                "minute TEXT," +
                "yoil TEXT)";
        db.execSQL(sql);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       db.execSQL("drop table Myalarm");
       onCreate(db);
    }
}
