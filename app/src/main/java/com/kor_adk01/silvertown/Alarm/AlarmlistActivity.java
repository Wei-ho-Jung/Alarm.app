package com.kor_adk01.silvertown.Alarm;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.kor_adk01.silvertown.Memo_SQLiteHelper;
import com.kor_adk01.silvertown.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;


public class AlarmlistActivity extends AppCompatActivity{

    public static final int REQUEST_CODE1 = 1000;


    private AlarmManager alarmManager;
    private Button tpBtn, removeBtn;
    private ListView listView;
    private TextView textView;
    private int hour, minute;
    private String am_pm,yoil;
    private Handler handler;
    private SimpleDateFormat mFormat;
    private SQLiteDatabase db;
    private Alarm_SQLiteHelper helper;
    int request;//팬딩인텐트 리퀘스트코드

    int dbVersion=3;//db 버전
    String dbName = "st_file.db";//db 이름
    String tag = "SQLite";//태그에 사용할 태그명
    Cursor cursor;//커서객체
    Timecurseradapter dbAdapter;






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_alarmlist);


        try {//알람 db
            helper = new Alarm_SQLiteHelper(
                    this,  // 현재 화면의 제어권자
                    dbName,// db 이름
                   null,  // 커서팩토리-null : 표준커서가 사용됨
                    dbVersion);       // 버전


            db = helper.getWritableDatabase(); // 읽고 쓸수 있는 DB
            //db = helper.getReadableDatabase(); // 읽기 전용 DB select문

        } catch (SQLiteException e) {
            e.printStackTrace();
           Log.e(tag,"데이터 베이스를 얻을 수 없습니다.");
        finish();
        }




        alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);

        dbAdapter = new Timecurseradapter(AlarmlistActivity.this, cursor);//알람 커서어뎁터
        //arrayAdapter = new Timeadpter();
        listView=findViewById(R.id.list_view);
        //listView.setAdapter(arrayAdapter);

        //_id값 초기화작업(AUTOINCREMENT 옵션이 지정되어있기때문에  _id값이 너무 커지면 초기화가 한번씩 필요함)
        //String alter="UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'Myalarm';";
        //db.execSQL(alter);

        //실행시 알람 리스트 셀렉트 Query
        String result="SELECT * FROM Myalarm";
        cursor = db.rawQuery(result,null);
        //startManagingCursor(cursor);
            listView.setAdapter(dbAdapter);
            dbAdapter.changeCursor(cursor);



        /*long now = System.currentTimeMillis();
        Date date = new Date(now);*/

        //쓰레드를 사용해서 실시간으로 시간 출력
        //noinspection deprecation
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Calendar cal = Calendar.getInstance();

                mFormat = new SimpleDateFormat("HH:mm:ss");
                String strTime = mFormat.format(cal.getTime());
                textView = findViewById(R.id.current);
                textView.setTextSize(45);
                textView.setText(strTime);
            }
        };
         //실시간 스레드 내부클래스
        class NewRunnable implements Runnable {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(1000);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                }
            }
        }
        //실시간 스레드 객체생성
        NewRunnable runnable = new NewRunnable();
        Thread thread = new Thread(runnable);
        thread.start();


        //새로운 시간을 삽입하기위해 시간 입력 화면을 호출한다.
        tpBtn = findViewById(R.id.addBtn);
        tpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AlarmlistActivity.this, ExeAlarmActivity.class);
                startActivityForResult(intent,REQUEST_CODE1);

            }
        });



        //List에 있는 항목들 눌렀을 때 시간변경 또는 삭제(대화상자)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                //adapterPosition = position;
                //arrayAdapter.removeItem(position);



                cursor = (Cursor) dbAdapter.getItem(position);
                String index = cursor.getString(cursor.getColumnIndex("_id"));
                int id = Integer.parseInt(index);

                timeshow(id);//대화상자(커스텀) 함수 호출(인수=레코드의 id값)

            }
        });



        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, AlarmbootReciver.class);
        if (alarmManager != null){//알람 매니저에 정보가 있을경우 재부팅시에도 울리도록한다.
            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

        }


    }

 //시간이 저장된 리스트를 클릭했을때 호출되는 대화상자 함수
     public void timeshow(int id){





        AlertDialog.Builder builder = new AlertDialog.Builder(AlarmlistActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.time_dialog, null);
        builder.setView(view);

        RadioGroup rg = view.findViewById(R.id.subrg);
        Button btnsub= view.findViewById(R.id.sub);
        Button btndel = view.findViewById(R.id.delete);
        Button btncan = view.findViewById(R.id.cancel);
        final AlertDialog dialog = builder.create();
        EditText  dhour = view.findViewById(R.id.Dhour);
        EditText dmin = view.findViewById(R.id.Dminute);


          btnsub.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {//시간 수정버튼



                int rgid =rg.getCheckedRadioButtonId();


                  switch (rgid){
                      case R.id.subam:
                          am_pm="오전";  break;
                      case R.id.subpm:
                          am_pm="오후";  break;
                      default:
                          Toast.makeText(AlarmlistActivity.this,"오전/오후는 반드시 선택하세요",Toast.LENGTH_SHORT).show();
                          return;
                  }


                  if(dhour.getText().toString().equals("")){
                      Toast.makeText(AlarmlistActivity.this,"시간을 입력하세요",Toast.LENGTH_SHORT).show();
                      return;}

                  if(dmin.getText().toString().equals("")){
                      Toast.makeText(AlarmlistActivity.this,"분을 입력하세요",Toast.LENGTH_SHORT).show();
                      return;}

                  hour = Integer.parseInt(dhour.getText().toString());
                  minute = Integer.parseInt(dmin.getText().toString());

                  if(hour<0||hour>12){
                    Toast.makeText(AlarmlistActivity.this,"시 단위는 0~12까지입니다.",Toast.LENGTH_SHORT).show();
                    return; }

                if(minute>=60){
                    Toast.makeText(AlarmlistActivity.this,"분 단위는 0~59까지입니다.",Toast.LENGTH_SHORT).show();
                    return; }

                db.execSQL("UPDATE Myalarm SET am_pm='"+am_pm+"',hour='"+hour+"',minute='"+minute+"' where _id='"+id+"';");

                  String dayprint="SELECT * FROM Myalarm WHERE _ID='"+id+"'";
                  cursor = db.rawQuery(dayprint,null);
                  cursor.moveToFirst();

                  Intent intent = new Intent(AlarmlistActivity.this, AlarmReciver.class);
                  request = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));;
                  PendingIntent pIntent = PendingIntent.getBroadcast(AlarmlistActivity.this, request, intent, FLAG_UPDATE_CURRENT);






                //요일은 수정하지않으므로 id값을 기준으로 요일 문자열을 가져와서 숫자로 환원한다.
                  int int_day=0;
                  String bool = cursor.getString(cursor.getColumnIndex("yoil"));
                  switch (bool) {//리스트로 가져온 문자 데이터를 숫자로 환산
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


                  Calendar calendar = Calendar.getInstance();
                      calendar.set(Calendar.HOUR, cursor.getInt(cursor.getColumnIndex("hour")));
                      calendar.set(Calendar.MINUTE, cursor.getInt(cursor.getColumnIndex("minute")));
                      calendar.set(Calendar.DAY_OF_WEEK, int_day);
                      calendar.set(Calendar.SECOND, 0);
                      calendar.set(Calendar.MILLISECOND, 0);


                      alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntent);


                  String calsel="SELECT * FROM Myalarm";
                  cursor = db.rawQuery(calsel,null);

                  listView.setAdapter(dbAdapter);
                  dbAdapter.changeCursor(cursor);
                  dbAdapter.notifyDataSetChanged();
             Toast.makeText(AlarmlistActivity.this, "수정하셨습니다.", Toast.LENGTH_SHORT).show();
             dialog.dismiss();

              }
            });



              btndel.setOnClickListener(new View.OnClickListener() {//클릭한 레코드 삭제
              @Override
              public void onClick(View v) {
                  String dayprint="SELECT * FROM Myalarm WHERE _ID='"+id+"'";
                  cursor = db.rawQuery(dayprint,null);

                  cursor.moveToFirst();
                  Intent intent = new Intent(AlarmlistActivity.this, AlarmReciver.class);
                  request = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));;
                  PendingIntent pIntent = PendingIntent.getBroadcast(AlarmlistActivity.this,request, intent, FLAG_UPDATE_CURRENT);

                  alarmManager.cancel(pIntent);
                  pIntent.cancel();

                  String sql = "DELETE from Myalarm WHERE _ID='"+id+"'";
                   db.execSQL(sql);




                String result="SELECT * FROM Myalarm";
                cursor = db.rawQuery(result,null);
                listView.setAdapter(dbAdapter);
                dbAdapter.changeCursor(cursor);
                dbAdapter.notifyDataSetChanged();

                Toast.makeText(AlarmlistActivity.this,"삭제하셨습니다.",Toast.LENGTH_SHORT).show();
                  dialog.dismiss();
                        }
                  });



                btncan.setOnClickListener(new View.OnClickListener() {//동작취소버튼

                    @Override
                    public void onClick(View v) {
                    Toast.makeText(AlarmlistActivity.this,"취소하셨습니다.",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                       }
                 });

        dialog.show();


    }

    //셋팅값을 받아온 결과를 DB에 추가
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        dbAdapter = new Timecurseradapter(AlarmlistActivity.this, cursor);//알람 커서어뎁터


        //시간 리스트 추가
        if(requestCode == REQUEST_CODE1 && resultCode == RESULT_OK && data != null) {
            am_pm = data.getStringExtra("am_pm");
            hour = data.getIntExtra("hour",1);
            minute = data.getIntExtra("minute",2);
            yoil = data.getStringExtra("day");





            db.execSQL("INSERT INTO Myalarm VALUES (null,'"+am_pm+"','"+hour+"','"+minute+"','"+yoil+"');");


            String calsel="SELECT * FROM Myalarm";
            cursor = db.rawQuery(calsel,null);

       cursor.moveToFirst();
       cursor.moveToLast();

            Intent intent = new Intent(AlarmlistActivity.this, AlarmReciver.class);
            request = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
            PendingIntent pIntent = PendingIntent.getBroadcast(AlarmlistActivity.this, request, intent, FLAG_UPDATE_CURRENT);
            Calendar calendar = Calendar.getInstance();

            int int_day=0;

            switch (yoil) {//리스트로 가져온 문자 데이터를 숫자로 환산
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



            calendar.set(Calendar.HOUR,cursor.getInt(cursor.getColumnIndex("hour")));
            calendar.set(Calendar.MINUTE, cursor.getInt(cursor.getColumnIndex("minute")));
            calendar.set(Calendar.DAY_OF_WEEK, int_day);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);


            /*arrayAdapter.addItem(hour, minute, am_pm, month, day, medicine);
            arrayAdapter.notifyDataSetChanged();*/


            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntent);


            String result="SELECT * FROM Myalarm";
            cursor = db.rawQuery(result,null);

            listView.setAdapter(dbAdapter);
            dbAdapter.changeCursor(cursor);
            dbAdapter.notifyDataSetChanged();



        }

        /*//시간 리스트 터치 시 변경된 시간값 저장
        if(requestCode ==REQUEST_CODE2 && resultCode == RESULT_OK && data != null) {
            alarmManager.cancel(pIntent);
            pIntent.cancel();

            hour = data.getIntExtra("hour", 1);
            minute = data.getIntExtra("minute", 2);
            am_pm = data.getStringExtra("am_pm");

            int id=data.getIntExtra("id",0);
            //medicine = data.getStringExtra("medicine");

            db.execSQL("UPDATE Myalarm SET month='"+month+"',day='"+day+"',am_pm='"+am_pm+"',hour='"+hour+"',minute='"+minute+"' where _id='"+id+"';");

            String result="SELECT * FROM Myalarm";
            cursor = db.rawQuery(result,null);
            cursor.moveToFirst();
            int hourtime = cursor.getInt(cursor.getColumnIndex("hour"));
            String bool = cursor.getString(cursor.getColumnIndex("am_pm"));

            if(bool=="오후"){
                hourtime+=12;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourtime);
            calendar.set(Calendar.MINUTE, cursor.getInt(cursor.getColumnIndex("minute")));
            calendar.set(Calendar.SECOND, 00);
            calendar.set(Calendar.MILLISECOND, 00);




            listView.setAdapter(dbAdapter);
            dbAdapter.changeCursor(cursor);
            cursor.close();

            //arrayAdapter.addItem(hour, minute, am_pm, month, day, medicine);
            //arrayAdapter.notifyDataSetChanged();
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pIntent);


        }*/


    }




}