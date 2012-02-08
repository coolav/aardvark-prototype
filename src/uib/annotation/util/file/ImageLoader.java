/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.annotation.util.file;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.index.CorruptIndexException;
import uib.annotation.panels.*;
import uib.gui.AardvarkGui;
import uib.gui.SemanticAnnotation;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import org.apache.lucene.document.Document;
import uib.annotation.util.AnnotationToolkit;

/**
 *
 * @author Olav
 */
public class ImageLoader extends Thread {

    public static DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
    private JLabel status;
    private AardvarkGui parent;
    private File img;
    private SemanticAnnotation textPanel;
    GraphicalAnnotation graphicalAnnotation;
    //private int currentDocument;
    private Document d;

    public ImageLoader(JLabel status, AardvarkGui parent, File img,
            SemanticAnnotation textPanel, GraphicalAnnotation graphicalAnnotation) {
        this.status = status;
        this.parent = parent;
        this.img = img;
        this.textPanel = textPanel;
        this.graphicalAnnotation = graphicalAnnotation;
    }

    @Override
    public void run() {
        parent.setEnabled(false);
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        status.setText("Please wait while loading image " + img.toString());

        try {
            d = parent.reader.document(parent.currentDocument);
        } catch (CorruptIndexException ex) {
            Logger.getLogger(ImageLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ImageLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        String file = d.getField(net.semanticmetadata.lire.DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();

        try {
            BufferedImage image;
            if (!file.startsWith("http:")) {
                image = ImageIO.read(new java.io.FileInputStream(img));
                extractInformation(image);
            } else {
                image = ImageIO.read(new URL(file));
                extractInformation(image);
            }
        } catch (IOException ex) {
            Logger.getLogger(ImageLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        // clearing previous information:
        //textPanel.resetTextFields();
        //status.setText("Finished");
    }

    private void extractInformation(BufferedImage image) {

        try {
            String filePath = img.getCanonicalPath();
            
            String mp7name = filePath.substring(0, filePath.lastIndexOf('.')) + ".mp7.xml";
            
            File mp7file = new File(mp7name);
            if (mp7file.exists()) {
                debug("Reading existing MPEG-7 information " + mp7name);
                status.setText("Reading existing MPEG-7 information " + mp7name);
                // ----------------------------
                // Reading from the MPEG-7 File
                // ----------------------------
                SAXBuilder builder = new SAXBuilder();
                Element root = builder.build(mp7file).getRootElement();

                // textual description of the File ..
                String text = getSingleValue(root, "//Image/TextAnnotation/FreeTextAnnotation");
                if (text != null) {
                    textPanel.setFreeText(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Name/Name");
                if (text != null) {
                    textPanel.setName(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Period/Name");
                if (text != null) {
                    textPanel.setPeriod(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Technique/Name");
                if (text != null) {
                    textPanel.setTechnique(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Materials/Name");
                if (text != null) {
                    textPanel.setMaterials(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Actor/Name");
                if (text != null) {
                    textPanel.setActor(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Location/Name");
                if (text != null) {
                    textPanel.setLocation(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Activity/Name");
                if (text != null) {
                    textPanel.setActivity(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Theme/Name");
                if (text != null) {
                    textPanel.setTheme(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Concept/Name");
                if (text != null) {
                    textPanel.setConcept(text);
                }
                // semantics ...
                //java.util.List results = AnnotationToolkit.xpathQuery(root, "//Image/Semantic", null);
                //status.setText(results.toString());
                //if (results.size() > 0) {
                //    debug("setting semantic description ...");
                //    graphicalAnnotation.setSemantics((Element)((Element)results.get(0)).detach());
                //}

            }
        } catch (IOException e) {
            debug("IOException while searching and reading existing MPEG-7 description " + e.toString());
        } catch (JDOMException e) {
            debug("Exception parsing existing MPEG-7 description" + e.toString());
        }

    }

    private void debug(String message) {
        if (parent.DEBUG) {
            System.out.println("[at.lux.fotoannotation.ImageLoader] " + message);
        }
    }

    private String getSingleValue(Element root, String xpath) {
        String text = null;
        java.util.List results = AnnotationToolkit.xpathQuery(root, xpath, null);
        if (results.size() > 0) {
            text = ((Element) results.get(0)).getTextTrim();
        }
        return text;
    }
}
