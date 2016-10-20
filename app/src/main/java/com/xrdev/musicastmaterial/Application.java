package com.xrdev.musicastmaterial;

import android.content.Context;

import com.xrdev.musicastmaterial.utils.DatabaseHandler;

/**
 * Created by Guilherme on 11/04/2016.
 */
public class Application extends android.app.Application {

    // Glide -- imagens
    public static final int GLIDE_BLUR_RADIUS = 175;

    private static DatabaseHandler mDatabaseHandler;


    public static DatabaseHandler getDbHandler(Context context) {
        if (mDatabaseHandler == null) {
            mDatabaseHandler = new DatabaseHandler(context);
        }

        return mDatabaseHandler;
    }
}
