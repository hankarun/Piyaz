package com.hankarun.piyangoogren;

import android.content.ContentValues;
import android.database.Cursor;

public class Stats {
    private int mId;
    private String mType;
    private String mNumber;
    private int mGame;

    public String getmType(){ return mType;}
    public String getmNumber(){ return mNumber;}
    public int getmGame(){ return mGame;}
    public int getmId(){return mId;}

    public void setmType(String type){ mType = type;}
    public void setmNumber(String number){ mNumber = number;}
    public void setmGame(int game){ mGame = game;}
    public void setmId(int id){ mId = id;}

    public static Stats fromCursor(Cursor cursor){
        Stats tmp = new Stats();
        tmp.setmId(cursor.getInt(cursor.getColumnIndex(StatisticsDatabaseHelper.ID)));
        tmp.setmType(cursor.getString(cursor.getColumnIndex(StatisticsDatabaseHelper.STATISTICS_TYPE)));
        tmp.setmGame(cursor.getInt(cursor.getColumnIndex(StatisticsDatabaseHelper.STATISTICS_GAME)));
        tmp.setmNumber(cursor.getString(cursor.getColumnIndex(StatisticsDatabaseHelper.STATISTICS_NUMBER)));
        return tmp;
    }

    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        if(getmId() != 0)
            values.put(StatisticsDatabaseHelper.ID,getmId());
        values.put(StatisticsDatabaseHelper.STATISTICS_GAME, getmGame());
        values.put(StatisticsDatabaseHelper.STATISTICS_NUMBER, getmNumber());
        values.put(StatisticsDatabaseHelper.STATISTICS_TYPE, getmType());
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game myObject = (Game) o;

        return myObject.getmId() == mId;

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
