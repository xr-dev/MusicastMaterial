package com.xrdev.musicastmaterial.apis;

import android.content.Context;

import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;
import com.xrdev.musicastmaterial.R;

import java.util.List;

/**
 * Created by Guilherme on 15/10/2016.
 * Classe que fornece as opções para instanciar o Singleton CastContext, necessária para o Cast SDK
 */
class CastOptionsProvider implements OptionsProvider {
    @Override
    public CastOptions getCastOptions(Context appContext) {
        CastOptions castOptions = new CastOptions.Builder()
                .setReceiverApplicationId(appContext.getString(R.string.cast_app_id))
                .build();
        return castOptions;
    }
    @Override
    public List<SessionProvider> getAdditionalSessionProviders(Context context) {
        return null;
    }
}
