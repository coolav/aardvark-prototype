/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import org.apache.lucene.document.Document;

/**
 *
 * @author Olav
 */
public class ImagePanel extends JPanel {
    private BufferedImage image;
    private AardvarkGui parent;

    public ImagePanel() {
        this.image=null;
    }

    public BufferedImage getImage() {
        return image;
    }
    public BufferedImage getImageParent(int docId) {
        try {
            Document d = parent.reader.document(docId);

            image = null;
            String file = d.getField(net.semanticmetadata.lire.DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
            if (!file.startsWith("http:")) {
                image = ImageIO.read(new java.io.FileInputStream(file));
            } else {
                image = ImageIO.read(new URL(file));
            }
            
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(parent, "Error loading image:\n" + e.toString(),
                    "An error occurred", JOptionPane.ERROR_MESSAGE);
            System.err.println(e.toString());
        }
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        int w = this.getWidth()-10;
        int h = this.getHeight()-10;
        int iw = image.getWidth();
        int ih = image.getHeight();
        if (iw > w || ih > h) { // we have to scale the image
            int tmpw, tmph;
            // trying to go by width:
            tmpw = w;
            tmph = (int) ((float)ih*((float)w/(float)iw));
            if (tmph>h) { // doesn't work, so going by height
                tmph = h;
                tmpw = (int) ((float) iw * ((float)h/(float)ih));
            }
            ih = tmph;
            iw = tmpw;
        } 
        // just paint ...
        g2.drawImage(image, 5+(w-iw)/2, 5, iw, ih, null);
    }
}
    
