package com.siameseNetwork;

import java.io.InputStream;
import java.util.Properties;

public class OnnxConfig {
    private static final String CONFIG_FILE = "model.properties";
    private final Properties props = new Properties();

    public OnnxConfig() {
        try (InputStream in = getClass()
                .getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new RuntimeException(CONFIG_FILE + " not found on classpath");
            }
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    /** Returns the ONNX model path as defined in model.properties */
    public String getOnnxModelPath() {
        return props.getProperty("onnx.model.path");
    }
}
