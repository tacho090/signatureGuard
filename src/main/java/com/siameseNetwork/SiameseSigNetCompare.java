package com.siameseNetwork;

import com.signatureGuard.ResizeImage;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;

import java.text.DecimalFormat;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_core.absdiff;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.Canny;
import static org.bytedeco.opencv.global.opencv_imgproc.GaussianBlur;


public class SiameseSigNetCompare {

    private static final int KERNEL_SIZE = 3;
    private static final String imageDebugDir = "images_debug";

    public String compareSignatures(Mat signatureA, Mat signatureB) {
        System.out.println("Loading images as grayscale");

        validateImages(signatureA, signatureB);

        Mat img1 = convertToGrayScale(signatureA, "firstImageGrayscale");
        Mat img2 = convertToGrayScale(signatureB, "secondImageGrayscale");

        validateImages(img1, img2);
        img2 = ResizeImage.resizeImage(img1, img2);


        System.out.println("Apply Gaussian Blur to smooth edges");
        Size gaussianBlurKernelSize =
                new Size(5, 5);
        applyGaussianBlur(img1, img1, gaussianBlurKernelSize);
        applyGaussianBlur(img2, img2, gaussianBlurKernelSize);
        saveImageToDisk(img1, "Gaussian Blur " + signatureA, "gaussianA");
        saveImageToDisk(img2, "Gaussian Blur " + signatureB, "gaussianB");

        float[] inputTensorA = new float[0];
        float[] inputTensorB = new float[0];

        List<Float> embeddings = getEmbeddings(inputTensorA, inputTensorB);

        return "test";
    }

    private List<Float> getEmbeddings(float[] inputA, float[] inputB) {

    }

    private Mat convertToGrayScale(Mat signatureImage, String identifier) {
        Mat grayScaleImage = new Mat();
        opencv_imgproc.cvtColor(signatureImage, grayScaleImage, opencv_imgproc.COLOR_BGR2GRAY);
        saveImageToDisk(grayScaleImage, "Saving grayscale image ", "grayscale_" + identifier);
        return grayScaleImage;
    }

    private void applyGaussianBlur(
            Mat sourceImage,
            Mat destinyImage,
            Size kernel
    ) {
        GaussianBlur(
            sourceImage,
            destinyImage,
            kernel,
            0,
            0,
            0
        );
    }

    private void validateImages(Mat img1, Mat img2) {
        if (img1.empty() || img2.empty()) {
            System.err.println("Error: One or both images could not be loaded.");
            throw new RuntimeException("Error: images are either empty or null. Cannot process image.");
        }
    }

    public static void saveImageToDisk(Mat imageToSave, String message, String name) {
        System.out.println(message);
        imwrite(String.format("%s/%s.jpg", imageDebugDir, name), imageToSave);
    }
}
