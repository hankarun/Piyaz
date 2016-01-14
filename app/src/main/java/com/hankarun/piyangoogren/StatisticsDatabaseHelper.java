package com.hankarun.piyangoogren;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StatisticsDatabaseHelper extends SQLiteOpenHelper {
    public static String TABLE_STATISTICS = "statistics";

    public static String ID = "_id";
    public static String STATISTICS_TYPE = "stype";
    public static String STATISTICS_NUMBER = "snumber";
    public static String STATISTICS_GAME = "game";

    private static final String STATISTICS_DATABASE_CREATE = "create table "
            + TABLE_STATISTICS
            + "(" + ID + " integer primary key autoincrement, "
            + STATISTICS_TYPE + " text,"
            + STATISTICS_NUMBER + " text,"
            + STATISTICS_GAME + " int"
            + ");";

    public static String[] STATICS_PROJECTION = {
            ID,
            STATISTICS_TYPE,
            STATISTICS_NUMBER,
            STATISTICS_GAME,
    };

    private static final String DATABASE_NAME = "stats.db";
    private static final int DATABASE_VERSION = 1;

    private Context context;
    private SQLiteDatabase myDataBase;


    public StatisticsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //db.execSQL(STATISTICS_DATABASE_CREATE);
    }

    public void onOpen(){
        boolean dbExist = checkDataBase();

        Log.d("oncreate", "");
        if(!dbExist){
            this.getReadableDatabase();
            try {
                copyDataBase();
                Log.d("copy","done");
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;

        try{
            File myPath = context.getDatabasePath(DATABASE_NAME);
            return myPath.exists();
        }catch(SQLiteException e){
            Log.d("err", e.getMessage());
        }

        if(checkDB != null){
            checkDB.close();
        }

        return checkDB != null;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = context.getAssets().open(DATABASE_NAME);

        String outFileName = context.getFilesDir().getParentFile().getPath()+ "/databases/" + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase(){
        String myPath = context.getFilesDir().getParentFile().getPath()+ "/databases/" + DATABASE_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATISTICS);
        onCreate(db);
    }
}
