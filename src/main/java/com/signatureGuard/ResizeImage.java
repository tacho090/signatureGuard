package com.signatureGuard;

import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;

import java.util.Arrays;
import java.util.List;

public class ResizeImage {

    private static void displaySize(Mat image) {
        System.out.printf(
                "Image 1 Size (height x width):  %d x %d%n",
                image.size().height(), image.size().width());
    }

    public static Boolean areSameSize(
            Mat image1, Mat image2) {
        List<Mat> images = Arrays.asList(image1, image2);
        images.forEach(ResizeImage::displaySize);
        return image1.size().width() == image2.size().width() &&
                image1.size().height() == image2.size().height();
    }

    public static void resizeImage(
            Mat correctlySizedImage,
            Mat imageToBeResized
    ){
        Mat resizedImg = new Mat();
        opencv_imgproc.resize(
                correctlySizedImage,
                imageToBeResized,
                correctlySizedImage.size());
        System.out.println("New image sizes:");
        List<Mat> images = Arrays.asList(
                correctlySizedImage, imageToBeResized);
        images.forEach(ResizeImage::displaySize);
    }
}
