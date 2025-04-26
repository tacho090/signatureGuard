package com.api.config;

import com.api.ImageStorageService;
import com.signatureGuardProcessor.ImageProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for the SignatureGuard application
 * This class bridges the API module with components from other modules
 */
@Configuration
public class ApplicationConfig {

    /**
     * Creates an ImageProcessor bean that can be autowired into controllers
     * 
     * @param imageStorageService The storage service for signature images
     * @return A configured ImageProcessor instance
     */
    @Bean
    public ImageProcessor imageProcessor(ImageStorageService imageStorageService) {
        return new ImageProcessor(imageStorageService);
    }
}
