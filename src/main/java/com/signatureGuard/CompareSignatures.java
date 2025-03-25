package com.signatureGuard;

import org.bytedeco.opencv.opencv_core.Mat;

import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.opencv.global.opencv_imgproc.Canny;
import static org.bytedeco.opencv.global.opencv_imgproc.GaussianBlur;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Scalar;

import java.text.DecimalFormat;

import static org.bytedeco.opencv.global.opencv_core.absdiff;



public class CompareSignatures {

    private static final int KERNEL_SIZE = 3;
    private static final String imageDebugDir = "images_debug";

    public String compareSignatures(String signatureA, String signatureB) {
        System.out.println("Loading images as grayscale");
        System.out.println("Signature locations: " + signatureA);
        System.out.println("Signature locations: " + signatureB);

        Mat img1 = convertToGrayScale(signatureA, "firstImageGrayscale");
        Mat img2 = convertToGrayScale(signatureB, "secondImageGrayscale");

        validateImages(img1, img2);

        if(!ResizeImage.areSameSize(img1, img2)) {
            System.out.println("Images have different sizes! Resizing...");
            img2 = ResizeImage.resizeImage(img1, img2);
        }

        System.out.println("Apply Gaussian Blur to smooth edges");
        org.bytedeco.opencv.opencv_core.Size gaussianBlurKernelSize =
                new org.bytedeco.opencv.opencv_core.Size(5, 5);
        applyGaussianBlur(img1, img1, gaussianBlurKernelSize);
        applyGaussianBlur(img2, img2, gaussianBlurKernelSize);
        saveImageToDisk(img1, "Gaussian Blur " + signatureA, "gaussianA");
        saveImageToDisk(img2, "Gaussian Blur " + signatureB, "gaussianB");


        System.out.println("Use Edge detection with Canny algorithm");
        applyCannyEdgeDetection(img1, img1);
        applyCannyEdgeDetection(img2, img2);
        saveImageToDisk(img1, "Canny Detection " + signatureA, "cannyA");
        saveImageToDisk(img2, "Canny Detection " + signatureB, "cannyB");

        validateImages(img1, img2);

        System.out.println("Compute Absolute Difference");
        Mat diff = new Mat(173, 302, CV_8UC1);
        absdiff(img1, img2, diff);
        imwrite(String.format("%s/difference.jpg", imageDebugDir), diff);
        System.out.println("Absolute Difference: " + diff);

        System.out.println("Difference Image Size: " + diff.size());
        System.out.println("Difference Image Type: " + diff.type());
        saveImageToDisk(img1, "Saving absolute difference image", "absolute_difference");


        System.out.println("Calculate the difference score of all elements in the diff image");
        Scalar sumDiff = opencv_core.sumElems(diff);

        // Output the sum of elements
        System.out.println("Sum of differences: " + sumDiff);
        // Calculate the total number of pixels in the image (for a grayscale image)
        int totalPixels = img1.rows() * img1.cols();

        // Maximum possible difference for a grayscale image (255 * total number of pixels)
        double maxPossibleDifference = 255 * totalPixels;

        // Calculate the sum of differences (grayscale images have only one channel)
        double sumOfDifferences = sumDiff.get(0);  // Using val(0) to get the sum of grayscale pixels

        // Calculate the similarity percentage
        double similarityPercentage;
        similarityPercentage = (1 - (sumOfDifferences / maxPossibleDifference)) * 100;

        // Debugging output: Check the calculated similarity percentage before clamping
        System.out.println("Similarity Percentage (Before Clamping): " + similarityPercentage);

        // Ensure the similarity percentage is between 0% and 100% (clamp values)
        similarityPercentage = Math.max(0, Math.min(100, similarityPercentage));

        // Print the result
        System.out.println("Similarity Percentage (Clamped): " + similarityPercentage + "%");
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(similarityPercentage);
    }

    private Mat convertToGrayScale(String signature, String identifier) {
        Mat grayscaleImage = imread(signature, IMREAD_GRAYSCALE);
        System.out.println("Image channels: " + grayscaleImage.channels()); // Should print 1
        saveImageToDisk(grayscaleImage, "Saving grayscale image " + signature, "grayscale_" + identifier);
        return grayscaleImage;
    }

    private void applyGaussianBlur(
            Mat sourceImage,
            Mat destinyImage,
            org.bytedeco.opencv.opencv_core.Size kernel
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

    private void applyCannyEdgeDetection(
            Mat sourceImage,
            Mat destinyImage
    ) {
        Canny(
                sourceImage,
                destinyImage,
                50d,
                150d
        );
    }

    private void validateImages(Mat img1, Mat img2) {
        if (img1.empty() || img2.empty()) {
            System.err.println("Error: One or both images could not be loaded.");
            System.exit(-1);
        }
    }

    public static void saveImageToDisk(Mat imageToSave, String message, String name) {
        System.out.println(message);
        imwrite(String.format("%s/%s.jpg", imageDebugDir, name), imageToSave);
    }
}
