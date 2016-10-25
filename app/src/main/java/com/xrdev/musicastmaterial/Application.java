package com.xrdev.musicastmaterial;

import android.content.Context;

import com.xrdev.musicastmaterial.models.LocalQueue;
import com.xrdev.musicastmaterial.utils.DatabaseHandler;

/**
 * Created by Guilherme on 11/04/2016.
 */
public class Application extends android.app.Application {

    // Glide -- imagens
    public static final int GLIDE_BLUR_RADIUS = 175;

    private static DatabaseHandler mDatabaseHandler;
    private static LocalQueue mLocalQueue;

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
