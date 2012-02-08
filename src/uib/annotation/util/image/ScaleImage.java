/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.annotation.util.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

/**
 *This class is modifyed from the work of Romain Guy, and might
 * be used instead of the scaleimage method in Lire
 * 
 * 
 * @author Olav
 */
public class ScaleImage {

    public BufferedImage getFasterScaledInstance(BufferedImage img,
            int targetWidth, int targetHeight, Object hint,
            boolean progressiveBilinear) {

        int type = (img.getTransparency() == Transparency.OPAQUE)
                ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage ret = (BufferedImage) img;
        BufferedImage scratchImage = null;

        Graphics2D g2 = null;
        int w, h;
        int prevW = ret.getWidth();
        int prevH = ret.getHeight();

        if (progressiveBilinear) {
            // Use multistep technique: start with original size,
            // then scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (progressiveBilinear && w > targetWidth) {
                w /= 2;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }
            if (progressiveBilinear && h > targetHeight) {
                h /= 2;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }
            if (scratchImage == null) {
                // Use a single scratch buffer for all iterations
                // and then copy to the final, correctly sized image
                // before returning
                scratchImage = new BufferedImage(w, h, type);
                g2 = scratchImage.createGraphics();
            }
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    hint);
            g2.drawImage(ret, 0, 0, w, h, 0, 0, prevW, prevH, null);
            prevW = w;
            prevH = h;
            ret = scratchImage;
        } while (w != targetWidth || h != targetHeight);
        if (g2 != null) {
            g2.dispose();
        }
        // If we used a scratch buffer that is larger than our
        // target size, create an image of the right size and copy
        // the results into it
        if (targetWidth != ret.getWidth()
                || targetHeight != ret.getHeight()) {
            scratchImage = new BufferedImage(targetWidth,
                    targetHeight, type);
            g2 = scratchImage.createGraphics();
            g2.drawImage(ret, 0, 0, null);
            g2.dispose();
            ret = scratchImage;
        }
        return ret;
    }
}
