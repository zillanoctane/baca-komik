package com.myproject.utils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

public class Utils {
    
    static {
        ensureImageIOInitialized();
    }
    
    private static void ensureImageIOInitialized() {
        try {
            ImageIO.scanForPlugins();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Image bytesToImage(byte[] imageData) {
        if (imageData == null || imageData.length == 0) return null;
        
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData)) {
            BufferedImage img = ImageIO.read(bais);
            if (img != null) return img;
        } catch (IOException e) {
        }
        
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
             ImageInputStream iis = ImageIO.createImageInputStream(bais)) {
            
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                reader.setInput(iis);
                BufferedImage img = reader.read(0);
                reader.dispose();
                return img;
            }
        } catch (Exception e) {
        }
        
        try {
            ImageIcon icon = new ImageIcon(imageData);
            if (icon.getIconWidth() > 0) return icon.getImage();
        } catch (Exception e) {
        }
        
        return null;
    }
    
    public static Image scaleImage(Image image, int maxWidth, int maxHeight) {
        if (image == null) return null;

        int w = image.getWidth(null);
        int h = image.getHeight(null);

        if (w <= 0 || h <= 0) {
            ImageIcon icon = new ImageIcon(image);
            w = icon.getIconWidth();
            h = icon.getIconHeight();
        }

        if (w <= 0 || h <= 0) {
            MediaTracker tracker = new MediaTracker(new Container());
            tracker.addImage(image, 0);
            try { tracker.waitForID(0, 1000); } catch (InterruptedException ignored) {}
            w = image.getWidth(null);
            h = image.getHeight(null);
        }

        if (w <= 0 || h <= 0) return image;
        if (w <= maxWidth && h <= maxHeight) return image;

        double scale = Math.min((double) maxWidth / w, (double) maxHeight / h);
        return image.getScaledInstance((int) (w * scale), (int) (h * scale), Image.SCALE_SMOOTH);
    }

    public static Image scaleFit(Image image, int maxWidth, int maxHeight) {
        if (image == null) return null;

        int w = image.getWidth(null);
        int h = image.getHeight(null);

        if (w <= 0 || h <= 0) {
            ImageIcon icon = new ImageIcon(image);
            w = icon.getIconWidth();
            h = icon.getIconHeight();
        }

        if (w <= 0 || h <= 0) return image;

        double ratio = Math.min((double) maxWidth / w, (double) maxHeight / h);
        if (ratio >= 1.0) return image;

        return image.getScaledInstance((int) (w * ratio), (int) (h * ratio), Image.SCALE_SMOOTH);
    }
}