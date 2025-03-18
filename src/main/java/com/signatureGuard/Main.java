package com.signatureGuard;

import org.bytedeco.opencv.opencv_core.Mat;


public class Main {

    public static void main(String[] args) {
        String signatureAPath = "firmas/firma1.png";
        String signatureBPath = "firmas/firma1.png";
        final Mat sourceSignatureA =
                ImageReader.imageReader(signatureAPath);
        final Mat sourceSignatureB =
                ImageReader.imageReader(signatureBPath);

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
