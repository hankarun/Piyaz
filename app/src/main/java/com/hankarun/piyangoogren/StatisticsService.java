package com.hankarun.piyangoogren;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

import static com.hankarun.piyangoogren.Statics.setSayisal;

public class StatisticsService extends IntentService {

    private static final String RESULT = "result";

    public StatisticsService() {
        super("StaticsService");
    }

    public StatisticsService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Cursor allLocalGames = getContentResolver().query(GamesContentProvider.CONTENT_URI,
                GamesDatabaseHelper.GAMES_PROJECTION,
                GamesDatabaseHelper.GAMES_NUMBERS + " != '' AND "+ GamesDatabaseHelper.GAMES_TYPE + " != 'piyango' AND " + GamesDatabaseHelper.GAMES_STATISTICS + " = ''",
                null,
                null);
        ArrayList<Game> gameList = new ArrayList<>();
        if (allLocalGames != null) {
            while (allLocalGames.moveToNext()) {
                Game tmp = Game.fromCursor(allLocalGames);
                tmp.setmStatistics("1");
                tmp.setmNumbers(tmp.getmNumbers().replace("<b>",""));
                tmp.setmNumbers(tmp.getmNumbers().replace("</b>",""));
                gameList.add(tmp);
                getContentResolver().update(Uri.parse("content://" + GamesContentProvider.AUTHORITY + "/" + GamesContentProvider.BASE_PATH + "/" + tmp.getmId()), tmp.getValues(), null, null);
            }
            allLocalGames.close();
        }

        for(Game game:gameList) {
            Stats tmp = new Stats();

            Cursor staticsCursor = getContentResolver().query(GamesContentProvider.CONTENT_URI_STATS,
                    StatisticsDatabaseHelper.STATICS_PROJECTION,
                    StatisticsDatabaseHelper.STATISTICS_GAME + " = '" + game.returnWeek() + "' AND " + StatisticsDatabaseHelper.STATISTICS_TYPE + " = '" + game.getmType() + "'",
                    null,
                    null);
            String current;
            String[] currentArray;
            int[] sayisal = new int[setSayisal(game.getmType())];
            if(staticsCursor != null && staticsCursor.getCount()>0){
                //Parse cursor.
                staticsCursor.moveToFirst();

                tmp.setmId(staticsCursor.getInt(staticsCursor.getColumnIndex(StatisticsDatabaseHelper.ID)));

                current = staticsCursor.getString(staticsCursor.getColumnIndex(StatisticsDatabaseHelper.STATISTICS_NUMBER));
                currentArray = current.replace(" ", "").split("\\-");

                String[] numberarray = game.getmNumbers().replace(" ", "").split("\\-");

                for(int i = 0; i < setSayisal(game.getmType()); i++){
                    sayisal[i] = Integer.parseInt(currentArray[i]);
                }
                //Write or update this to database game week data

                for (String a : numberarray) {
                    sayisal[Integer.parseInt(a) - 1] = sayisal[Integer.parseInt(a) - 1] + 1;
                }
                staticsCursor.close();
            } else {
                String[] array = game.getmNumbers().replace(" ", "").split("\\-");

                for(int i = 0; i < setSayisal(game.getmType()); i++){
                    sayisal[i] = 0;
                }

                for (String a : array) {
                    sayisal[Integer.parseInt(a) - 1] = sayisal[Integer.parseInt(a) - 1] + 1;
                }
            }

            String test = "";
            for (int i = 0; i < setSayisal(game.getmType()); i++) {
                test = test + sayisal[i] + "-";
            }
            //Write or update this to database game week data

            tmp.setmType(game.getmType());
            tmp.setmNumber(test);
            tmp.setmGame(game.returnWeek());

            getContentResolver().insert(GamesContentProvider.CONTENT_URI_STATS, tmp.getValues());
            publishResults(0);
        }
    }

    private void publishResults(int result) {
        Intent intent = new Intent();
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}
