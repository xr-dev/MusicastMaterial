package com.xrdev.musicastmaterial.models;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;


/**
 * Created by Guilherme on 11/04/2016.
 */
public class TrackItem {
    private String trackId;
    private String name;
    private String artists;
    private long duration;
    private String album;
    private int initialPos;
    private List<Image> images;
    private ArrayList<String> votes;

    private String youtubeId;
    public static String VIDEO_NOT_FOUND = "0";

    private String refreshCacheDate;

    public boolean wasCached;

    private int queueIndex;


    public TrackItem(Track apiTrack) {
        this.trackId = apiTrack.id;
        this.name = apiTrack.name;
        this.duration = apiTrack.duration_ms / 1000; // Duração no Spotify API é em milissegundos. Transformar em segundos.
        this.album = apiTrack.album.name;
        this.artists = getArtistsFromApi(apiTrack);
        this.votes = new ArrayList<String>();
        this.images = apiTrack.album.images;
        this.wasCached = false;
    }

    /**
     * Apenas para debugging.
     * @return
     */

    public TrackItem(String name, long duration, String album, String artists){
        this.name = name;
        this.duration = duration;
        this.album = album;
        this.artists = artists;
        this.votes = new ArrayList<>();
        this.wasCached = false;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getArtistsFromApi(Track apiTrack) {
        List<ArtistSimple> artists = apiTrack.artists;
        Iterator iterator = artists.iterator();
        String artistsString = "";

        while (iterator.hasNext()) {
            ArtistSimple artistSimple = (ArtistSimple) iterator.next();
            artistsString += artistSimple.name;
            if (iterator.hasNext())
                artistsString += ", ";
        }
        return artistsString;
    }

    public String getArtists(){
        return artists;
    }

    public String getAlbum() {
        return album;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public int getQueueIndex() {
        return queueIndex;
    }

    public void setQueueIndex(int queueIndex) {
        this.queueIndex = queueIndex;
    }

    public boolean wasSearched(){
        if (youtubeId != null)
            return true;
        else
            return false;
    }

    public boolean wasFound(){
        if (null == youtubeId) {
            return false;
        }

        if (youtubeId.equals(VIDEO_NOT_FOUND))
            return false;
        else
            return true;
    }

    public int getInitialPos() {
        return initialPos;
    }

    public void setInitialPos(int initialPos) {
        this.initialPos = initialPos;
    }

    public ArrayList<String> getVotes() {
        return votes;
    }

    public boolean hasVoted(String uuid) {
        if (votes.contains(uuid)) {
            return true;
        } else {
            return false;
        }
    }

    public void setVotes(ArrayList<String> votes) {
        this.votes = votes;
    }

    public int getVoteCount() {
        return votes.size();
    }

    public String getRefreshCacheDate() {
        return refreshCacheDate;
    }

    public boolean isRefreshNeeded() {
        if (refreshCacheDate == null)
            return false;

        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        DateTime refreshDateTime = fmt.parseDateTime(refreshCacheDate);

        return refreshDateTime.isBeforeNow();

    }

    public void setRefreshCacheDate(String refreshCacheDate) {
        this.refreshCacheDate = refreshCacheDate;
    }

}
