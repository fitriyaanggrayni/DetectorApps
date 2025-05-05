package com.example.insectdetector;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class RiwayatDeteksiModel {
    private String nama_file;
    private String hasil;
    private Timestamp timestamp;

    public RiwayatDeteksiModel() {}

    public RiwayatDeteksiModel(String nama_file, String hasil, Timestamp timestamp) {
        this.nama_file = nama_file;
        this.hasil = hasil;
        this.timestamp = timestamp;
    }

    @PropertyName("nama_file")
    public String getNamaFile() {
        return nama_file;
    }

    @PropertyName("hasil")
    public String getHasil() {
        return hasil;
    }

    @PropertyName("timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }
}
