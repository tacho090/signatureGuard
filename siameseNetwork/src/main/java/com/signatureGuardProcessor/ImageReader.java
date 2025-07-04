package com.signatureGuardProcessor;

import org.bytedeco.opencv.opencv_core.Mat;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;


public class ImageReader {

    public static Mat imageReader(String imagePath) {
        Mat srcImage = imread(imagePath, 1);
        try {
            if(srcImage.empty()) {
                System.out.println("Image could not be loaded");
            } else {
                System.out.println("Image loaded succesfully");
            }
        } catch(Exception e) {
            System.out.println("Exception logged " + e);
            return null;
        }
        return srcImage;
    }

}
