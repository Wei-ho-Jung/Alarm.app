package com.kor_adk01.silvertown.Alarm;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.kor_adk01.silvertown.R;

import org.w3c.dom.Text;

public class Timecurseradapter extends CursorAdapter {

    public Timecurseradapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.round_theme, parent, false);


        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView hourText = view.findViewById(R.id.textTime1);
        TextView minuteText = view.findViewById(R.id.textTime2);
        TextView am_pm = view.findViewById(R.id.am_pm);
        TextView yoil = view.findViewById(R.id.yoil);

        hourText.setText(cursor.getString(cursor.getColumnIndex("hour"))+"시");
        minuteText.setText(cursor.getString(cursor.getColumnIndex("minute"))+"분");
        am_pm.setText(cursor.getString(cursor.getColumnIndex("am_pm")));
        yoil.setText(cursor.getString(cursor.getColumnIndex("yoil")));






    }
}
