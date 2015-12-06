package com.hankarun.piyangoogren;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class GameSyncService extends Service implements SyncUpdateInterface{
    private static final Object sSyncAdapterLock = new Object();
    private static GamesSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new GamesSyncAdapter(getApplicationContext(), true,this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }

    @Override
    public void updated(String s) {
        String[] temp = s.split("\\,");

        String results = "";
        for(String ee:temp)
            results = results + "," + Statics.menu.get(Statics.menuOriginal.indexOf(ee));
        results = results.substring(1);

        String total;
        if(temp.length>1){
            total = "çekilişleri";
        }else{
            total = "çekilişi";
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Yeni çekilis sonucu.")
                        .setAutoCancel(true)
                        .setContentText(results +" " + total +" yapıldı. Sonuçlar için tıklayınız.");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
    }

}
