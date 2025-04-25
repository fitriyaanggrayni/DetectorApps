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

public class LokasiActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private TextView txtLatitude, txtLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lokasi);

        // Inisialisasi Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference("gps");

        // Inisialisasi TextView
        txtLatitude = findViewById(R.id.txtLatitude);
        txtLongitude = findViewById(R.id.txtLongitude);

        // Inisialisasi Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Ambil data GPS dari Firebase
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = dataSnapshot.child("longitude").getValue(Double.class);

                    if (latitude != null && longitude != null) {
                        LatLng lokasi = new LatLng(latitude, longitude);

                        // Tambahkan marker dan geser kamera
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(lokasi).title("Lokasi GPS"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15.0f));

                        // Update UI
                        txtLatitude.setText("Latitude: " + latitude);
                        txtLongitude.setText("Longitude: " + longitude);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Gagal mengambil data GPS", databaseError.toException());
            }
        });
    }
}
