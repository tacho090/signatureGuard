package com.api;

import com.signatureGuard.ImageProcessor;
import com.signatureGuard.ImageReader;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController  // Marks the class as a RESTful controller
@RequestMapping("/api")  // All endpoints in this controller will start with /api
public class ImageController {

    private final ImageStorageService imageStorageService;
    private final ImageProcessor imageProcessor;
    public ImageController(
            ImageStorageService imageStorageService,
            ImageProcessor imageProcessor
    ) {
        this.imageStorageService = imageStorageService;
        this.imageProcessor = imageProcessor;
    }

    @PostMapping(value = "/upload-signature", consumes = "multipart/form-data")
    public String uploadSignature(@RequestParam("file1") MultipartFile signature1,
                                  @RequestParam("file2") MultipartFile signature2) {
        try {
            Mat sourceSignatureA = convertMultipartFileToMat(signature1);
            imageStorageService.saveImage("signatureA", sourceSignatureA);
            Mat sourceSignatureB = convertMultipartFileToMat(signature2);
            imageStorageService.saveImage("signatureB", sourceSignatureB);

            return imageProcessor.processImages();
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/get-signature")
    public String getImageSize(String imageName) {
        Mat image = imageStorageService.getImage(imageName);
        if (image != null && !image.empty()) {
            return "Stored image size: " + image.cols() + "x" + image.rows();
        }
        return "No Image uploaded yet.";
    }

    private Mat convertMultipartFileToMat(MultipartFile file) throws IOException {
        Path tempFile = Files.createTempFile("uploaded_", ".png");
        Files.write(tempFile, file.getBytes());
        Mat matImage = ImageReader.imageReader(tempFile.toAbsolutePath().toString());
        Files.delete(tempFile);
        return matImage;
    }
}
