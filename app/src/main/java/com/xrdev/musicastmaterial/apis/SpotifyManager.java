package com.xrdev.musicastmaterial.apis;

import android.content.Context;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.PlaylistTracksRequest;
import com.wrapper.spotify.methods.UserPlaylistsRequest;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.RefreshAccessTokenCredentials;
import com.wrapper.spotify.models.SimplePlaylist;
import com.wrapper.spotify.models.User;
import com.xrdev.musicastmaterial.Application;
import com.xrdev.musicastmaterial.models.PlaylistItem;
import com.xrdev.musicastmaterial.models.Token;
import com.xrdev.musicastmaterial.models.TrackItem;
import com.xrdev.musicastmaterial.utils.DatabaseHandler;
import com.xrdev.musicastmaterial.utils.PrefsManager;

import java.util.ArrayList;

/**
 * Created by Guilherme on 22/07/2014.
 */
public class SpotifyManager {
    // IDs:
    public static final String TAG = "SpotifyHandler";
    public static final String CLIENT_ID = "befa95e4d007494ea40efcdbd3e1fff7";
    public static final String REDIRECT_URI = "musicast://callback";
    public static final String CLIENT_SECRET = "cffb5db7d8eb4910b3a95527fcee6899";
    public static final int REQUEST_CODE = 9000;

    // Api:
    private static Api api = Api.builder()
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .redirectURI(REDIRECT_URI)
            .build();


    public static ArrayList<TrackItem> getPlaylistTracks(PlaylistItem playlist, int limit, int offset, Context context) {
        User currentUser = getCurrentUser(context);
        DatabaseHandler dbHandler = Application.getDbHandler(context);
        int totalTracks = playlist.getNumTracksInt();
        ArrayList<TrackItem> result = new ArrayList<TrackItem>();

        final PlaylistTracksRequest request = api.getPlaylistTracks(playlist.getOwnerId(), playlist.getPlaylistId())
                .limit(limit)
                .offset(offset)
                .build();

        Log.d(TAG, "Total tracks:" + totalTracks);
        Log.d(TAG, "PlaylistRequest: " + request.toStringWithQueryParameters());
        try {
            final Page<PlaylistTrack> tracksPage = request.get();
            for (PlaylistTrack playlistTrack : tracksPage.getItems()) {

                TrackItem trackItem = dbHandler.checkForMatch(
                        new TrackItem(playlistTrack.getTrack())
                );
                result.add(trackItem);
            }

        } catch (Exception e) {
            Log.e(TAG, "Erro ao obter faixas de lista de reprodução. / Unable to get playlist tracks. Error: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<PlaylistItem> getUserPlaylists(Context context) {
        User currentUser = getCurrentUser(context);
        // Nenhum usuário atual, não retornar nenhuma playlist.
        if (currentUser == null)
            // TODO: Usuário não conectado. Rever a lógica.
            return null;

        ArrayList<PlaylistItem> result = new ArrayList<PlaylistItem>();
        final UserPlaylistsRequest request = api.getPlaylistsForUser(currentUser.getId()).build();
        try {
            final Page<SimplePlaylist> playlistsPage = request.get();

            for (SimplePlaylist playlist : playlistsPage.getItems()) {

                String name = playlist.getName();
                int numTracks = playlist.getTracks().getTotal();
                String playlistId = playlist.getId();
                String ownerId = playlist.getOwner().getId();
                result.add(new PlaylistItem(name, numTracks, playlistId, ownerId));
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao obter listas de reprodução do usuário. / Unable to get user playlists. Error: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public static User getCurrentUser(Context context) {
        try {
            Log.d(TAG, "Obtendo token válido para transação da API. / Obtaining valid token for API transaction.");
            Token token = PrefsManager.getValidToken(context);

            if (token == null) {
                Log.d(TAG, "Não foi possível obter um token válido. / Unable to obtain valid token.");
                return null;
            } else {
                String accessString = token.getAccessString();
                api.setAccessToken(accessString);
                Log.d(TAG, "Access token obtido pelo getCurrentUser(): / Access token obtained on getCurrentUser(): " + accessString);
                return api.getMe().accessToken(accessString).build().get();
            }

        } catch (Exception e) {
            Log.e(TAG, "EXCEPTION: Não foi possível obter os dados do usuário atual. / Unable to get data about current user. Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void setAuthCredentials(Context context) {
        try {

            String code = PrefsManager.getCodeFromPrefs(context);
            AuthorizationCodeCredentials authorizationCodeCredentials = api.authorizationCodeGrant(code).build().get();
            Token token = new Token(authorizationCodeCredentials);
            if (token == null || token.getAccessString() == null) {
                Log.e(TAG, "Não foi possível obter tokens pelo AuthorizationCodeCredentials. / Unable to get tokens via AuthorizationCodeCredentials");
            } else {
                Log.d(TAG, "Token obtido pelo AuthorizationCodeCredentials: / Token obtained via AuthenticationCodeCredentials:  " + token.getAccessString());
                Log.d(TAG, "Refresh Token obtido pelo AuthorizationCodeCredentials: / Refresh Token obtained via AuthenticationCodeCredentials:  " + token.getRefreshString());
                PrefsManager.setTokenToPrefs(context, token);
            }
        } catch (Exception e) {
            Log.e(TAG,"Não foi possível fazer login à Web API. / Unable to login to Web API. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Token getRefreshedToken(Context context) {
        try {
            Log.d(TAG, "Token expirado, atualizando token... / Token expired, refreshing token...");

            Token currentToken = PrefsManager.getTokenFromPrefs(context);

            api.setAccessToken(currentToken.getAccessString());
            api.setRefreshToken(currentToken.getRefreshString());

            RefreshAccessTokenCredentials refreshRequest = api.refreshAccessToken().build().get();
            String refreshedAccessToken = refreshRequest.getAccessToken();

            if (refreshedAccessToken == null) {
                Log.e(TAG, "Não foi possível obter o token atualizado pelo RefreshAccessTokenCredentials. / Unable to get refreshed token via RefreshAccessTokenCredentials.");
                return null;
            } else {
                Log.d(TAG,"Token atualizado pelo RefreshAccessTokenCredentials: / Refreshed token obtained via RefreshAccessTokenCredentials: " + refreshedAccessToken);
                return new Token(refreshedAccessToken, currentToken.getRefreshString(), refreshRequest.getExpiresIn());
            }
        } catch (Exception e) {
            Log.e(TAG,"Exception: Não foi possível atualizar o access token. / Unable to refresh access token. " );
            e.printStackTrace();
            return null;
        }
    }

}
