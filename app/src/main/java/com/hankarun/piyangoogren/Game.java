package com.hankarun.piyangoogren;


import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Game {
    private int mId;
    private String mNumbers;
    private String mDate;
    private String mDateDetail;
    private String mType;
    private String mStatistics;

    public int getmId() { return mId; }
    public String getmNumbers() { return mNumbers; }
    public String getmDate() { return mDate; }
    public String getmDateDetail() { return mDateDetail; }
    public String getmType() { return mType; }
    public String getmStatistics() { return mStatistics; }

    public void setmId(int id) { mId = id;}
    public void setmNumbers(String numbers) { mNumbers = numbers; }
    public void setmDate(String date) { mDate = date; }
    public void setmDateDetail(String dateDetail) { mDateDetail = dateDetail;}
    public void setmType(String type){ mType = type; }
    public void setmStatistics(String statistics) { mStatistics = statistics; }

    public static Game fromCursor(Cursor cursor){
        Game tmp = new Game();
        tmp.setmId(cursor.getInt(cursor.getColumnIndex(GamesDatabaseHelper.ID)));
        tmp.setmDate(cursor.getString(cursor.getColumnIndex(GamesDatabaseHelper.GAMES_DATE)));
        tmp.setmDateDetail(cursor.getString(cursor.getColumnIndex(GamesDatabaseHelper.GAMES_DATEVIEW)));
        tmp.setmType(cursor.getString(cursor.getColumnIndex(GamesDatabaseHelper.GAMES_TYPE)));
        tmp.setmStatistics(cursor.getString(cursor.getColumnIndex(GamesDatabaseHelper.GAMES_STATISTICS)));
        tmp.setmNumbers(cursor.getString(cursor.getColumnIndex(GamesDatabaseHelper.GAMES_NUMBERS)));
        return tmp;
    }

    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        if(getmId() != 0)
            values.put(GamesDatabaseHelper.ID,getmId());
        values.put(GamesDatabaseHelper.GAMES_DATE, getmDate());
        values.put(GamesDatabaseHelper.GAMES_DATEVIEW, getmDateDetail());
        values.put(GamesDatabaseHelper.GAMES_NUMBERS, getmNumbers());
        values.put(GamesDatabaseHelper.GAMES_STATISTICS, getmStatistics());
        values.put(GamesDatabaseHelper.GAMES_TYPE, getmType());
        return values;
    }

    public Date returnDate(){
        DateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(mDate);
        }catch (Exception e){
            Log.d("date ",e.toString());
        }
        return date;
    }

    public int returnWeek(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(returnDate());
        return cal.get(Calendar.WEEK_OF_YEAR)==53 ? 52 : cal.get(Calendar.WEEK_OF_YEAR);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game myObject = (Game) o;

        return myObject.getmDate().equals(getmDate());

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(mId);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mId);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

}
