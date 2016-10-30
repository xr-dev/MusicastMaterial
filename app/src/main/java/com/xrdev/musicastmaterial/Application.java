package com.xrdev.musicastmaterial;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.xrdev.musicastmaterial.models.LocalQueue;
import com.xrdev.musicastmaterial.utils.DatabaseHandler;

/**
 * Created by Guilherme on 11/04/2016.
 */
public class Application extends android.app.Application {

    // Glide -- imagens
    public static final int GLIDE_BLUR_RADIUS = 175;
    public static final String TAG = "Application";

    private static DatabaseHandler mDatabaseHandler;
    private static LocalQueue mLocalQueue;

    private static String mAdmin;

    /**
     * --------------------------------------------------------------------------------------------
     * GETTERS & SETTERS
     * --------------------------------------------------------------------------------------------
     */

    public static DatabaseHandler getDbHandler(Context context) {
        if (mDatabaseHandler == null) {
            mDatabaseHandler = new DatabaseHandler(context);
        }
        return mDatabaseHandler;
    }

    public static LocalQueue getQueue(String playlistId){
        if (mLocalQueue == null || !mLocalQueue.getPlaylistId().equals(playlistId))
            mLocalQueue = LocalQueue.initialize(playlistId);
        return mLocalQueue;
    }
}
