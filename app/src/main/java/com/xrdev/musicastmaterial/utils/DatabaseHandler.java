package com.xrdev.musicastmaterial.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.xrdev.musicastmaterial.models.TrackItem;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Created by Guilherme on 27/04/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "musicast.db";
    public static final int DATABASE_VERSION = 3    ;

    public static final String TBL_MATCHES = "matches";

    public static final String MATCHES_SPOTIFY_ID = "spotify_id";
    public static final String MATCHES_YOUTUBE_ID = "youtube_id";
    public static final String MATCHES_REFRESH_DATE = "cache_date";

    private static final String CREATE_MATCHES = "create table "
            + TBL_MATCHES + "("
            + MATCHES_SPOTIFY_ID + " text primary key, "
            + MATCHES_YOUTUBE_ID + " text not null, "
            + MATCHES_REFRESH_DATE + " text)";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MATCHES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_MATCHES);
        onCreate(db);
    }

    public TrackItem checkForMatch(TrackItem trackItem) {
        SQLiteDatabase sqLite = this.getReadableDatabase();
        Cursor cursor = sqLite.query(
                TBL_MATCHES,
                new String[] {MATCHES_YOUTUBE_ID, MATCHES_REFRESH_DATE},
                MATCHES_SPOTIFY_ID + "=?",
                new String[] { trackItem.getTrackId()},
                null, null, null, null);

        if(cursor == null || cursor.getCount() == 0) {
            // Nada encontrado.
            trackItem.setRefreshCacheDate(null);
            trackItem.wasCached = false;
        } else {
            cursor.moveToFirst();
            trackItem.setYoutubeId(cursor.getString(0));
            trackItem.setRefreshCacheDate(cursor.getString(1));
            trackItem.wasCached = true;
        }

        return trackItem;
    }

    public void insertMatch(TrackItem trackItem) {
        SQLiteDatabase sqLite = this.getWritableDatabase();

        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

        String spotifyId = trackItem.getTrackId();
        String youtubeId = trackItem.getYoutubeId();
        String refreshDate = new DateTime().plusWeeks(1).toString(fmt);

        sqLite.execSQL(
                "INSERT OR REPLACE INTO "
                    + TBL_MATCHES
                    + " ("
                    + MATCHES_SPOTIFY_ID + ", " + MATCHES_YOUTUBE_ID + ", " + MATCHES_REFRESH_DATE
                    + ")"
                    + " values ("
                    + "'" + spotifyId + "', '" + youtubeId + "', '" + refreshDate
                    + "')"
        );
    }

    public void removeMatch(String youtubeId) {
        SQLiteDatabase sqLite = this.getWritableDatabase();

        sqLite.delete(TBL_MATCHES,
                MATCHES_YOUTUBE_ID + "= ?",
                new String[]{youtubeId}
        );

        sqLite.close();
    }

}
