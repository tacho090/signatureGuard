package com.signatureGuard;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import org.bytedeco.opencv.opencv_core.Mat;

import static com.signatureGuard.ImageReader.imageReader;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;


public class Main {


    public static void main(String[] args) {
        String signatureAPath = "firmas/firma1.png";
        String signatureBPath = "firmas/firma1.png";
        final Mat sourceSignatureA = imageReader(signatureAPath);
        final Mat sourceSignatureB = imageReader(signatureBPath);


        CompareSignatures compareSignatures = new CompareSignatures();
        if (sourceSignatureA != null && sourceSignatureB != null) {
            double signatureSimilarity = compareSignatures
                    .compareSignatures(signatureAPath, signatureBPath);
            System.out.println(
                    "The similarity between the signatures is: "
                            + (signatureSimilarity * 100) + "%");
        } else {
            System.out.println("Error loading images.");
        }

    }

}
