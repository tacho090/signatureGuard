package com.siameseNetwork;

import ai.onnxruntime.OrtException;
import com.signatureGuard.ResizeImage;
import com.utilities.AppLogger;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.opencv.core.CvType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;


public class SiameseSigNetCompare {

    private static final String imageDebugDir = "images_debug";
    protected double THRESHOLD;
    private static final Logger log =
            AppLogger.getLogger(SiameseSigNetCompare.class);
    private final String onnxPath;

    public SiameseSigNetCompare() {
        OnnxConfig cfg = new OnnxConfig();
        this.onnxPath = cfg.getOnnxModelPath();
        this.THRESHOLD = Integer.parseInt(cfg.getOnnxModelThreshold());
    }


    public String compareSignatures(
            Mat signatureA,
            Mat signatureB
    ) {
        try {
            Mat[] images = {signatureA, signatureB};

            log.info("Validating images");
            validateImages(images[0], images[1]);


            log.info("Convert images to grayscale");
            Mat[] grayImages = new Mat[images.length];
            String[] grayNames = {
                    "firstImageGrayscale",
                    "secondImageGrayscale"
            };
            for (int i = 0; i < images.length; i++) {
                grayImages[i] = convertToGrayScale(images[i]);
            }

            log.info("Resizing and converting images to new +" +
                    "input weights and heights and 32-bit float 1 channel images");
            Mat[] resizedImages = new Mat[images.length];
            ResizeImage resizeImage = new ResizeImage();
            for (int i = 0; i < grayImages.length; i++) {
                resizedImages[i] = resizeImage.resizeImage(grayImages[i]);
                resizedImages[i].convertTo(resizedImages[i], CvType.CV_32F);
            }

            log.info("Creating tensors");
            int[] rows = new int[resizedImages.length];
            int[] cols = new int[resizedImages.length];
            for (int i = 0; i < resizedImages.length; i++) {
                rows[i] = resizedImages[i].rows();
                cols[i] = resizedImages[i].cols();
            }
            float[] inputTensorA = new float[rows[0] * cols[0]];
            float[] inputTensorB = new float[rows[1] * cols[1]];

            log.info("Generate flat indexes for resized Images");
            for (int i = 0; i < resizedImages.length; i++) {
                FloatIndexer floatIndexer = resizedImages[i].createIndexer();
                int flatIndex = 0;
                for (int y = 0; y < rows[i]; y++) {
                    for (int x = 0; x < cols[i]; x++) {
                        inputTensorA[flatIndex++] = floatIndexer.get(y, x);
                    }
                }
                floatIndexer.release();
            }

            log.info("Load onnx model configuration and run model with signatures");
            OnnxModelVerifier onnxModelVerifier = new OnnxModelVerifier(this.onnxPath);
            float[][] embeddings = onnxModelVerifier.getEmbeddings(
                    inputTensorA, inputTensorB);

            double euclideanDistance = euclideanDistance(embeddings[0], embeddings[1]);
            log.info(String.format("Distance between signatures: %.4f\n", euclideanDistance));

            if (euclideanDistance < THRESHOLD) {
                return String.format(
                        "✔️ Distance between signatures: %.4f\n." +
                                "Euclidean distance Threshold: %.4f\n." +
                                "Signatures match!",
                        euclideanDistance, THRESHOLD);
            } else {
                return String.format(
                        "❌ Distance between signatures: %.4f\n." +
                                "Euclidean distance Threshold: %.4f\n." +
                                "Signatures do NOT match!",
                        euclideanDistance, THRESHOLD);
            }
        } catch (OrtException e) {
            return String.format(
                    "There was an error. Review stacktrace: %s", stackTraceToString(e));
        }
    }

    /**
     * Computes Euclidean distance between two vectors.
     */
    private String stackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
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

    /**
     * Converts images to grayscale 1 channel inputs.
     */
    private Mat convertToGrayScale(Mat signatureImage) {
        Mat grayScaleImage = new Mat();
        opencv_imgproc.cvtColor(
                signatureImage, grayScaleImage, opencv_imgproc.COLOR_BGR2GRAY);
        return grayScaleImage;
    }

    /**
     * Validates images are being imported into the method.
     */
    private void validateImages(Mat img1, Mat img2) {
        if (img1.empty() || img2.empty()) {
            log.info("Error: One or both images could not be loaded.");
            throw new RuntimeException("Error: images are either empty or null. Cannot process image.");
        }
    }

}
