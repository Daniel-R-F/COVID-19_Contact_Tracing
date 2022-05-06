package com.example.contacttracing.firebase;

import com.google.firebase.database.ServerValue;

public class Contact {
    private Object startTime;
    private Object endTime;

    private String location;

    private long offTime;



    public Contact(String location) {
        startTime = ServerValue.TIMESTAMP;
        endTime = null;
        offTime = 0;

        this.location = location;
    }

    public Object getStartTime() {
        return startTime;
    }

    public void setStartTime(Object startTime) {
        this.startTime = startTime;
    }

    public Object getEndTime() {
        return endTime;
    }

    public void setEndTime(Object endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getOffTime() {
        return offTime;
    }

    public void setOffTime(long offTime) {
        this.offTime = offTime;
    }
}
