package com.kor_adk01.silvertown.Alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.kor_adk01.silvertown.R;

import java.util.Calendar;

public class AlarmReciver extends BroadcastReceiver {
    NotificationManager manager;
    NotificationCompat.Builder builder;
    SQLiteDatabase db;
    Cursor cursor;


    //오레오 이상은 반드시 채널을 설정해줘야 Notification이 작동함
    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel1";



    @Override
    public void onReceive(Context context, Intent intent) {


        db = context.openOrCreateDatabase("st_file.db", 0, null);
        cursor = db.rawQuery("SELECT * From Myalarm", null);
        cursor.moveToFirst();

        String day= cursor.getString(cursor.getColumnIndex("yoil"));
        builder = null;
        manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context,CHANNEL_ID);
        }


        Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringtone = RingtoneManager.getRingtone(context,alarm);
        ringtone.play();


        /*Calendar cal = Calendar.getInstance();
        if (!week[cal.get(Calendar.DAY_OF_WEEK)])//체크한 요일이 아니면 종료
            return;

        int setday=cal.get(Calendar.DAY_OF_WEEK);
        String day="";

        switch (setday){
            case 0:
                day="err";
                break;
            case 1:
                day="일요일";
                break;
            case 2:
                day="월요일";
                break;
            case 3:
                day="화요일";
                break;
            case 4:
                day="수요일";
                break;
            case 5:
                day="목요일";
                break;
            case 6:
                day="금요일";
                break;
            case 7:
                day="토요일";
                break;
            default:
                day="err";
                break;

        }*/



        //알림창 클릭 시 activity 화면 부름
        Intent intent2 = new Intent(context, AlarmlistActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,101,intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        ringtone.stop();//알람음 멈추기

        //알림창 제목
        builder.setContentTitle("알람~~");
        //알림창 텍스트
        builder.setContentText(day+" 약 드실시간입니다.");
        //알림창 아이콘
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        //알림창 터치시 자동 삭제
        builder.setAutoCancel(true);

        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        manager.notify(1,notification);



        //Toast.makeText(context, "알람~!!", Toast.LENGTH_LONG).show();    // AVD 확인용
        //Log.e("Alarm", "약 드실 시간 입니다..");    // 로그 확인용
    }

}