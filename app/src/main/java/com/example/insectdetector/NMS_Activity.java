package com.example.insectdetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NMS_Activity {

    public static class Detection {
        public float x, y, width, height, confidence;
        public int classId;

        public Detection(float x, float y, float width, float height, float confidence, int classId) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.confidence = confidence;
            this.classId = classId;
        }

        public float getLeft() { return x; }
        public float getTop() { return y; }
        public float getRight() { return x + width; }
        public float getBottom() { return y + height; }
    }

    public static List<Detection> nonMaxSuppression(List<Detection> detections, float iouThreshold) {
        List<Detection> result = new ArrayList<>();

        // Group by class
        for (int classId = 0; classId < 1000; classId++) {
            List<Detection> classDetections = new ArrayList<>();
            for (Detection det : detections) {
                if (det.classId == classId) {
                    classDetections.add(det);
                }
            }

            // Sort by confidence
            classDetections.sort((d1, d2) -> Float.compare(d2.confidence, d1.confidence));

            boolean[] isSuppressed = new boolean[classDetections.size()];

            for (int i = 0; i < classDetections.size(); i++) {
                if (isSuppressed[i]) continue;
                Detection det1 = classDetections.get(i);
                result.add(det1);

                for (int j = i + 1; j < classDetections.size(); j++) {
                    if (isSuppressed[j]) continue;
                    Detection det2 = classDetections.get(j);

                    if (iou(det1, det2) > iouThreshold) {
                        isSuppressed[j] = true;
                    }
                }
            }
        }

        return result;
    }

    private static float iou(Detection d1, Detection d2) {
        float x1 = Math.max(d1.getLeft(), d2.getLeft());
        float y1 = Math.max(d1.getTop(), d2.getTop());
        float x2 = Math.min(d1.getRight(), d2.getRight());
        float y2 = Math.min(d1.getBottom(), d2.getBottom());

        float intersection = Math.max(0, x2 - x1) * Math.max(0, y2 - y1);
        float area1 = d1.width * d1.height;
        float area2 = d2.width * d2.height;
        float union = area1 + area2 - intersection;

        return union <= 0 ? 0 : intersection / union;
    }
}
