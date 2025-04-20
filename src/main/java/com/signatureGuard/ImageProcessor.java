package com.signatureGuard;

import com.api.ImageStorageService;
import com.siameseNetwork.SiameseSigNetCompare;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.stereotype.Service;

@Service
public class ImageProcessor {

    private final ImageStorageService imageStorageService;

    public ImageProcessor(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    public String processImages() {

        final Mat sourceSignatureA = imageStorageService.getImage("signatureA");
        final Mat sourceSignatureB = imageStorageService.getImage("signatureB");

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

//        CompareSignatures compareSignatures = new CompareSignatures();
//        if (sourceSignatureA != null && sourceSignatureB != null) {
//            String signatureSimilarity = compareSignatures
//                    .compareSignatures(sourceSignatureA, sourceSignatureB);
//            return
//                    "The similarity between the signatures is: "
//                            + signatureSimilarity + "%";
//
//        } else {
//            return "Error loading images.";
//        }
    }

}
