package com.example.contacttracing.firebase;

public class Exposure {
    private long minutes;
    private long latestDate;
    private String location;

    public Exposure() {
        minutes = 0;
        latestDate = 0;
        location = "";
    }

    public Exposure(long minutes, long latestDate, String location) {
        this.minutes = minutes;
        this.latestDate = latestDate;
        this.location = location;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public Object getLatestDate() {
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
}
