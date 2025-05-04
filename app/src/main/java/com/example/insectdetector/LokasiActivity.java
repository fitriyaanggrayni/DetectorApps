package com.example.insectdetector;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LokasiActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private TextView txtLatitude, txtLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lokasi);

        // Inisialisasi Firebase Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference("gps");

        // Inisialisasi TextView
        txtLatitude = findViewById(R.id.txtLatitude);
        txtLongitude = findViewById(R.id.txtLongitude);

        // Inisialisasi Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                Double longitude = dataSnapshot.child("longitude").getValue(Double.class);

                if (latitude != null && longitude != null) {
                    // Tampilkan di peta
                    LatLng lokasi = new LatLng(latitude, longitude);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(lokasi).title("Lokasi GPS"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15.0f));

                    // Tampilkan di UI
                    txtLatitude.setText("Latitude: " + latitude);
                    txtLongitude.setText("Longitude: " + longitude);

                    // Simpan ke Firestore
                    simpanLokasiKeFirestore(latitude, longitude);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Gagal mengambil data GPS", databaseError.toException());
            }
        });
    }

    private void simpanLokasiKeFirestore(Double latitude, Double longitude) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Map<String, Object> dataLokasi = new HashMap<>();
        dataLokasi.put("latitude", latitude);
        dataLokasi.put("longitude", longitude);
        dataLokasi.put("timestamp", new Date());  // Waktu lokal perangkat

        firestore.collection("riwayat_lokasi")
                .add(dataLokasi)
                .addOnSuccessListener(documentReference ->
                        Log.d("Firestore", "Lokasi disimpan"))
                .addOnFailureListener(e ->
                        Log.e("Firestore", "Gagal simpan lokasi", e));
    }
}
