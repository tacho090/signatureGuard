package com.signatureGuard;

import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

public class OpenCVTest {
    public static void main(String[] args) {
        System.out.println("Loading OpenCV...");
        System.out.println(opencv_core.CV_8U);
        Mat srcImage = imread("firmas/firma1.png", 1);
        System.out.println(srcImage);
    }
}
