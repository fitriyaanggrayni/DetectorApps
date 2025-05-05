package com.example.insectdetector;

import com.google.firebase.Timestamp;

public class RiwayatLokasiModel {
    private double latitude;
    private double longitude;
    private Timestamp timestamp;

    public RiwayatLokasiModel() {}

    public RiwayatLokasiModel(double latitude, double longitude, Timestamp timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


    public Timestamp getTimestamp() {
        return timestamp;
    }
}
