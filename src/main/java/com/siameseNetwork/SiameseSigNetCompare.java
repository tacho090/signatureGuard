package com.siameseNetwork;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.signatureGuard.ResizeImage;
import com.utilities.AppLogger;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.javacpp.indexer.Indexer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.opencv.core.CvType;
import java.util.Arrays;

import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.bytedeco.opencv.global.opencv_core.CV_8UC1;
import static org.bytedeco.opencv.global.opencv_core.absdiff;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imwrite;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

import com.siameseNetwork.OnnxModelVerifier;


public class SiameseSigNetCompare {

    private static final String imageDebugDir = "images_debug";
    private static final double THRESHOLD = 0.5;
    private static final Logger log =
            AppLogger.getLogger(SiameseSigNetCompare.class);


    public String compareSignatures(
            Mat signatureA,
            Mat signatureB
    ) {
        try {
            log.info("Load onnx model configuration");
            OnnxConfig cfg = new OnnxConfig();
            String onnxPath = cfg.getOnnxModelPath();
            int THRESHOLD = Integer.parseInt(cfg.getOnnxModelThreshold());
            OnnxModelVerifier onnxVerifier = new OnnxModelVerifier(onnxPath);

            log.info("Loading images as grayscale");

            validateImages(signatureA, signatureB);

            Mat img1 = convertToGrayScale(signatureA, "firstImageGrayscale");
            Mat img2 = convertToGrayScale(signatureB, "secondImageGrayscale");

            validateImages(img1, img2);

            Mat resizedImage1 = ResizeImage.resizeImage(img1);
            Mat resizedImage2 = ResizeImage.resizeImage(img2);

            resizedImage1.convertTo(resizedImage1, CvType.CV_32F);
            resizedImage2.convertTo(resizedImage2, CvType.CV_32F);

            SiameseSigNetCompare.saveImageToDisk(
                    resizedImage1, "Save Resized image 1", "resizedImage1");
            SiameseSigNetCompare.saveImageToDisk(
                    resizedImage2, "Save Resized image 2", "resizedImage2");


            int rows1 = resizedImage1.rows(), cols1 = resizedImage1.cols();
            int rows2 = resizedImage2.rows(), cols2 = resizedImage2.cols();
            float[] inputTensorA = new float[rows1 * cols1];
            float[] inputTensorB = new float[rows2 * cols2];

            FloatIndexer fidx1 = resizedImage1.createIndexer();
            int flatIdx = 0;
            for (int y = 0; y < rows1; y++) {
                for (int x = 0; x < cols1; x++) {
                    inputTensorA[flatIdx++] = fidx1.get(y, x);
                }
            }
            fidx1.release();

            FloatIndexer fidx2 = resizedImage2.createIndexer();
            int flatIdx2 = 0;
            for (int y = 0; y < rows2; y++) {
                for (int x = 0; x < cols2; x++) {
                    inputTensorB[flatIdx2++] = fidx2.get(y, x);
                }
            }
            fidx2.release();


            boolean identical = Arrays.equals(inputTensorA, inputTensorB);
            double distance = euclideanDistance(inputTensorA, inputTensorB);
            boolean similar = almostEqual(inputTensorA, inputTensorB, 1e-3f);

            float[][] embeddings = onnxVerifier.getEmbeddings(inputTensorA, inputTensorB);

            // DEBUG: print first few values of each embedding to inspect differences
            int newLength = 128;
            System.out.println("Embedding A (first 5 vals): " +
                    Arrays.toString(Arrays.copyOf(embeddings[0], newLength)));
            System.out.println("Embedding B (first 5 vals): " +
                    Arrays.toString(Arrays.copyOf(embeddings[1], newLength)));

            double euclideanDistance = euclideanDistance(embeddings[0], embeddings[1]);
            System.out.printf("Distance between signatures: %.4f\n", euclideanDistance);

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

    public static boolean almostEqual(float[] a, float[] b, float tol) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (Math.abs(a[i] - b[i]) > tol) return false;
        }
        return true;
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
