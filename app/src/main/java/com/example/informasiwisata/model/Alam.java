package com.example.informasiwisata.model;

public class Alam {
    private String id,judul,deskripsi,editgambar;
    long timestamp;

    public Alam() {
    }

    public Alam(String id, String judul, String deskripsi, String editgambar, long timestamp) {
        this.id = id;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.editgambar = editgambar;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getEditgambar() {
        return editgambar;
    }

    public void setEditgambar(String editgambar) {
        this.editgambar = editgambar;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
