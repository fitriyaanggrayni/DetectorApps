package com.example.insectdetector;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.tensorflow.lite.Interpreter;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.res.AssetFileDescriptor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.view.View;
import android.widget.ProgressBar;

import android.content.Intent;
import android.net.Uri;


public class GambarActivity extends AppCompatActivity {
    private BoundingBoxDrawer boxDrawer;
    private ImageView imgGambar;
    private TextView txtFileName, txtDetectionResult;
    private final Handler handler = new Handler();
    private final int REFRESH_INTERVAL = 5000;
    private String lastImageUrl = "";
    private String lastFileName = "";
    private Interpreter tflite;
    private List<String> labels;
    private final int IMAGE_SIZE = 640;
    private final String GOOGLE_SCRIPT_URL = "https://script.google.com/macros/s/AKfycbzfEYCdK3_hgfS9A3TzJprOVETYkepVhFg8C1lQz8-tH8WkrQUJnz22GqDlT5RZYrcjxg/exec?func=ESP32CAM&ID_FOLDER=1bmf4fh86xDNUnPLwoC2jmmPPuId-1lKd";
    private ProgressBar progressBar;
    private long startTime;
    private static final int REQUEST_PICK_IMAGE = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gambar);

        imgGambar = findViewById(R.id.imgGambar);
        txtFileName = findViewById(R.id.txtFileName);
        txtDetectionResult = findViewById(R.id.txtDetectionResult);
        progressBar = findViewById(R.id.progressBar);
        findViewById(R.id.btnPilihGambarLokal).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_PICK_IMAGE);
        });


        try {
            tflite = new Interpreter(loadModelFile(), new Interpreter.Options());
            labels = loadLabels();
            boxDrawer = new BoundingBoxDrawer(labels, IMAGE_SIZE, 0.5f);
        } catch (IOException e) {
            Log.e("TFLite", "Gagal memuat model atau label", e);
        }

        startAutoRefresh();
    }

    private void startAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchLatestFileFromDrive();
                handler.postDelayed(this, REFRESH_INTERVAL);
            }
        }, REFRESH_INTERVAL);
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd("best-fp16.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        return inputStream.getChannel().map(FileChannel.MapMode.READ_ONLY, fileDescriptor.getStartOffset(), fileDescriptor.getDeclaredLength());
    }

    private List<String> loadLabels() {
        List<String> loadedLabels = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("labels.txt")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                loadedLabels.add(line);
            }
        } catch (IOException e) {
            Log.e("TFLite", "Gagal membaca labels.txt!", e);
        }
        return loadedLabels;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                Glide.with(this)
                        .asBitmap()
                        .load(selectedImageUri)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                imgGambar.setImageBitmap(resource);
                                processImageWithTFLite(resource);
                            }

                            @Override
                            public void onLoadCleared(Drawable placeholder) {}
                        });

                txtFileName.setText("Gambar dari galeri");
                findViewById(R.id.loadingOverlay).setVisibility(View.VISIBLE);
                startTime = System.currentTimeMillis();
            }
        }
    }


    private void fetchLatestFileFromDrive() {
        new Thread(() -> {
            try {
                URL url = new URL(GOOGLE_SCRIPT_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());

                String imageUrl = "";
                String fileName = "";
                if (jsonResponse.has("files")) {
                    JSONArray files = jsonResponse.getJSONArray("files");
                    if (files.length() > 0) {
                        JSONObject latestFile = files.getJSONObject(0);
                        imageUrl = latestFile.getString("url");
                        fileName = latestFile.getString("name");
                    }
                } else if (jsonResponse.has("url") && jsonResponse.has("name")) {
                    imageUrl = jsonResponse.getString("url");
                    fileName = jsonResponse.getString("name");
                }

                final String finalImageUrl = getValidGoogleDriveUrl(imageUrl);

                if (!finalImageUrl.equals(lastImageUrl)) {
                    lastImageUrl = finalImageUrl;
                    lastFileName = fileName;
                    runOnUiThread(() -> {
                        txtFileName.setText("Gambar Terbaru: " + lastFileName);
                        loadImageAndProcess(finalImageUrl);
                    });
                }
            } catch (Exception e) {
                Log.e("DriveFetch", "Gagal mengambil gambar dari Google Drive", e);
            }
        }).start();
    }

    private String getValidGoogleDriveUrl(String fileUrl) {
        if (fileUrl.contains("/d/")) {
            String fileId = fileUrl.split("/d/")[1].split("/")[0];
            return "https://drive.google.com/uc?export=view&id=" + fileId;
        }
        return fileUrl;
    }

    private void loadImageAndProcess(String imageUrl) {
        // Tampilkan loading
        runOnUiThread(() -> findViewById(R.id.loadingOverlay).setVisibility(View.VISIBLE));
        startTime = System.currentTimeMillis();


        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        imgGambar.setImageBitmap(resource);
                        processImageWithTFLite(resource);
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) { }
                });
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(IMAGE_SIZE * IMAGE_SIZE * 3 * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[IMAGE_SIZE * IMAGE_SIZE];
        bitmap.getPixels(intValues, 0, IMAGE_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE);

        for (int pixel : intValues) {
            byteBuffer.putFloat(((pixel >> 16) & 0xFF) / 255.0f);
            byteBuffer.putFloat(((pixel >> 8) & 0xFF) / 255.0f);
            byteBuffer.putFloat((pixel & 0xFF) / 255.0f);
        }
        return byteBuffer;
    }

    private void processImageWithTFLite(Bitmap bitmap) {
        if (tflite == null) return;

        bitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true);
        ByteBuffer inputBuffer = convertBitmapToByteBuffer(bitmap);

        float[][][] output = new float[1][25200][7];
        tflite.run(inputBuffer, output);

        List<NMS_Activity.Detection> detections = new ArrayList<>();
        for (float[] detection : output[0]) {
            float objectness = detection[4];
            if (objectness > 0.5) {
                float maxClassScore = 0;
                int classIndex = -1;
                for (int i = 5; i < detection.length; i++) {
                    if (detection[i] > maxClassScore) {
                        maxClassScore = detection[i];
                        classIndex = i - 5;
                    }
                }

                float finalConfidence = objectness * maxClassScore;
                if (finalConfidence > 0.5 && classIndex >= 0 && classIndex < labels.size()) {
                    detections.add(new NMS_Activity.Detection(
                            detection[0], detection[1], detection[2], detection[3],
                            finalConfidence, classIndex
                    ));
                }
            }
        }

        List<NMS_Activity.Detection> finalDetections = NMS_Activity.nonMaxSuppression(detections, 0.5f);

        Bitmap annotated = boxDrawer.draw(bitmap, finalDetections);
        runOnUiThread(() -> imgGambar.setImageBitmap(annotated));

        String hasilDeteksi = parseDetectionOutput(finalDetections);
        long endTime = System.currentTimeMillis();
        long detectionDuration = endTime - startTime;
        double seconds = detectionDuration / 1000.0;
        String hasilDeteksiAkhir = hasilDeteksi + "\n Waktu Deteksi: " + String.format(Locale.getDefault(), "%.2f", seconds) + " detik";
        runOnUiThread(() -> {
            txtDetectionResult.setText(hasilDeteksiAkhir);
            kirimKeRealtimeDatabase(hasilDeteksiAkhir);
            simpanKeFirestore(hasilDeteksiAkhir, lastFileName);
            findViewById(R.id.loadingOverlay).setVisibility(View.GONE);
        });
    }

    private String parseDetectionOutput(List<NMS_Activity.Detection> detections) {
        StringBuilder result = new StringBuilder();
        int totalDeteksi = 0;
        Map<String, Integer> labelCounts = new HashMap<>();

        for (NMS_Activity.Detection detection : detections) {
            String label = labels.get(detection.classId);
            labelCounts.put(label, labelCounts.getOrDefault(label, 0) + 1);
            totalDeteksi++;
        }

        if (totalDeteksi == 0) {
            return "Tidak ada objek terdeteksi";
        }

        result.append("Jumlah Deteksi:\n");
        for (Map.Entry<String, Integer> entry : labelCounts.entrySet()) {
            result.append(entry.getKey())
                    .append(" = ")
                    .append(entry.getValue())
                    .append("\n");
        }
        result.append("Total Serangga = ").append(totalDeteksi);
        return result.toString();
    }

    private void kirimKeRealtimeDatabase(String hasilDeteksi) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Hasil Deteksi");

        // Format timestamp ke tanggal dan waktu
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String waktuSekarang = sdf.format(new Date());

        Map<String, Object> data = new HashMap<>();
        data.put("hasil", hasilDeteksi);
        data.put("timestamp", waktuSekarang); //

        ref.setValue(data)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Realtime DB Berhasil"))
                .addOnFailureListener(e -> Log.e("Firebase", "Gagal simpan Realtime DB", e));
    }


    private void simpanKeFirestore(String hasilDeteksiAkhir, String fileName) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("riwayat_deteksi")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Jika sudah ada 20 data, hapus yang paling lama
                    if (queryDocumentSnapshots.size() >= 20) {
                        int totalToDelete = queryDocumentSnapshots.size() - 19;
                        for (int i = 0; i < totalToDelete; i++) {
                            firestore.collection("riwayat_deteksi")
                                    .document(queryDocumentSnapshots.getDocuments().get(i).getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Deteksi lama dihapus"))
                                    .addOnFailureListener(e -> Log.e("Firestore", "Gagal hapus deteksi lama", e));
                        }
                    }

                    // Simpan deteksi baru
                    Map<String, Object> data = new HashMap<>();
                    data.put("hasil", hasilDeteksiAkhir);
                    data.put("nama_file", fileName);
                    data.put("timestamp", new Date());

                    firestore.collection("riwayat_deteksi")
                            .add(data)
                            .addOnSuccessListener(documentReference -> Log.d("Firestore", "Deteksi disimpan"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Gagal simpan deteksi", e));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Gagal membaca koleksi", e));
    }


}
