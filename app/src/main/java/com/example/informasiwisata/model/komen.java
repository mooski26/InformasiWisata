package com.example.informasiwisata.model;

public class komen {
    String id, wisataId, timestamp, comment, uid;

    public komen(){
    }

    public komen(String id, String wisataId, String timestamp, String comment, String uid) {
        this.id = id;
        this.wisataId = wisataId;
        this.timestamp = timestamp;
        this.comment = comment;
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWisataId() {
        return wisataId;
    }

    public void setWisataId(String wisataId) {
        this.wisataId = wisataId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
