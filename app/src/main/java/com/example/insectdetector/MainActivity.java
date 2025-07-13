package com.example.insectdetector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private CardView cardLokasi, cardGambar, cardRiwayat;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi Views
        initializeViews();

        // Setup Click Listeners dengan animasi
        setupClickListeners();

        // Animasi masuk untuk cards
        animateCardsOnStart();
    }

    private void initializeViews() {
        cardLokasi = findViewById(R.id.cardLokasi);
        cardGambar = findViewById(R.id.cardGambar);
        cardRiwayat = findViewById(R.id.cardRiwayat);
    }

    private void setupClickListeners() {
        // Card Lokasi dengan animasi klik
        cardLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateCardClick(view, () -> handleLokasi());
            }
        });

        // Card Gambar dengan animasi klik
        cardGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateCardClick(view, () -> handleGambar());
            }
        });

        // Card Riwayat dengan animasi klik
        cardRiwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateCardClick(view, () -> handleRiwayat());
            }
        });
    }

    private void animateCardsOnStart() {
        // Animasi fade in untuk setiap card dengan delay
        cardLokasi.setAlpha(0f);
        cardGambar.setAlpha(0f);
        cardRiwayat.setAlpha(0f);

        cardLokasi.animate().alpha(1f).setDuration(500).setStartDelay(100).start();
        cardGambar.animate().alpha(1f).setDuration(500).setStartDelay(200).start();
        cardRiwayat.animate().alpha(1f).setDuration(500).setStartDelay(300).start();
    }

    private void animateCardClick(View view, Runnable action) {
        // Animasi scale down kemudian scale up
        ObjectAnimator scaleDown = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f);
        scaleDown.setDuration(100);

        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f);
        scaleDownY.setDuration(100);

        scaleDown.start();
        scaleDownY.start();

        handler.postDelayed(() -> {
            ObjectAnimator scaleUp = ObjectAnimator.ofFloat(view, "scaleX", 0.95f, 1f);
            scaleUp.setDuration(100);

            ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.95f, 1f);
            scaleUpY.setDuration(100);

            scaleUp.start();
            scaleUpY.start();

            // Jalankan action setelah animasi
            handler.postDelayed(action, 50);
        }, 100);
    }

    private void handleLokasi() {
        try {
            showToast("📍 Membuka Lokasi...");

            Intent intent = new Intent(this, LokasiActivity.class);
            startActivity(intent);

            // Tambahan: bisa kirim data
            // intent.putExtra("source", "main_activity");

        } catch (Exception e) {
            showToast("⚠️ Fitur Lokasi dalam pengembangan");
        }
    }

    private void handleGambar() {
        try {
            showToast("📸 Memuat Gambar...");

            Intent intent = new Intent(this, GambarActivity.class);
            startActivity(intent);

        } catch (Exception e) {
            showToast("⚠️ Fitur Kamera dalam pengembangan");
        }
    }

    private void handleRiwayat() {
        try {
            showToast("📊 Membuka Riwayat...");

            Intent intent = new Intent(this, RiwayatActivity.class);
            startActivity(intent);

        } catch (Exception e) {
            showToast("⚠️ Fitur Riwayat dalam pengembangan");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Bisa digunakan untuk refresh data atau status
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cleanup handler untuk mencegah memory leaks
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    // Interface untuk callback animasi
    interface AnimationCallback {
        void onAnimationComplete();
    }
}