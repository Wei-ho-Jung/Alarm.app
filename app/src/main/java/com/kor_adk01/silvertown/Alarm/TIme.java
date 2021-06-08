package com.kor_adk01.silvertown.Alarm;

public class TIme {
    private int hour, minute;
    private String am_pm, yoil;

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getAm_pm() {
        return am_pm;
    }

    public void setAm_pm(String am_pm) {
        this.am_pm = am_pm;
    }


    public String getyoil(){
        return  yoil;
    }
    public void setyoil(String yoil){
        this.yoil = yoil;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Time{");
        sb.append("hour=").append(hour);
        sb.append(", minute=").append(minute);
        sb.append('}');
        return sb.toString();
    }
}


