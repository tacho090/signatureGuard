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

    /** Returns the ONNX model threshold as defined in model.properties */
    public String getOnnxModelThreshold() {
        return props.getProperty("onnx.model.threshold");
    }

    /** Returns input channels in model.properties */
    public String inputChannels() {
        return props.getProperty("input.channels");
    }

    /** Returns input height in model.properties */
    public String inputHeight() {
        return props.getProperty("input.height");
    }

    /** Returns input width in model.properties */
    public String inputWidth() {
        return props.getProperty("input.width");
    }

}
