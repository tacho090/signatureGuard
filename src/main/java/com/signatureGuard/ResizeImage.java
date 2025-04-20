package com.signatureGuard;

import com.siameseNetwork.SiameseSigNetCompare;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;

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
        SiameseSigNetCompare.saveImageToDisk(image1, "Saved sourceSignatureA", "sourceSignatureA");
        SiameseSigNetCompare.saveImageToDisk(image2, "Saved sourceSignatureB", "sourceSignatureB");

        return image1.size().width() == image2.size().width() &&
                image1.size().height() == image2.size().height();
    }

    public static Mat resizeImage(
            Mat imageToBeResized
    ){
        Mat resizedImage = new Mat();
        opencv_imgproc.resize(
                imageToBeResized,
                resizedImage,
                new Size(128, 128));
        return resizedImage;
    }
}
