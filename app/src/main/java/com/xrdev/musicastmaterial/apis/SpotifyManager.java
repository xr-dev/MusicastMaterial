package com.xrdev.musicastmaterial.apis;

import android.content.Context;
import android.util.Log;
import com.xrdev.musicastmaterial.Application;
import com.xrdev.musicastmaterial.models.PlaylistItem;
import com.xrdev.musicastmaterial.models.Token;
import com.xrdev.musicastmaterial.models.TrackItem;
import com.xrdev.musicastmaterial.utils.DatabaseHandler;
import com.xrdev.musicastmaterial.utils.PrefsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Guilherme on 22/07/2014.
 */
public class SpotifyManager {
    // IDs:
    public static final String TAG = "SpotifyManager";
    public static final String CLIENT_ID = "befa95e4d007494ea40efcdbd3e1fff7";
    public static final String REDIRECT_URI = "musicast://callback";
    public static final String CLIENT_SECRET = "cffb5db7d8eb4910b3a95527fcee6899";
    public static final int REQUEST_CODE = 9000;

    // Api:
    private SpotifyApi api;
    private SpotifyService apiService;
    private String accessToken;

    // Misc:
    DatabaseHandler dbHandler;

    public SpotifyManager(Context context){
        api = new SpotifyApi();
        apiService = api.getService();
        dbHandler = Application.getDbHandler(context);
    }

    public void setAccessToken(Token token){
        this.accessToken = token.getAccessString();
    }

    public ArrayList<TrackItem> getPlaylistTracks(PlaylistItem playlist, int limit, int offset) {
        if (accessToken == null)
            return null;

        int totalTracks = playlist.getNumTracksInt();
        ArrayList<TrackItem> result = new ArrayList<TrackItem>();

        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, offset);
        options.put(SpotifyService.LIMIT, limit);


        Log.d(TAG, "Total tracks:" + totalTracks);
        try {
            api.setAccessToken(accessToken);

            final Pager<PlaylistTrack> tracksPage =
                    apiService.getPlaylistTracks(
                            playlist.getOwnerId(),
                            playlist.getPlaylistId(),
                            options
                    );
            for (PlaylistTrack playlistTrack : tracksPage.items) {
                TrackItem trackItem = dbHandler.checkForMatch(
                        new TrackItem(playlistTrack.track)
                );
                result.add(trackItem);
            }

        } catch (Exception e) {
            Log.e(TAG, "Erro ao obter faixas de lista de reprodução. / Unable to get playlist tracks. Error: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public int getUserPlaylistsCount() {
        int count = 0;
        if (accessToken == null)
            return 0;
        try {
            api.setAccessToken(accessToken);
            count = apiService.getMyPlaylists().total;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao obter listas de reprodução do usuário. / Unable to get user playlists. Error: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

    public ArrayList<PlaylistItem> getUserPlaylists(int limit, int offset) {
        ArrayList<PlaylistItem> result = new ArrayList<>();
        // Nenhum usuário atual, não retornar nenhuma playlist.
        if (accessToken == null)
            return null;

        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, offset);
        options.put(SpotifyService.LIMIT, limit);

        try {
            api.setAccessToken(accessToken);
            final Pager<PlaylistSimple> playlistsPage = apiService.getMyPlaylists(options);

            for (PlaylistSimple playlist : playlistsPage.items) {
                result.add(new PlaylistItem(playlist));
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro ao obter listas de reprodução do usuário. / Unable to get user playlists. Error: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

//    public static Token getRefreshedToken(Context context) {
//        try {
//            Log.d(TAG, "Token expirado, atualizando token... / Token expired, refreshing token...");
//
//            Token currentToken = PrefsManager.getTokenFromPrefs(context);
//
//            api.setAccessToken(currentToken.getAccessString());
//            api.setRefreshToken(currentToken.getRefreshString());
//
//            RefreshAccessTokenCredentials refreshRequest = api.refreshAccessToken().build().get();
//            String refreshedAccessToken = refreshRequest.getAccessToken();
//
//            if (refreshedAccessToken == null) {
//                Log.e(TAG, "Não foi possível obter o token atualizado pelo RefreshAccessTokenCredentials. / Unable to get refreshed token via RefreshAccessTokenCredentials.");
//                return null;
//            } else {
//                Log.d(TAG,"Token atualizado pelo RefreshAccessTokenCredentials: / Refreshed token obtained via RefreshAccessTokenCredentials: " + refreshedAccessToken);
//                return new Token(refreshedAccessToken, currentToken.getRefreshString(), refreshRequest.getExpiresIn());
//            }
//        } catch (Exception e) {
//            Log.e(TAG,"Exception: Não foi possível atualizar o access token. / Unable to refresh access token. " );
//            e.printStackTrace();
//            return null;
//        }
//    }

}
