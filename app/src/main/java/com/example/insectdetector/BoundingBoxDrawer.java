package com.example.insectdetector;

import android.graphics.*;
import java.util.List;

public class BoundingBoxDrawer {
    private List<String> labels;
    private int imageSize;

    public BoundingBoxDrawer(List<String> labels, int imageSize) {
        this.labels = labels;
        this.imageSize = imageSize;
    }

    public Bitmap draw(Bitmap bitmap, float[][] detections, float threshold) {
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);

        Paint boxPaint = new Paint();
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(4);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(30);
        textPaint.setStyle(Paint.Style.FILL);

        for (float[] detection : detections) {
            float x = detection[0];
            float y = detection[1];
            float w = detection[2];
            float h = detection[3];
            float objectness = detection[4];

            if (objectness > threshold) {
                float maxClassScore = 0;
                int classIndex = -1;

                for (int i = 5; i < detection.length; i++) {
                    if (detection[i] > maxClassScore) {
                        maxClassScore = detection[i];
                        classIndex = i - 5;
                    }
                }

                float finalConfidence = objectness * maxClassScore;

                if (finalConfidence > threshold && classIndex >= 0 && classIndex < labels.size()) {
                    int left = (int)((x - w / 2) * imageSize);
                    int top = (int)((y - h / 2) * imageSize);
                    int right = (int)((x + w / 2) * imageSize);
                    int bottom = (int)((y + h / 2) * imageSize);

                    canvas.drawRect(left, top, right, bottom, boxPaint);
                    canvas.drawText(labels.get(classIndex), left, top - 10, textPaint);
                }
            }
        }

        return mutableBitmap;
    }
}
