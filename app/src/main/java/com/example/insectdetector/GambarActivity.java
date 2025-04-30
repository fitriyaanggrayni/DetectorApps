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

public class GambarActivity extends AppCompatActivity {

    private ImageView imgGambar;
    private TextView txtFileName, txtDetectionResult;

    private final Handler handler = new Handler();
    private final int REFRESH_INTERVAL = 5000;
    private String lastImageUrl = "";

    private Interpreter tflite;
    private List<String> labels;

    private final int IMAGE_SIZE = 640;

    private final String GOOGLE_SCRIPT_URL = "https://script.google.com/macros/s/AKfycbzfEYCdK3_hgfS9A3TzJprOVETYkepVhFg8C1lQz8-tH8WkrQUJnz22GqDlT5RZYrcjxg/exec?func=ESP32CAM&ID_FOLDER=1bmf4fh86xDNUnPLwoC2jmmPPuId-1lKd";

    private TextView txtJumlahDeteksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gambar);

        imgGambar = findViewById(R.id.imgGambar);
        txtFileName = findViewById(R.id.txtFileName);
        txtDetectionResult = findViewById(R.id.txtDetectionResult);
        txtJumlahDeteksi = findViewById(R.id.txtJumlahDeteksi);


        try {
            tflite = new Interpreter(loadModelFile(), new Interpreter.Options());
            labels = loadLabels();
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

// Buat variabel final
                final String finalImageUrl = getValidGoogleDriveUrl(imageUrl);

                if (!finalImageUrl.equals(lastImageUrl)) {
                    lastImageUrl = finalImageUrl;
                    final String finalFileName = fileName;
                    runOnUiThread(() -> {
                        txtFileName.setText("Gambar Terbaru: " + finalFileName);
                        loadImageAndProcess(finalImageUrl); // gunakan yang final
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
            byteBuffer.putFloat(((pixel >> 16) & 0xFF) / 255.0f); // Red
            byteBuffer.putFloat(((pixel >> 8) & 0xFF) / 255.0f);  // Green
            byteBuffer.putFloat((pixel & 0xFF) / 255.0f);         // Blue
        }
        return byteBuffer;
    }

    private void processImageWithTFLite(Bitmap bitmap) {
        if (tflite == null) return;

        bitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true);
        ByteBuffer inputBuffer = convertBitmapToByteBuffer(bitmap);

        float[][][] output = new float[1][25200][7];
        tflite.run(inputBuffer, output);

        String resultText = parseDetectionOutput(output);
        runOnUiThread(() -> txtDetectionResult.setText(resultText));
    }

    private String parseDetectionOutput(float[][][] output) {
        float confidenceThreshold = 0.7f;
        StringBuilder result = new StringBuilder("Deteksi:\n");
        int detectedCount = 0;

        for (float[] detection : output[0]) {
            float objectness = detection[4];

            if (objectness > confidenceThreshold) {
                float maxClassScore = 0;
                int classIndex = -1;

                for (int i = 5; i < detection.length; i++) {
                    if (detection[i] > maxClassScore) {
                        maxClassScore = detection[i];
                        classIndex = i - 5;
                    }
                }

                float finalConfidence = objectness * maxClassScore;

                if (finalConfidence > confidenceThreshold && classIndex >= 0 && classIndex < labels.size()) {
                    String label = labels.get(classIndex);
                    result.append(label)
                            .append(" (")
                            .append(String.format("%.2f", finalConfidence * 100))
                            .append("%)\n");
                    detectedCount++;
                }
            }
        }

        final int total = detectedCount;
        runOnUiThread(() -> txtJumlahDeteksi.setText("Jumlah Deteksi: " + total));

        return result.length() > 9 ? result.toString() : "Tidak ada objek terdeteksi";
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (tflite != null) tflite.close();
    }
}
