package com.signatureGuardProcessor;

import com.signatureGuardProcessor.ImageStorage;
import com.siameseNetwork.SiameseSigNetCompare;
import org.bytedeco.opencv.opencv_core.Mat;

/**
 * Processes signature images for comparison
 */
public class ImageProcessor {

    private final ImageStorage imageStorage;

    public ImageProcessor(ImageStorage imageStorage) {
        this.imageStorage = imageStorage;
    }

    public String processImages() {

        final Mat sourceSignatureA = imageStorage.getImage("signatureA");
        final Mat sourceSignatureB = imageStorage.getImage("signatureB");

        SiameseSigNetCompare compareSignatures = new SiameseSigNetCompare();
        if (sourceSignatureA != null && sourceSignatureB != null) {
            String signatureSimilarity = compareSignatures
                    .compareSignatures(sourceSignatureA, sourceSignatureB);
            return
                    "The similarity between the signatures is: "
                            + signatureSimilarity + "%";

        } else {
            return "Error loading images.";
        }

    }

}
