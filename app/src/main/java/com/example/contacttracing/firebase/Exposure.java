package com.example.contacttracing.firebase;

public class Exposure {
    private long minutes;
    private long latestDate;
    private String location;
    private String key;

    public Exposure() {
        minutes = 0;
        latestDate = 0;
        location = "";
        key = "";
    }

    public Exposure(long minutes, long latestDate, String location, String key) {
        this.minutes = minutes;
        this.latestDate = latestDate;
        this.location = location;
        this.key = key;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public long getLatestDate() {
        return latestDate;
    }

    public void setLatestDate(long latestDate) {
        this.latestDate = latestDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
