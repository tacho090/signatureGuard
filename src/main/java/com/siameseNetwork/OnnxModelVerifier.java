package com.siameseNetwork;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import java.nio.FloatBuffer;
import java.util.Map;

public class OnnxModelVerifier {

    private static OrtEnvironment env = null;
    private static OrtSession session = null;

    public OnnxModelVerifier() throws OrtException {
        // Create the runtime environment
        env = OrtEnvironment.getEnvironment();
        // Load your ONNX model into a session
        String onnxModelPath = "src/main/resources/models/siamese.onnx";
        session = env.createSession(onnxModelPath, new OrtSession.SessionOptions());
    }

    public static float[][] getEmbeddings(float[] inputA, float[] inputB) throws OrtException {
        // Prepare ONNX tensors with shape [1,1,128,128]
        OnnxTensor tensorA = OnnxTensor.createTensor(env, FloatBuffer.wrap(inputA), new long[]{1, 1, 128, 128});
        OnnxTensor tensorB = OnnxTensor.createTensor(env, FloatBuffer.wrap(inputB), new long[]{1, 1, 128, 128});

        // Run the model: names must match what you exported
        Map<String, OnnxTensor> inputs = Map.of(
                "signature_A", tensorA,
                "signature_B", tensorB
        );
        try (OrtSession.Result results = session.run(inputs)) {
            // Extract output embeddings
            float[][] embA = (float[][]) results.get(0).getValue(); // shape [1][128]
            float[][] embB = (float[][]) results.get(1).getValue();
            return new float[][] { embA[0], embB[0] };
        }
    }

}
