package com.siameseNetwork;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.utilities.AppLogger;
import java.nio.FloatBuffer;
import java.util.Map;
import java.util.logging.Logger;


public class OnnxModelVerifier {

    private final OrtEnvironment env ;
    private final OrtSession session;
    private static final Logger log =
            AppLogger.getLogger(SiameseSigNetCompare.class);

    public OnnxModelVerifier(
            String onnxPath) throws OrtException {
        log.info("Create the runtime environment");
        env = OrtEnvironment.getEnvironment();

        log.info("Load the ONNX model into a session");
        session = env.createSession(
                onnxPath, new OrtSession.SessionOptions());
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
