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

public class GamesDatabaseHelper extends SQLiteOpenHelper {
    public static String TABLE_GAMES = "games";
    public static String TABLE_STATISTICS = "statistics";

    public static String ID = "_id";

    public static final String GAMES_NUMBERS = "numbers";
    public static final String GAMES_DATE = "date";
    public static final String GAMES_DATEVIEW = "dateview";
    public static final String GAMES_TYPE = "type";
    public static final String GAMES_STATISTICS = "stats";
    public static final String GAMES_LUKYONES = "luky";



    private static final String GAMES_DATABASE_CREATE = "create table "
            + TABLE_GAMES
            + "(" + ID + " integer primary key autoincrement, "
            + GAMES_NUMBERS + " text,"
            + GAMES_DATE + " text,"
            + GAMES_DATEVIEW + " text,"
            + GAMES_TYPE + " text,"
            + GAMES_STATISTICS + " text,"
            + GAMES_LUKYONES + " text"
            + ");";



    public static String[] GAMES_PROJECTION = {
            ID,
            GAMES_NUMBERS,
            GAMES_DATE,
            GAMES_DATEVIEW,
            GAMES_TYPE,
            GAMES_STATISTICS,
            GAMES_LUKYONES
    };

    private static final String DATABASE_NAME = "games.db";
    private static final int DATABASE_VERSION = 1;

    private Context context;
    private SQLiteDatabase myDataBase;

    public GamesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(GAMES_DATABASE_CREATE);
    }

    public void onOpen(){
        boolean dbExist = checkDataBase();

        Log.d("oncreate","");
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATISTICS);
        onCreate(db);
    }
}
