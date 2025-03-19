package com.signatureGuard;

import org.bytedeco.opencv.opencv_core.Mat;

public class MatMetadata {
    private Mat mat;
    private String name;

    public MatMetadata(String name, Mat mat) {
        this.name = name;
        this.mat = mat;
    }

    public String getName() {
        return name;
    }

    public Mat getMat() {
        return mat;
    }

}