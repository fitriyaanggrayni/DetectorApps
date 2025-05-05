package com.example.insectdetector;

import com.google.firebase.Timestamp;

public class RiwayatModel {
    private String id;
    private Timestamp timestamp;
    private String lokasi;
    private String deteksi;

    public RiwayatModel() {}

    public RiwayatModel(String id, Timestamp timestamp, String lokasi, String deteksi) {
        this.id = id;
        this.timestamp = timestamp;
        this.lokasi = lokasi;
        this.deteksi = deteksi;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getDeteksi() {
        return deteksi;
    }

    public void setDeteksi(String deteksi) {
        this.deteksi = deteksi;
    }
}
