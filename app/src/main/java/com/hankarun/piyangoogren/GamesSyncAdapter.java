package com.hankarun.piyangoogren;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class GamesSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    private SyncUpdateInterface syncUpdateInterface;

    public Context context;

    public GamesSyncAdapter(Context context, boolean autoInitialize, SyncUpdateInterface syncUpdateInterface) {
        super(context, autoInitialize);
        this.context = context;
        this.syncUpdateInterface = syncUpdateInterface;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        try {
            //save(context,Statics.menuOriginal.get(2),"1");
            //save(context,Statics.menuOriginal.get(1),"1");

            //syncUpdateInterface.updated(Statics.menuOriginal.get(0)+","+Statics.menuOriginal.get(1));

            for (String game : Statics.menuOriginal) {
                //Do this for all of the five games.
                // Get shows from the remote server for all games
                ArrayList<Game> remoteGames = getDates(game);
                // Get shows from the local storage
                ArrayList<Game> localGames = new ArrayList<>();
                try {
                    Cursor allLocalGames = provider.query(GamesContentProvider.CONTENT_URI,
                            GamesDatabaseHelper.GAMES_PROJECTION,
                            GamesDatabaseHelper.GAMES_TYPE + " = '" + game + "'",
                            null,
                            null);
                    if (allLocalGames != null) {
                        while (allLocalGames.moveToNext()) {
                            localGames.add(Game.fromCursor(allLocalGames));
                        }
                        allLocalGames.close();
                    }
                } catch (Exception e) {
                    Log.d("error", e.toString());
                }

                //Find different games
                if (localGames.size() > 0) {
                    for (Game localgame : localGames) {
                        remoteGames.remove(localgame);
                    }
                    //Add them to the database
                    if (remoteGames.size() > 0) {
                        for (Game tmp : remoteGames) {
                            provider.insert(GamesContentProvider.CONTENT_URI, tmp.getValues());
                        }


                    }
                } else {
                    if (remoteGames.size() > 0) {
                        for (Game tmp : remoteGames) {
                            provider.insert(GamesContentProvider.CONTENT_URI, tmp.getValues());
                        }
                        //Add them to the database
                    }
                }
            }
            int[] s = {0,0,0,0};
            boolean notify = false;
            for (String game : Statics.menuOriginal) {
                //Check for empty dates
                //Fill them on the background
                try {
                    Cursor allLocalGames = provider.query(GamesContentProvider.CONTENT_URI,
                            GamesDatabaseHelper.GAMES_PROJECTION,
                            GamesDatabaseHelper.GAMES_TYPE + " = '" + game + "' AND " + GamesDatabaseHelper.GAMES_NUMBERS + " = ''",
                            null,
                            null);

                    //Saving new games.
                    if (allLocalGames != null && allLocalGames.moveToFirst()) {
                        s[Statics.menuOriginal.indexOf(game)] = 1;
                        notify = true;
                        save(context, game, "1");
                        Game gamed = Game.fromCursor(allLocalGames);
                        gamed.setmNumbers(getNumbers(gamed.getmDate(), gamed.getmType()));
                        provider.update(GamesContentProvider.CONTENT_URI1, gamed.getValues(), null, null);
                        allLocalGames.close();
                    }
                } catch (Exception e) {
                    Log.d("error1", e.toString());
                }
            }
            if(notify){
                syncUpdateInterface.updated(s);
            }




            for (String game : Statics.menuOriginal) {
                //Check for empty dates
                //Fill them on the background
                try {
                    Cursor allLocalGames = provider.query(GamesContentProvider.CONTENT_URI,
                            GamesDatabaseHelper.GAMES_PROJECTION,
                            GamesDatabaseHelper.GAMES_TYPE + " = '" + game + "' AND " + GamesDatabaseHelper.GAMES_NUMBERS + " = ''",
                            null,
                            null);
                    if (allLocalGames != null) {
                        while (allLocalGames.moveToNext()) {
                            Game gamed = Game.fromCursor(allLocalGames);
                            gamed.setmNumbers(getNumbers(gamed.getmDate(), gamed.getmType()));
                            provider.update(GamesContentProvider.CONTENT_URI, gamed.getValues(), GamesDatabaseHelper.ID + "=? ", new String[]{String.valueOf(gamed.getmId())});
                        }
                        allLocalGames.close();
                    }
                } catch (Exception e) {
                    Log.d("error2", e.toString());
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save(Context context, String name,String text) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        editor = settings.edit();

        editor.putString(name, text);
        editor.commit();
    }

    public String getNumbers(String date, String type) throws RemoteException, IOException {
        String numbers = "";
        try {
            JSONObject json = new JSONObject(downloadUrl("http://www.millipiyango.gov.tr/sonuclar/cekilisler/" + type + "/" + date + ".json"));
            JSONObject jObject = json.getJSONObject("data");
            numbers = jObject.getString("rakamlarNumaraSirasi");
            numbers = numbers.replace("<b>", "");
            numbers = numbers.replace("</b>", "");
            String s = jObject.getString("bilenKisiler");
            Log.d("aa",s);
        } catch (Exception e) {
            Log.d("ss", e.toString());
        }
        return numbers;
    }

    public ArrayList<Game> getDates(String type) throws RemoteException, IOException {

        ArrayList<Game> tmpList = new ArrayList<>();
        try {
            JSONArray json = new JSONArray(downloadUrl("http://www.millipiyango.gov.tr/sonuclar/listCekilisleriTarihleri.php?tur=" + type));
            for (int i = 0; i < json.length(); i++) {
                JSONObject jObject = json.getJSONObject(i);
                try {
                    Game tmp = new Game();
                    tmp.setmDate(jObject.getString("tarih"));
                    tmp.setmDateDetail(jObject.getString("tarihView"));
                    tmp.setmType(type);
                    tmp.setmNumbers("");
                    tmp.setmStatistics("");
                    tmpList.add(tmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.d("ss", e.toString());
        }
        return tmpList;
    }

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            //Log.d("d", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer, "UTF-8");
            return writer.toString();

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), "piyangoogren.hankarun.com");

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            ContentResolver.setIsSyncable(newAccount, GamesContentProvider.AUTHORITY, 1);
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        GamesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, GamesContentProvider.AUTHORITY, true);

        /*
         * Finally, let's do a sync to get things started
         */
        //syncImmediately(context);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = GamesContentProvider.AUTHORITY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                GamesContentProvider.AUTHORITY, bundle);
    }


}
