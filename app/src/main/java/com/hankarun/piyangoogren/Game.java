package com.hankarun.piyangoogren;


import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private String mLukyOnes;

    public int getmId() { return mId; }
    public String getmNumbers() { return mNumbers; }
    public String getmDate() { return mDate; }
    public String getmDateDetail() { return mDateDetail; }
    public String getmType() { return mType; }
    public String getmStatistics() { return mStatistics; }
    public String getmLukyOnes() { return mLukyOnes;}

    public void setmId(int id) { mId = id;}
    public void setmNumbers(String numbers) { mNumbers = numbers; }
    public void setmDate(String date) { mDate = date; }
    public void setmDateDetail(String dateDetail) { mDateDetail = dateDetail;}
    public void setmType(String type){ mType = type; }
    public void setmStatistics(String statistics) { mStatistics = statistics; }
    public void setmLukyOnes(String lukyOnes) { mLukyOnes = lukyOnes;}

    public static Game fromCursor(Cursor cursor){
        Game tmp = new Game();
        tmp.setmId(cursor.getInt(cursor.getColumnIndex(GamesDatabaseHelper.ID)));
        tmp.setmDate(cursor.getString(cursor.getColumnIndex(GamesDatabaseHelper.GAMES_DATE)));
        tmp.setmDateDetail(cursor.getString(cursor.getColumnIndex(GamesDatabaseHelper.GAMES_DATEVIEW)));
        tmp.setmType(cursor.getString(cursor.getColumnIndex(GamesDatabaseHelper.GAMES_TYPE)));
        tmp.setmStatistics(cursor.getString(cursor.getColumnIndex(GamesDatabaseHelper.GAMES_STATISTICS)));
        tmp.setmNumbers(cursor.getString(cursor.getColumnIndex(GamesDatabaseHelper.GAMES_NUMBERS)));
        tmp.setmLukyOnes(cursor.getString(cursor.getColumnIndex(GamesDatabaseHelper.GAMES_LUKYONES)));
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
        values.put(GamesDatabaseHelper.GAMES_LUKYONES, getmLukyOnes());
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

    public String getLukyToString(){
        if(mLukyOnes == null)
            mLukyOnes = "[\n" +
                    "    {\n" +
                    "        \"kisiBasinaDusenIkramiye\": 7.35,\n" +
                    "        \"kisiSayisi\": 86929,\n" +
                    "        \"oid\": \"64ga09ii0m1xi203\",\n" +
                    "        \"tur\": \"$3_BILEN\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"kisiBasinaDusenIkramiye\": 89.8,\n" +
                    "        \"kisiSayisi\": 4153,\n" +
                    "        \"oid\": \"64ga09ii0m1xi202\",\n" +
                    "        \"tur\": \"$4_BILEN\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"kisiBasinaDusenIkramiye\": 4800.2,\n" +
                    "        \"kisiSayisi\": 72,\n" +
                    "        \"oid\": \"64ga09ii0m1xi201\",\n" +
                    "        \"tur\": \"$5_BILEN\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"kisiBasinaDusenIkramiye\": 1110670.8000000003,\n" +
                    "        \"kisiSayisi\": 0,\n" +
                    "        \"oid\": \"64ga09ii0m1xi200\",\n" +
                    "        \"tur\": \"$6_BILEN\"\n" +
                    "    }\n" +
                    "]";
        try {
            mLukyOnes = mLukyOnes.replace("$6_BILEN","6 Bilen ");
            mLukyOnes = mLukyOnes.replace("$5_BILEN","5 Bilen ");
            mLukyOnes = mLukyOnes.replace("$4_BILEN","4 Bilen ");
            mLukyOnes = mLukyOnes.replace("$3_BILEN","3 Bilen ");
            mLukyOnes = mLukyOnes.replace("$2_BILEN","2 Bilen ");
            mLukyOnes = mLukyOnes.replace("$7_BILEN","7 Bilen ");
            mLukyOnes = mLukyOnes.replace("$8_BILEN","8 Bilen ");
            mLukyOnes = mLukyOnes.replace("$9_BILEN","9 Bilen ");
            mLukyOnes = mLukyOnes.replace("$10_BILEN","10 Bilen ");
            mLukyOnes = mLukyOnes.replace("$1_1_BILEN","1+1 Bilen ");
            mLukyOnes = mLukyOnes.replace("$2_1_BILEN","2+1 Bilen ");
            mLukyOnes = mLukyOnes.replace("$3_1_BILEN","3+1 Bilen ");
            mLukyOnes = mLukyOnes.replace("$4_1_BILEN","4+1 Bilen ");
            mLukyOnes = mLukyOnes.replace("$5_1_BILEN","5+1 Bilen ");
            mLukyOnes = mLukyOnes.replace("$HIC","0 Bilen ");
            JSONArray json = new JSONArray(getmLukyOnes());
            String temp = "";
            String temp2 = "";
            for(int x = 0; x < json.length(); x++){
                JSONObject js = (JSONObject) json.get(x);
                Double a = Double.parseDouble(js.getString("kisiBasinaDusenIkramiye"));
                temp += js.getString("tur") + String.format("%,.0f",Double.parseDouble(js.getString("kisiSayisi"))) +" Kişi\n";
                temp2 += "İkramiye : " +  String.format("%,.0f",a) + " TL" + "\n";
            }
            return temp+"-"+temp2;
        }catch (Exception e){
            Log.d("hata",e.toString());
        }
        return null;
    }

}
