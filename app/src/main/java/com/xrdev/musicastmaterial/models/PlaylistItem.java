package com.xrdev.musicastmaterial.models;

/**
 * Created by Guilherme on 11/04/2016.
 */
public class PlaylistItem {
    private String name;
    private int numTracks;
    private String playlistId;
    private String ownerId;

    public PlaylistItem(String name, int numTracks, String playlistId, String ownerId) {
        this.name = name;
        this.numTracks = numTracks;
        this.playlistId = playlistId;
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumTracks() {
        return Integer.toString(numTracks);
    }

    public int getNumTracksInt() {
        return numTracks;
    }

    public void setNumTracks(int numTracks) {
        this.numTracks = numTracks;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

}
