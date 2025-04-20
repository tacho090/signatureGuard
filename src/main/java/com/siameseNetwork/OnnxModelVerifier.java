package com.siameseNetwork;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

public class OnnxModelVerifier {

    private final OrtEnvironment env;
    private final OrtSession session;

    public OnnxModelVerifier() throws OrtException {
        // Create the runtime environment
        env = OrtEnvironment.getEnvironment();
        // Load your ONNX model into a session
        String onnxModelPath = "src/main/resources/models/siamese.onnx";
        session = env.createSession(onnxModelPath, new OrtSession.SessionOptions());
    }

}
