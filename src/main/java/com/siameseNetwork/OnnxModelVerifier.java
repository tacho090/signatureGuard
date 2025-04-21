package com.siameseNetwork;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import java.nio.FloatBuffer;
import java.util.Map;


public class OnnxModelVerifier {

    private final OrtEnvironment env ;
    private final OrtSession session;


    public OnnxModelVerifier(
            String onnxPath) throws OrtException {
        // Create the runtime environment
        env = OrtEnvironment.getEnvironment();
        // Load your ONNX model into a session
        session = env.createSession(
                onnxPath, new OrtSession.SessionOptions());
    }

    public float[][] getEmbeddings(
            float[] inputA,
            float[] inputB) throws OrtException {
        // Prepare ONNX tensors with shape [1,1,128,128]
        OnnxTensor tensorA = this.createTensor(inputA);
        OnnxTensor tensorB = this.createTensor(inputB);

        // Run the model
        Map<String, OnnxTensor> inputs = Map.of(
                "signature_A", tensorA,
                "signature_B", tensorB
        );

        // Extract output embeddings
        try (OrtSession.Result results = session.run(inputs)) {
            float[][] embA = (float[][]) results.get(0).getValue();
            float[][] embB = (float[][]) results.get(1).getValue();
            return new float[][] { embA[0], embB[0] };
        }
    }

    private OnnxTensor createTensor(float[] input) throws OrtException {
        return OnnxTensor.createTensor(
                env, FloatBuffer.wrap(input), new long[]{1, 1, 128, 128});
    }

}
