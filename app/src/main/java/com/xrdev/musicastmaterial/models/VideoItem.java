package com.xrdev.musicastmaterial.models;

import java.math.BigInteger;

public class VideoItem {
	private String videoId;
	private String title;
	private String description;
	private BigInteger viewCount;
    private int duration;
    private boolean isLicensed;
	
	public VideoItem(String id, String title, String description, BigInteger viewCount, int duration) {
		this.videoId = id;
		this.title = title;
		this.description = description;
		this.viewCount = viewCount;
        this.duration = duration;
	}

    public VideoItem(String id, int duration){
        this.videoId = id;
        this.duration = duration;
    }

    public VideoItem(String id, int duration, boolean isLicensed){
        this.videoId = id;
        this.duration = duration;
        this.isLicensed = isLicensed;
    }
	
	public String getVideoId() {
		return videoId;
	}
	public void setVideoId(String url) {
		this.videoId = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setViewCount(BigInteger viewCount) {
		this.viewCount = viewCount;
	}
	public BigInteger getViewCountBigInt() {
		return this.viewCount;
	}
	public String getViewCount() {
        return String.valueOf(viewCount);
	}
    public String getDuration() {
        return String.valueOf(duration);
    }
    public int getDurationInt(){
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isLicensed() {
        return isLicensed;
    }

    public void setLicensed(boolean isLicensed) {
        this.isLicensed = isLicensed;
    }
}
