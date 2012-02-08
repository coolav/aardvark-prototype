/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.gui.util;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 *
 * @author Olav
 */
public class ImageResize {

    private BufferedImage image;

    public static BufferedImage doResize(int newWidth, int newHeight, BufferedImage source) {
        GraphicsConfiguration gc = getDefaultConfiguration();
        BufferedImage result = gc.createCompatibleImage(newWidth, newHeight, source.getColorModel().getTransparency());
        Graphics2D g2d = null;
        double scaleX = source.getHeight();
        double scaleY = source.getWidth();

        try {
            g2d = result.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            AffineTransform at = AffineTransform.getScaleInstance(scaleX, scaleY);
            g2d.drawRenderedImage(source, at);
        } finally {
            if (g2d != null) {
                g2d.dispose();
            }
        }
        return result;

    }

    public static BufferedImage scale(BufferedImage image, int pWidth, int pHeight) {
        int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage resizedImage = new BufferedImage(pWidth, pHeight, type);
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.setComposite(AlphaComposite.Src);
        int w = pWidth - 10;
        int h = pHeight - 10;
        int iw = image.getWidth();
        int ih = image.getHeight();

        if (iw > w || ih > h) { // we have to scale the image
            int tmpw, tmph;
            // trying to go by width:
            tmpw = w;
            tmph = (int) ((float) ih * ((float) w / (float) iw));
            if (tmph > h) { // doesn't work, so going by height
                tmph = h;
                tmpw = (int) ((float) iw * ((float) h / (float) ih));
            }
            ih = tmph;
            iw = tmpw;
        }

        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.drawImage(image, 5+(w-iw)/2, 5, iw, ih, null);
        //graphics.drawImage(image, 0, 0, iw, ih, null);
        graphics.dispose();

        return resizedImage;
    }

    public static BufferedImage scaleImage(BufferedImage image, int maxSideLength) {
        int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
        assert (maxSideLength > 0);
        double originalWidth = image.getWidth();
        double originalHeight = image.getHeight();
        double scaleFactor = 0.0;
        if (originalWidth > originalHeight) {
            scaleFactor = ((double) maxSideLength / originalWidth);
        } else {
            scaleFactor = ((double) maxSideLength / originalHeight);
        }
        // create smaller image
        BufferedImage img = new BufferedImage((int) (originalWidth * scaleFactor), (int) (originalHeight * scaleFactor), type);
        // fast scale (Java 1.4 & 1.5)
        Graphics2D g = img.createGraphics();
        g.setComposite(AlphaComposite.Src);
        
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION , RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(image, 0, 0, img.getWidth(), img.getHeight(), null);
        return img;
    }

    public static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        return gd.getDefaultConfiguration();
    }
}
