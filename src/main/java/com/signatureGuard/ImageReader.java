package com.signatureGuard;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBufferByte;
import java.io.File;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;


public class ImageReader {

    static {
        System.out.println("Core version: " + Core.NATIVE_LIBRARY_NAME);
        System.out.println(System.getProperty("java.library.path"));
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }


    public static void main(String[] args) {
        System.out.println("OpenCV version: " + Core.VERSION);
        String signatureAPath = "firmas/firma1.png";
        String signatureBPath = "firmas/firma2.png";
        BufferedImage signatureA = loadImage(signatureAPath);
        BufferedImage signatureB = loadImage(signatureBPath);
        if (signatureA != null && signatureB != null) {
//            signatureA = convertToGrayscale(signatureA);
//            signatureB = convertToGrayscale(signatureB);
            double signatureSimilarity = compareSignatures(signatureAPath, signatureBPath);
            System.out.println("The similarity between the signatures is: " + (signatureSimilarity * 100) + "%");
        } else {
            System.out.println("Error loading images.");
        }
    }

    private static double compareSignatures(String signatureA, String signatureB) {
        Mat img1 = Imgcodecs.imread(signatureA, Imgproc.COLOR_BGR2GRAY);
        Mat img2 = Imgcodecs.imread(signatureB, Imgproc.COLOR_BGR2GRAY);

        Imgproc.GaussianBlur(img1, img1, new Size(5, 5), 0);
        Imgproc.GaussianBlur(img2, img2, new Size(5, 5), 0);

        Imgproc.Canny(img1, img1, 50, 150);
        Imgproc.Canny(img2, img2, 50, 150);

        Mat diff = new Mat();
        Core.absdiff(img1, img2, diff);

        Scalar sumDiff = Core.sumElems(diff);
        double difference = sumDiff.val[0] / (img1.rows() * img1.cols());

        return 1 - (difference / 255.0);
    }

    private static Mat bufferedImageToMat(BufferedImage img) {
        byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC1);
        mat.put(0, 0, pixels);
        return mat;
    }

    public static BufferedImage loadImage(String path) {
        try {
            System.out.println("Reading image");
            return ImageIO.read(new File(path));
        } catch(Exception e){
            return null;
        }
    }

    public static BufferedImage convertToGrayscale(BufferedImage img) {
        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        return op.filter(img, null);
    }
}
