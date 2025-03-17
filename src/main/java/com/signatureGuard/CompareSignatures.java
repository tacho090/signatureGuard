package com.signatureGuard;

import org.opencv.core.Core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import static com.signatureGuard.Main.KERNEL_SIZE;

public class CompareSignatures {

    public double compareSignatures(String signatureA, String signatureB) {
        System.out.println("Loading images as grayscale");
        Mat img1 = convertToGrayScale(signatureA);
        Mat img2 = convertToGrayScale(signatureB);

        validateImages("loadImages", img1, img2);
        validateImages("size", img1, img2);

        System.out.println("Apply Gaussian Blur to smooth edges");
        applyGaussianBlur(img1, img1, new Size(5, 5));
        applyGaussianBlur(img2, img2, new Size(5, 5));

        System.out.println("Use Edge detection with Canny algorithm");
        applyCannyEdgeDetection(img1, img1, 50, 150, KERNEL_SIZE);
        applyCannyEdgeDetection(img2, img2, 50, 150, KERNEL_SIZE,  false);

        System.out.println("Compute Absolute Difference");
        Mat diff = new Mat();
        Core.absdiff(img1, img2, diff);

        System.out.println("Difference Image Size: " + diff.size());
        System.out.println("Difference Image Type: " + diff.type());

        System.out.println("Calculate the difference score");
        Scalar sumDiff = Core.sumElems(diff);
        double difference = sumDiff.val[0] / (img1.rows() * img1.cols());

        System.out.println("Return a similarity score between 0 and 1");
        return 1 - (difference / 255.0);
    }

    private Mat convertToGrayScale(String signature) {
        return Imgcodecs.imread(signature, Imgproc.COLOR_BGR2GRAY);
    }

    private void applyGaussianBlur(
            Mat sourceImage,
            Mat destinyImage,
            Size kernel
    ) {
        Imgproc.GaussianBlur(
                sourceImage,
                destinyImage,
                new Size(kernel.width, kernel.height),
                0
        );
    }

    private void applyCannyEdgeDetection(
            Mat sourceImage,
            Mat destinyImage,
            Size kernel
    ) {
        Imgproc.Canny(
                sourceImage,
                destinyImage,
                new Size(kernel.width, kernel.height),
                false
        );
    }

    private void validateImages(String condition, Mat img1, Mat img2) {
        switch (condition) {
            case "loadImages":
                if (img1.empty() || img2.empty()) {
                    System.err.println("Error: One or both images could not be loaded.");
                    System.exit(-1);
                }
                break;

            case "size":
                System.out.println("Ensure both images are the same size");
                System.out.println("Image 1 Size: " + img1.size());
                System.out.println("Image 2 Size: " + img2.size());
                System.out.println("Image 1 Type: " + img1.type());
                System.out.println("Image 2 Type: " + img2.type());
                if (!img1.size().equals(img2.size())) {
                    System.err.println("Error: Images have different sizes!");
                    System.exit(-1);
                }
                break;

        }
    }
}
