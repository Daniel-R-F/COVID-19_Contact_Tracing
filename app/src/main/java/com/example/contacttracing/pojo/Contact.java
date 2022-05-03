package com.example.contacttracing.pojo;

import com.google.firebase.database.ServerValue;

public class Contact {
    private Object startTime;
    private Object endTime;

    private String location;
    private String contactUid;



    public Contact(String contactUid, String location) {
        startTime = ServerValue.TIMESTAMP;

        this.contactUid = contactUid;
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

    public String getContactUid() {
        return contactUid;
    }

    public void setContactUid(String contactUid) {
        this.contactUid = contactUid;
    }
}
