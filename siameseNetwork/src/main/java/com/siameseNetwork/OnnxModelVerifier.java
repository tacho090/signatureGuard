package com.siameseNetwork;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.utilities.AppLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.logging.Logger;


public class OnnxModelVerifier {

    private final OrtEnvironment env ;
    private final OrtSession session;
    private static final Logger log =
            AppLogger.getLogger(OnnxModelVerifier.class);

    public OnnxModelVerifier(
            String modelPath) throws OrtException {
        log.info("Create the runtime environment");
        env = OrtEnvironment.getEnvironment();

        try {
            // First try to load as a classpath resource
            log.info("Attempting to load model from classpath: " + modelPath);
            File modelFile = createTempFileFromResource(modelPath);
            
            if (modelFile != null) {
                log.info("Loading model from temp file: " + modelFile.getAbsolutePath());
                session = env.createSession(modelFile.getAbsolutePath(), new OrtSession.SessionOptions());
            } else {
                // Fall back to direct file path
                log.info("Falling back to direct file path: " + modelPath);
                session = env.createSession(modelPath, new OrtSession.SessionOptions());
            }
        } catch (Exception e) {
            log.severe("Error loading ONNX model: " + e.getMessage());
            // Re-throw original OrtException or wrap other exceptions
            if (e instanceof OrtException) {
                throw (OrtException) e;
            } else {
                // For other exceptions, just log and throw a simple OrtException
                throw new OrtException(1, "Failed to load ONNX model: " + e.getMessage());
            }
        }
    }
    
    /**
     * Create a temporary file from a classpath resource
     */
    private File createTempFileFromResource(String resourcePath) {
        try {
            log.info("Looking for resource: " + resourcePath);
            // Strip off any leading path components like 'src/main/resources/'
            String cleanPath = resourcePath;
            if (cleanPath.contains("resources/")) {
                cleanPath = cleanPath.substring(cleanPath.indexOf("resources/") + "resources/".length());
            }
            
            log.info("Cleaned resource path: " + cleanPath);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(cleanPath);
            
            if (inputStream == null) {
                log.warning("Resource not found in classpath: " + cleanPath);
                return null;
            }
            
            // Create temp file with .onnx extension
            File tempFile = File.createTempFile("model", ".onnx");
            tempFile.deleteOnExit();
            
            // Copy stream to temp file
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();
            
            return tempFile;
        } catch (IOException e) {
            log.warning("Failed to create temp file from resource: " + e.getMessage());
            return null;
        }
    }

    public float[][] getEmbeddings(
            float[] inputA,
            float[] inputB) throws OrtException {
        log.info("Prepare ONNX tensors with shape [1,1,128,128]");
        OnnxTensor tensorA = this.createTensor(inputA);
        OnnxTensor tensorB = this.createTensor(inputB);

        log.info("Run the onnx model");
        Map<String, OnnxTensor> inputs = Map.of(
                "signature_A", tensorA,
                "signature_B", tensorB
        );

        log.info("Extract embeddings");
        try (OrtSession.Result results = session.run(inputs)) {
            float[][] embA = (float[][]) results.get(0).getValue();
            float[][] embB = (float[][]) results.get(1).getValue();
            return new float[][] { embA[0], embB[0] };
        }
    }

    private OnnxTensor createTensor(float[] input) throws OrtException {
        log.info("Create onnxTensor");
        return OnnxTensor.createTensor(
                env, FloatBuffer.wrap(input), new long[]{1, 1, 128, 128});
    }

}
