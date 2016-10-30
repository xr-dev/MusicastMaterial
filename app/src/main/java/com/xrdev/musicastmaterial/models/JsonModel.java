package com.xrdev.musicastmaterial.models;

import java.util.ArrayList;

/**
 * Created by Guilherme on 21/01/2015.
 */
public class JsonModel {
    String type;
    String index;
    String videoId;
    String mode;
    ArrayList<TrackItem> tracksMetadata;
    String message;
    TrackItem trackInfo;
    String uuid;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public ArrayList<TrackItem> getTracksMetadata() {
        return tracksMetadata;
    }

    public void setTracksMetadata(ArrayList<TrackItem> tracksMetadata) {
        this.tracksMetadata = tracksMetadata;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TrackItem getTrackInfo() {
        return trackInfo;
    }

    public void setTrackInfo(TrackItem trackInfo) {
        this.trackInfo = trackInfo;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
