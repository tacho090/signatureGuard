package com.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
    scanBasePackages = {
        "com.api",
        "com.signatureGuardProcessor",
        "com.utilities"
})
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

}
