package com.signatureGuard;

import org.bytedeco.opencv.opencv_core.Mat;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.Canny;
import static org.bytedeco.opencv.global.opencv_imgproc.GaussianBlur;
import org.bytedeco.opencv.global.opencv_core;


public class CompareSignatures {

    private static final int KERNEL_SIZE = 3;

    public double compareSignatures(String signatureA, String signatureB) {
        System.out.println("Loading images as grayscale");
        Mat img1 = convertToGrayScale(signatureA);
        Mat img2 = convertToGrayScale(signatureB);

        validateImages(img1, img2);

        if(!ResizeImage.areSameSize(img1, img2)) {
            System.out.println("Images have different sizes! Resizing...");
            ResizeImage.resizeImage(img1, img2);
        }

        System.out.println("Apply Gaussian Blur to smooth edges");
        org.bytedeco.opencv.opencv_core.Size gaussianBlurKernelSize =
                new org.bytedeco.opencv.opencv_core.Size(5, 5);
        applyGaussianBlur(img1, img1, gaussianBlurKernelSize);
        applyGaussianBlur(img2, img2, gaussianBlurKernelSize);

        System.out.println("Use Edge detection with Canny algorithm");
        applyCannyEdgeDetection(img1, img1);
        applyCannyEdgeDetection(img2, img2);

        System.out.println("Compute Absolute Difference");
        Mat diff = new Mat();

        opencv_core.absdiff(img1, img2, diff);

        System.out.println("Difference Image Size: " + diff.size());
        System.out.println("Difference Image Type: " + diff.type());

        System.out.println("Calculate the difference score of all elements in the diff image");
        org.bytedeco.opencv.opencv_core.Scalar sumDiff = opencv_core.sumElems(diff);

        // Output the sum of elements
        System.out.println("Sum of differences: " + sumDiff);
//        double difference = sumDiff.val[0] / (img1.rows() * img1.cols());
//
//        System.out.println("Return a similarity score between 0 and 1");
//        return 1 - (difference / 255.0);
        return 0d;
    }

    private Mat convertToGrayScale(String signature) {
        return imread(signature, 0);
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
}
