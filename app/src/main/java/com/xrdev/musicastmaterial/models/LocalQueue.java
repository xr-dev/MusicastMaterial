package com.xrdev.musicastmaterial.models;

import java.util.ArrayList;

/**
 * Created by Guilherme on 22/01/2015.
 */
public class LocalQueue {
    private static String mPlaylistId;
    private static LocalQueue sInstance;
    private ArrayList<TrackItem> mTracks;

    public static LocalQueue initialize(String playlistId){
        sInstance = new LocalQueue(playlistId);
        return sInstance;
    }

    public LocalQueue(String playlistId) {
        mPlaylistId = playlistId;
        mTracks = new ArrayList<TrackItem>();
    }

    public String getPlaylistId() {
        return mPlaylistId;
    }

    public void setPlaylistId(String playlistId) {
        mPlaylistId = playlistId;
    }

    public static void setPlaylist(String playlist) {
        mPlaylistId = playlist;
    }


    public ArrayList<TrackItem> getValidTracks() {
        ArrayList<TrackItem> validTracks = new ArrayList<TrackItem>();
        for (TrackItem track : mTracks) {
            if (track.wasFound())
                validTracks.add(track);
        }
        return validTracks;
    }

    public void addTrack(TrackItem track) {
        mTracks.add(track);
    }

    public void clear(){
        mTracks.clear();
    }

    public void setPositions(){
        int count = 0;
        for (TrackItem trackItem : mTracks) {
            trackItem.setInitialPos(count);
            count++;
        }
    }

}
