package com.signatureGuardProcessor;

import ai.onnxruntime.OrtException;
import com.siameseNetwork.OnnxConfig;
import com.siameseNetwork.SiameseSigNetCompare;
import com.utilities.AppLogger;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;

public class ResizeImage {

    private static final Logger log =
            AppLogger.getLogger(SiameseSigNetCompare.class);
    private final String inputHeight;
    private final String inputWidth;

    public ResizeImage() throws OrtException {
        OnnxConfig cfg = new OnnxConfig();
        this.inputHeight = cfg.getInputHeight();
        this.inputWidth = cfg.getInputWidth();
    }

    public Mat resizeImage(
            Mat imageToBeResized
    ){
        log.info("Resizing images");
        Mat resizedImage = new Mat();
        opencv_imgproc.resize(
                imageToBeResized,
                resizedImage,
                new Size(
                    parseInt(this.inputHeight),
                    parseInt(this.inputWidth)
                ));
        return resizedImage;
    }
}
