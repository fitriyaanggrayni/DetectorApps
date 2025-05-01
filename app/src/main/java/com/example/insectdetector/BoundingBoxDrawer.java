package com.example.insectdetector;

import android.graphics.*;
import java.util.List;

public class BoundingBoxDrawer {
    private List<String> labels;
    private int imageSize;
    private float threshold;

    // Tambahkan daftar warna
    private final int[] classColors = {
            Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.CYAN, Color.YELLOW, Color.GRAY, Color.WHITE
    };

    public BoundingBoxDrawer(List<String> labels, int imageSize, float threshold) {
        this.labels = labels;
        this.imageSize = imageSize;
        this.threshold = threshold;
    }

    public Bitmap draw(Bitmap bitmap, List<NMS_Activity.Detection> detections) {
        Bitmap annotatedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(annotatedBitmap);

        Paint boxPaint = new Paint();
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(3);

        Paint textPaint = new Paint();
        textPaint.setTextSize(30);
        textPaint.setFakeBoldText(true);

        for (NMS_Activity.Detection detection : detections) {
            if (detection.confidence >= threshold) {
                float cx = detection.x * imageSize;
                float cy = detection.y * imageSize;
                float w = detection.width * imageSize;
                float h = detection.height * imageSize;

                float left = cx - w / 2;
                float top = cy - h / 2;
                float right = cx + w / 2;
                float bottom = cy + h / 2;

                int color = classColors[detection.classId % classColors.length];  // Ambil warna berdasarkan classId

                boxPaint.setColor(color);
                textPaint.setColor(color);

                RectF rect = new RectF(left, top, right, bottom);
                canvas.drawRect(rect, boxPaint);

                String label = String.format("%s %.2f%%", labels.get(detection.classId), detection.confidence * 100);
                canvas.drawText(label, left, top - 10, textPaint);
            }
        }

        return annotatedBitmap;
    }
}
