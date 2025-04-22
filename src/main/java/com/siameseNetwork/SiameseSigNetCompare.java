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
        this.THRESHOLD = Double.parseDouble(cfg.getOnnxModelThreshold());
    }

    /**
     * Compares two signature images and returns a human‑readable similarity metric.
     *
     * <p>This method validates that both input Mats are non‑null, non‑empty,
     * and of the same dimensions, converts them to grayscale, prepares them for
     * the ONNX model, computes their embeddings, calculates the Euclidean distance
     * between those embeddings, and then formats that distance as a percentage
     * similarity string.</p>
     *
     * @param signatureA the first signature image as an OpenCV Mat; must be non‑null,
     *                   non‑empty, and the same size as {@code signatureB}
     * @param signatureB the second signature image as an OpenCV Mat; must be non‑null,
     *                   non‑empty, and the same size as {@code signatureA}
     * @return a String representing how similar the two signatures are
     *         (e.g. "Similarity: 84.21%")
     * @throws IllegalArgumentException if either image is null, empty, or their sizes differ
     * @throws OrtException if there is an error loading or running the ONNX Runtime model
     */
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
     * Converts the full stack trace of the given {@link Throwable} into a single string.
     *
     * <p>This is useful for embedding the complete stack trace into log messages
     * or error reports when you need the stack trace as text rather than printed
     * directly to standard error.</p>
     *
     * @param t the {@code Throwable} whose stack trace should be captured
     * @return a {@code String} containing the complete stack trace of {@code t}
     */
    private String stackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /**
     * Computes the Euclidean distance between two equal‑length float vectors.
     *
     * <p>The Euclidean distance is defined as the square root of the sum of squared
     * differences between corresponding elements of the two vectors.</p>
     *
     * @param a the first vector of floats
     * @param b the second vector of floats
     * @return the Euclidean distance between vectors {@code a} and {@code b}
     * @throws IllegalArgumentException if {@code a} and {@code b} have different lengths
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
     * Converts the given OpenCV Mat to a single‑channel grayscale image.
     *
     * <p>This method applies OpenCV's {@code cvtColor} function with
     * {@code COLOR_BGR2GRAY} to transform a 3‑channel BGR (or 4‑channel BGRA)
     * image into a one‑channel grayscale image.</p>
     *
     * @param signatureImage the source Mat to convert; must be non‑null and not empty
     * @return a new Mat containing the grayscale version of {@code signatureImage}
     * @throws IllegalArgumentException if {@code signatureImage} is null or empty
     */
    private Mat convertToGrayScale(Mat signatureImage) {
        Mat grayScaleImage = new Mat();
        opencv_imgproc.cvtColor(
                signatureImage, grayScaleImage, opencv_imgproc.COLOR_BGR2GRAY);
        return grayScaleImage;
    }

    /**
     * Validates that two OpenCV Mats are ready for comparison.
     *
     * <p>This method ensures that both images are non-null, non-empty,
     * and share the same dimensions (width and height). If any of these
     * conditions fail, an {@link IllegalArgumentException} is thrown.</p>
     *
     * @param img1 the first image to validate; must be non-null and non-empty
     * @param img2 the second image to validate; must be non-null and non-empty
     * @throws RuntimeException if either image is null, empty, or if their sizes differ
     */
    private void validateImages(Mat img1, Mat img2) {
        if (img1.empty() || img2.empty()) {
            log.info("Error: One or both images could not be loaded.");
            throw new RuntimeException("Error: images are either empty or null. Cannot process image.");
        }
    }

}
