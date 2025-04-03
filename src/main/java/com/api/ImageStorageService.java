package com.api;

import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class ImageStorageService {
    private final ConcurrentHashMap<String, Mat> storedImages = new ConcurrentHashMap<>();

    public void saveImage(String name, Mat image) {
        storedImages.put(name, image);
    }

    public Mat getImage(String name) {
        return storedImages.get(name);
    }
}
