package com.hankarun.piyangoogren;


import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

public class GamesContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.hankarun.piyangoogren";

    private static final int GAMES = 10;
    private static final int GAME_ID = 20;
    private static final int STATS = 30;
    private static final int STATS_ID = 40;

    private GamesDatabaseHelper database;
    private StatisticsDatabaseHelper sdatabase;

    public static final String BASE_PATH = "games";
    public static final String BASE_PATH1 = "game";

    public static final String BASE_STATS_PATH = "stats";
    public static final Uri CONTENT_URI_STATS = Uri.parse("content://" + AUTHORITY + "/" + BASE_STATS_PATH);



    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final Uri CONTENT_URI1 = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH1);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/games";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/game";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, GAMES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", GAME_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH1, GAME_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_STATS_PATH, STATS);
        sURIMatcher.addURI(AUTHORITY, BASE_STATS_PATH+ "/#", STATS_ID);
    }

    @Override
    public boolean onCreate() {
        database = new GamesDatabaseHelper(getContext());
        database.onOpen();
        sdatabase = new StatisticsDatabaseHelper(getContext());
        sdatabase.onOpen();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase db;



        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case GAMES:
                queryBuilder.setTables(GamesDatabaseHelper.TABLE_GAMES);
                db = database.getWritableDatabase();
                break;
            case GAME_ID:
                queryBuilder.setTables(GamesDatabaseHelper.TABLE_GAMES);
                db = database.getWritableDatabase();
                break;
            case STATS:
                queryBuilder.setTables(StatisticsDatabaseHelper.TABLE_STATISTICS);
                db = sdatabase.getWritableDatabase();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB;
        int rowsDeleted = 0;
        long id = 0;
        switch (uriType) {
            case GAMES:
                sqlDB = database.getWritableDatabase();
                id = sqlDB.insert(GamesDatabaseHelper.TABLE_GAMES, null, values);
                break;
            case STATS:
                sqlDB = sdatabase.getWritableDatabase();
                insertOrUpdateById(sqlDB, uri, StatisticsDatabaseHelper.TABLE_STATISTICS, values, "_id");
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB;
        int rowsUpdated = 0;
        switch (uriType) {
            case GAMES:
                sqlDB = database.getWritableDatabase();
                rowsUpdated = sqlDB.update(GamesDatabaseHelper.TABLE_GAMES,
                        values,
                        selection,
                        selectionArgs);
                break;
            case GAME_ID:
                sqlDB = database.getWritableDatabase();
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(GamesDatabaseHelper.TABLE_GAMES,
                            values,
                            GamesDatabaseHelper.ID + "='" + id+"'",
                            null);
                } else {
                    rowsUpdated = sqlDB.update(GamesDatabaseHelper.TABLE_GAMES,
                            values,
                            GamesDatabaseHelper.ID + "='" + id+"'"
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case STATS_ID:
                sqlDB = sdatabase.getWritableDatabase();
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(StatisticsDatabaseHelper.TABLE_STATISTICS,
                            values,
                            StatisticsDatabaseHelper.ID + "='" + id+"'",
                            null);
                } else {
                    rowsUpdated = sqlDB.update(StatisticsDatabaseHelper.TABLE_STATISTICS,
                            values,
                            StatisticsDatabaseHelper.ID + "='" + id+"'"
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void insertOrUpdateById(SQLiteDatabase db, Uri uri, String table,
                                    ContentValues values, String column) throws SQLException {
        try {
            db.insertOrThrow(table, null, values);
        } catch (SQLiteConstraintException e) {
            int nrRows = update(Uri.parse("content://" + AUTHORITY + "/" + BASE_STATS_PATH+"/"+values.getAsString("_id")), values, column + "=?",
                    new String[]{values.getAsString(column)});
            if (nrRows == 0)
                throw e;
        }
    }
}
