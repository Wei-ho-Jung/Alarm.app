package com.kor_adk01.silvertown.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class AlarmbootReciver extends BroadcastReceiver {

    SQLiteDatabase db;
    Cursor cursor;

    @Override
    public void onReceive(Context context, Intent intent) {

        db = context.openOrCreateDatabase("st_file.db", 0, null);
        cursor = db.rawQuery("SELECT * From Myalarm", null);
        cursor.moveToFirst();




        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {


            // on device boot complete, reset the alarm
            Intent alarmIntent = new Intent(context, AlarmReciver.class);
            int request = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, request, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);



            Calendar calendar = Calendar.getInstance();

            int int_day=0;

            switch (cursor.getString(cursor.getColumnIndex("yoil"))) {//리스트로 가져온 문자 데이터를 숫자로 환산
                case "일요일":
                    int_day=1; break;
                case "월요일":
                    int_day=2; break;
                case "화요일":
                    int_day=3; break;
                case "수요일":
                    int_day=4; break;
                case "목요일":
                    int_day=5; break;
                case "금요일":
                    int_day=6; break;
                case "토요일":
                    int_day=7; break;
            }
            calendar.set(Calendar.HOUR_OF_DAY, cursor.getInt(cursor.getColumnIndex("hour")));
            calendar.set(Calendar.MINUTE, cursor.getColumnIndex("minute"));
            calendar.set(Calendar.DAY_OF_WEEK,int_day);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);



            if (manager != null) {
                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()+1000,
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
    }
}

