package com.kor_adk01.silvertown.Alarm;

import android.app.AlarmManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.kor_adk01.silvertown.R;

import java.util.ArrayList;

public class Timeadpter extends BaseAdapter {
 public ArrayList<TIme> Tlist = new ArrayList<TIme>();
 private ArrayList<TIme> arrayList = Tlist;   //백업 arrayList


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.round_theme, parent, false);

            TextView hourText = (TextView)convertView.findViewById(R.id.textTime1);
            TextView minuteText = (TextView)convertView.findViewById(R.id.textTime2);
            TextView am_pm = (TextView)convertView.findViewById(R.id.am_pm);
            TextView yoil = convertView.findViewById(R.id.yoil);

            holder.hourText = hourText;
            holder.minuteText = minuteText;
            holder.am_pm = am_pm;
            holder.yoil = yoil;

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        TIme time = arrayList.get(position);
        holder.am_pm.setText(time.getAm_pm());
        holder.hourText.setText(time.getHour()+ "시");
        holder.minuteText.setText(time.getMinute()+ "분");
        holder.yoil.setText(time.getyoil());

        return convertView;
    }


    public void addItem(int hour, int minute, String am_pm, String yoil) {
        TIme time = new TIme();

        time.setHour(hour);
        time.setMinute(minute);
        time.setAm_pm(am_pm);
        time.setyoil(yoil);

        Tlist.add(time);
    }

    //List 삭제 method
    public void removeItem(int position) {
        if(Tlist.size() < 1) {

        }
        else {
            Tlist.remove(position);
        }
    }

    public void removeItem() {
        if(Tlist.size() < 1) {

        }
        else {
            Tlist.remove(Tlist.size()-1);
        }
    }

    static class ViewHolder{
        TextView hourText, minuteText, am_pm, yoil;

    }

}
