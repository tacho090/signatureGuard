package com.example;

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

    public ImageController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }

    @GetMapping("/goodbye")
    public String sayGoodbye() {
        return "Goodbye, World!";
    }

    @GetMapping("/person")
    public Person getPerson() {
        return new Person("John Doe", 30);
    }

    @PostMapping("/person")
    public Person createPerson(@RequestBody Person person) {
        // You can perform logic like saving to the database here
        return person;  // For now, simply return the received object
    }

    @PostMapping(value = "/upload-signature", consumes = "multipart/form-data")
    public String uploadSignature(@RequestParam("file") MultipartFile file) {
        try {
            byte[] imageBytes = file.getBytes();
            Path tempFile = Files.createTempFile("uploaded_", ".jpg");
            Files.write(tempFile, imageBytes);
            Mat matImage = ImageReader.imageReader(tempFile.toAbsolutePath().toString());
            imageStorageService.saveImage(matImage);
            Files.delete(tempFile);
            return "Image uploaded and stored successfully!";
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/get-signature")
    public String getImageSize() {
        Mat image = imageStorageService.getImage();
        if (image != null && !image.empty()) {
            return "Stored image size: " + image.cols() + "x" + image.rows();
        }
        return "No Image uploaded yet.";
    }
}
