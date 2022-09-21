package com.example.dspaint;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;

public class SwingJank {
    public static BufferedImage fromFXImage(Image img, BufferedImage bimg) {
        PixelReader pr = img.getPixelReader();
        if (pr == null) {
            return null;
        }
        int iw = (int) img.getWidth();
        int ih = (int) img.getHeight();
        PixelFormat fxFormat = pr.getPixelFormat();
        boolean srcPixelsAreOpaque = false;
        switch (fxFormat.getType()) {
            case INT_ARGB_PRE:
            case INT_ARGB:
            case BYTE_BGRA_PRE:
            case BYTE_BGRA:
                // Check fx image opacity only if
                // supplied BufferedImage is without alpha channel
                if (bimg != null &&
                        (bimg.getType() == BufferedImage.TYPE_INT_BGR ||
                                bimg.getType() == BufferedImage.TYPE_INT_RGB)) {
                    srcPixelsAreOpaque = checkFXImageOpaque(pr, iw, ih);
                }
                break;
            case BYTE_RGB:
                srcPixelsAreOpaque = true;
                break;
        }
        int prefBimgType = getBestBufferedImageType(pr.getPixelFormat(), bimg, srcPixelsAreOpaque);
        if (bimg != null) {
            int bw = bimg.getWidth();
            int bh = bimg.getHeight();
            if (bw < iw || bh < ih || bimg.getType() != prefBimgType) {
                bimg = null;
            } else if (iw < bw || ih < bh) {
                Graphics2D g2d = bimg.createGraphics();
                g2d.setComposite(AlphaComposite.Clear);
                g2d.fillRect(0, 0, bw, bh);
                g2d.dispose();
            }
        }
        if (bimg == null) {
            bimg = new BufferedImage(iw, ih, prefBimgType);
        }
        DataBufferInt db = (DataBufferInt)bimg.getRaster().getDataBuffer();
        int data[] = db.getData();
        int offset = bimg.getRaster().getDataBuffer().getOffset();
        int scan =  0;
        SampleModel sm = bimg.getRaster().getSampleModel();
        if (sm instanceof SinglePixelPackedSampleModel) {
            scan = ((SinglePixelPackedSampleModel)sm).getScanlineStride();
        }

        WritablePixelFormat pf = getAssociatedPixelFormat(bimg);
        pr.getPixels(0, 0, iw, ih, pf, data, offset, scan);
        return bimg;
    }
    private static boolean checkFXImageOpaque(PixelReader pr, int iw, int ih) {
        for (int x = 0; x < iw; x++) {
            for (int y = 0; y < ih; y++) {
                Color color = pr.getColor(x,y);
                if (color.getOpacity() != 1.0) {
                    return false;
                }
            }
        }
        return true;
    }

    private static WritablePixelFormat getAssociatedPixelFormat(BufferedImage bimg)
    {
        switch (bimg.getType()) {
            // We lie here for xRGB, but we vetted that the src data was opaque
            // so we can ignore the alpha.  We use ArgbPre instead of Argb
            // just to get a loop that does not have divides in it if the
            // PixelReader happens to not know the data is opaque.
            case BufferedImage.TYPE_INT_RGB:
            case BufferedImage.TYPE_INT_ARGB_PRE:
                return PixelFormat.getIntArgbPreInstance();
            case BufferedImage.TYPE_INT_ARGB:
                return PixelFormat.getIntArgbInstance();
            default:
                // Should not happen...
                throw new InternalError("Failed to validate BufferedImage type");
        }
    }

    static int getBestBufferedImageType(PixelFormat fxFormat, BufferedImage bimg,
                                        boolean isOpaque)
    {
        if (bimg != null) {
            int bimgType = bimg.getType();
            if (bimgType == BufferedImage.TYPE_INT_ARGB ||
                    bimgType == BufferedImage.TYPE_INT_ARGB_PRE ||
                    (isOpaque &&
                            (bimgType == BufferedImage.TYPE_INT_BGR ||
                                    bimgType == BufferedImage.TYPE_INT_RGB)))
            {
                // We will allow the caller to give us a BufferedImage
                // that has an alpha channel, but we might not otherwise
                // construct one ourselves.
                // We will also allow them to choose their own premultiply
                // type which may not match the image.
                // If left to our own devices we might choose a more specific
                // format as indicated by the choices below.
                return bimgType;
            }
        }
        switch (fxFormat.getType()) {
            default:
            case BYTE_BGRA_PRE:
            case INT_ARGB_PRE:
                return BufferedImage.TYPE_INT_ARGB_PRE;
            case BYTE_BGRA:
            case INT_ARGB:
                return BufferedImage.TYPE_INT_ARGB;
            case BYTE_RGB:
                return BufferedImage.TYPE_INT_RGB;
            case BYTE_INDEXED:
                return (fxFormat.isPremultiplied()
                        ? BufferedImage.TYPE_INT_ARGB_PRE
                        : BufferedImage.TYPE_INT_ARGB);
        }
    }
}
