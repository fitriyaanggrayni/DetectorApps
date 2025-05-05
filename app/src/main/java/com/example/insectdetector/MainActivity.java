package com.example.insectdetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Tombol Lokasi
        findViewById(R.id.btnLokasi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { handleLokasi(view);
            }
        });

        //Tombol Gambar
       findViewById(R.id.btnGambar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { handleGambar(view);
            }
        });

        findViewById(R.id.btnRiwayat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { handleRiwayat(view);
            }
        });

    }

    public void handleLokasi(View view) {
        Intent intent = new Intent(this, LokasiActivity.class);
        startActivity(intent);
    }

    public void handleGambar(View view) {
        Intent intent = new Intent(this, GambarActivity.class);
        startActivity(intent);
    }

    public void handleRiwayat(View view) {
        Intent intent = new Intent(this, RiwayatActivity.class);
        startActivity(intent);
    }
}