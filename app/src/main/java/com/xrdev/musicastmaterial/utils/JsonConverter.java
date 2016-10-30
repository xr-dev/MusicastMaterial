package com.xrdev.musicastmaterial.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.xrdev.musicastmaterial.models.JsonModel;
import com.xrdev.musicastmaterial.models.LocalQueue;
import com.xrdev.musicastmaterial.models.TrackItem;

/**
 * Created by Guilherme on 12/01/2015.
 */
public class JsonConverter {
    private static String TAG = "JSON_CONVERTER";
    private Context mContext;
    private Gson gson = new Gson();
    private String type;
    private String jsonString;
    private JsonModel jsonModel;
    public static String TYPE_LOAD_VIDEO = "loadVideo";
    public static String TYPE_LOAD_PLAYLIST = "loadPlaylist";
    public static String TYPE_PLAY_VIDEO_AT = "playVideoAt";
    public static String TYPE_PLAY_VIDEO = "playVideo";
    public static String TYPE_PAUSE_VIDEO = "pauseVideo";
    public static String TYPE_GET_STATUS = "getStatus";
    public static String TYPE_PLAY_PREVIOUS = "previousVideo";
    public static String TYPE_PLAY_NEXT = "nextVideo";
    public static String TYPE_SHOW_OVERLAY = "showOverlay";
    public static String TYPE_CHANGE_MODE = "changeMode";
    public static String TYPE_TRACK_VOTE = "trackVote";
    public static String TYPE_TRACK_ADD = "trackAdd";
    public static String TYPE_ADD_TO_QUEUE = "addListToQueue";
    public static String TYPE_SWAP_PLAYLIST = "swapPlaylist";
    public static String TYPE_STOP_HOSTING = "stopHosting";
    public static String TYPE_BECOME_ADMIN = "becomeAdmin";

    public JsonConverter(Context context) {
        mContext = context;
    }

    public String makeJson(String type, Object obj){

        if (type.equals(TYPE_LOAD_VIDEO) && obj instanceof TrackItem) {

            jsonModel = new JsonModel();
            jsonModel.setType(type);
            jsonModel.setVideoId(((TrackItem) obj).getYoutubeId());
            jsonModel.setUUID(PrefsManager.getUUID(mContext));

            jsonString = gson.toJson(jsonModel);

        } else {
            // Argumentos incorretos, apresentar mensagem de erro?
        }

        writeLog();

        return jsonString;
    }

    public String makeLoadPlaylistJson(LocalQueue queue, TrackItem track){

        jsonModel = new JsonModel();
        jsonModel.setType(TYPE_LOAD_PLAYLIST);
        jsonModel.setTrackInfo(track);
        jsonModel.setTracksMetadata(queue.getValidTracks());
        jsonModel.setUUID(PrefsManager.getUUID(mContext));

        jsonString = gson.toJson(jsonModel);

        writeLog();

        return jsonString;
    }

    public String makePlayAtJson(int index) {
        jsonModel = new JsonModel();
        jsonModel.setType(TYPE_PLAY_VIDEO_AT);
        jsonModel.setIndex(String.valueOf(index));
        jsonModel.setUUID(PrefsManager.getUUID(mContext));

        jsonString = gson.toJson(jsonModel);

        writeLog();

        return jsonString;

    }

    public String makeAddToQueueJson(LocalQueue queue){

        jsonModel = new JsonModel();
        jsonModel.setType(TYPE_ADD_TO_QUEUE);
        jsonModel.setTracksMetadata(queue.getValidTracks());
        jsonModel.setUUID(PrefsManager.getUUID(mContext));

        jsonString = gson.toJson(jsonModel);

        writeLog();

        return jsonString;
    }

    public String makeSwapPlaylistJson(LocalQueue queue){

        jsonModel = new JsonModel();
        jsonModel.setType(TYPE_SWAP_PLAYLIST);
        jsonModel.setTracksMetadata(queue.getValidTracks());
        jsonModel.setUUID(PrefsManager.getUUID(mContext));

        jsonString = gson.toJson(jsonModel);

        writeLog();

        return jsonString;
    }

    public String makeGeneric(String type) {
        jsonModel = new JsonModel();
        jsonModel.setType(type);
        jsonModel.setUUID(PrefsManager.getUUID(mContext));
        jsonString = gson.toJson(jsonModel);

        return jsonString;
    }

    public String makeModeJson(int mode) {
        jsonModel = new JsonModel();
        jsonModel.setType(TYPE_CHANGE_MODE);
        jsonModel.setMessage(String.valueOf(mode));
        jsonModel.setUUID(PrefsManager.getUUID(mContext));

        jsonString = gson.toJson(jsonModel);

        writeLog();

        return jsonString;
    }

    public String makeTrackVoteJson(TrackItem track) {
        jsonModel = new JsonModel();
        jsonModel.setType(TYPE_TRACK_VOTE);
        jsonModel.setTrackInfo(track);
        jsonModel.setUUID(PrefsManager.getUUID(mContext));

        jsonString = gson.toJson(jsonModel);

        writeLog();

        return jsonString;
    }

    public String makeTrackAddJson(TrackItem track) {
        jsonModel = new JsonModel();
        jsonModel.setType(TYPE_TRACK_ADD);
        jsonModel.setTrackInfo(track);
        jsonModel.setUUID(PrefsManager.getUUID(mContext));

        jsonString = gson.toJson(jsonModel);

        writeLog();

        return jsonString;
    }


    public void writeLog() {
        //Log.d(TAG,"Mensagem enviada JSON: " + jsonString);
    }

}
