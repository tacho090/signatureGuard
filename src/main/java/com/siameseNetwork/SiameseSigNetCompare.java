package com.siameseNetwork;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.signatureGuard.ResizeImage;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.opencv.core.CvType;

import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_core.absdiff;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.Canny;
import static org.bytedeco.opencv.global.opencv_imgproc.GaussianBlur;
import com.siameseNetwork.OnnxModelVerifier;


public class SiameseSigNetCompare {

    private static final int KERNEL_SIZE = 3;
    private static final String imageDebugDir = "images_debug";
    private static final double THRESHOLD = 0.5;




    public String compareSignatures(
            Mat signatureA,
            Mat signatureB
    ) {
        try {
            System.out.println("Loading images as grayscale");

            validateImages(signatureA, signatureB);

            Mat img1 = convertToGrayScale(signatureA, "firstImageGrayscale");
            Mat img2 = convertToGrayScale(signatureB, "secondImageGrayscale");

            validateImages(img1, img2);

            Mat resizedImage1 = ResizeImage.resizeImage(img1);
            Mat resizedImage2 = ResizeImage.resizeImage(img1);


            System.out.println("Apply Gaussian Blur to smooth edges");
            Size gaussianBlurKernelSize =
                    new Size(5, 5);
            applyGaussianBlur(resizedImage1, resizedImage1, gaussianBlurKernelSize);
            applyGaussianBlur(resizedImage2, resizedImage2, gaussianBlurKernelSize);
            saveImageToDisk(img1, "Gaussian Blur " + signatureA, "gaussianA");
            saveImageToDisk(img2, "Gaussian Blur " + signatureB, "gaussianB");

            resizedImage1.convertTo(resizedImage1, CvType.CV_32F);
            resizedImage2.convertTo(resizedImage2, CvType.CV_32F);

            // 4. Flatten channel‑first into a Java float array
            int channel = 1;
            int rowsImage1 = resizedImage1.rows(), colsImage1 = resizedImage1.cols();
            float[] inputTensorA = new float[channel * rowsImage1 * colsImage1];

            int rowsImage2 = resizedImage2.rows(), colsImage2 = resizedImage2.cols();
            float[] inputTensorB = new float[channel * rowsImage2 * colsImage2];

            float[][] embeddings = OnnxModelVerifier.getEmbeddings(inputTensorA, inputTensorB);

            double distance = euclideanDistance(embeddings[0], embeddings[1]);
            System.out.printf("Distance between signatures: %.4f\n", distance);

            if (distance < THRESHOLD) {
                return "✔️ Signatures match";
            } else {
                return "❌ Signatures do NOT match";
            }
        } catch (OrtException e) {
            e.printStackTrace();
            return "There was an error";
        }
    }

    /**
     * Computes Euclidean distance between two vectors.
     */
    private double euclideanDistance(float[] a, float[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
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
