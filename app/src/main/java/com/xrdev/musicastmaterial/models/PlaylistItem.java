package com.xrdev.musicastmaterial.models;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

/**
 * Created by Guilherme on 11/04/2016.
 */
public class PlaylistItem {
    private String name;
    private int numTracks;
    private String playlistId;
    private String ownerId;
    private String imageUrl;

    public PlaylistItem(PlaylistSimple apiPlaylist) {
        this.name = apiPlaylist.name;
        this.numTracks = apiPlaylist.tracks.total;
        this.playlistId = apiPlaylist.id;
        this.ownerId = apiPlaylist.owner.id;
        if (apiPlaylist.images.size() >= 1)
            this.imageUrl = apiPlaylist.images.get(0).url;
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

    public String getImageUrl() {
        return imageUrl;
    }

}
