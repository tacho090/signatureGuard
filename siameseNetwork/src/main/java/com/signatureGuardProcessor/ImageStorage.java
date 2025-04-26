package com.signatureGuardProcessor;

import org.bytedeco.opencv.opencv_core.Mat;

/**
 * Interface for image storage operations
 */
public interface ImageStorage {
    /**
     * Store an image with the given name
     * @param name A unique name for the image
     * @param image The image to store
     */
    void saveImage(String name, Mat image);
    
    /**
     * Retrieve an image by its name
     * @param name The image name
     * @return The image, or null if not found
     */
    Mat getImage(String name);
}
