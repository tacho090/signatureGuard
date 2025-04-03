package com.example;

import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.stereotype.Service;

@Service
public class ImageStorageService {
    private Mat storedImage;

    public void saveImage(Mat image) {
        this.storedImage = image;
    }

    public Mat getImage() {
        return this.storedImage;
    }
}
