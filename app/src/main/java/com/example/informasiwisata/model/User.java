package com.example.informasiwisata.model;

public class User {
    private String id, timestamp, judul, deskripsi, gambar;

    public User(String ids, String timestamps, String juduls, String deskripsis, String gambars) {
        this.id = ids;
        this.timestamp = timestamps;
        this.gambar = gambars;
        this.judul= juduls;
        this.deskripsi = deskripsis;
    }

    public String getIds() {
        return id;
    }

    public void setIds(String ids) {
        this.id = ids;
    }

    public String getTimestamps() {
        return timestamp;
    }

    public void setTimestamps(String timestamps) {
        this.timestamp = timestamps;
    }

    public String getJuduls() {
        return judul;
    }

    public void setJuduls(String juduls) {
        this.judul = juduls;
    }

    public String getDeskripsis() {
        return deskripsi;
    }

    public void setDeskripsis(String deskripsis) {
        this.deskripsi = deskripsis;
    }

    public String getGambars() {
        return gambar;
    }

    public void setGambars(String gambars) {
        this.gambar = gambars;
    }
}
