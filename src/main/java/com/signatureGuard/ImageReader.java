package com.signatureGuard;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;

public class ImageReader {


    public static void main(String[] args) {
        BufferedImage signatureA = loadImage("firma1.png");
        BufferedImage signatureB = loadImage("firma2.png");
        if (signatureA != null && signatureB != null) {
            signatureA = convertToGrayscale(signatureA);
            signatureB = convertToGrayscale(signatureB);
        }
        double signatureSimilarity = compareSignatures(signatureA, signatureB);
    }

    private static double compareSignatures(BufferedImage signatureA, BufferedImage signatureB) {
        return 0;
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
