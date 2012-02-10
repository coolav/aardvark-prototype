/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.gui;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.index.CorruptIndexException;
import uib.annotation.panels.*;
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
public class FileUtilities extends Thread {

    public static DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
    private JLabel status;
    private AardvarkGui parent;
    private File img;
    private SemanticAnnotation textPanel;
    GraphicalAnnotation graphicalAnnotation;
    //private int currentDocument;
    private Document d;

    public FileUtilities(JLabel status, AardvarkGui parent, File img,
            SemanticAnnotation textPanel, GraphicalAnnotation graphicalAnnotation) {
        this.status = status;
        this.parent = parent;
        this.img = img;
        this.textPanel = textPanel;
        this.graphicalAnnotation = graphicalAnnotation;
    }
    
    @Override
    public void run() {
        //parent.setEnabled(false);
        if(img != null){
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        status.setText("Please wait while loading imageinformation " + img.toString());

        try {
            d = parent.reader.document(parent.currentDocument);
             String file = d.getField(net.semanticmetadata.lire.DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
             
             if (!file.startsWith("http:")) {
                
                extractInformation(file);
                //textPanel.resetTextFields();
                status.setText("Finished");
            } else {
                
                extractInformation(file);
                textPanel.resetTextFields();
            }
        } catch (CorruptIndexException ex) {
            Logger.getLogger(FileUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
        parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        
        // clearing previous information:
        //textPanel.resetTextFields();
        status.setText("Finished");
        }
        else{
             parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
             status.setText("No files in index, please open or create an index");
        }
    }

    private void extractInformation(String filePath) {

        try {
            filePath = img.getCanonicalPath();
            
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
                    parent.textAreaAnnotateFreetext.setText(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Name/Name");
                if (text != null) {
                    parent.textfieldAnnotateName.setText(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Creator/Name");
                if (text != null) {
                    parent.textfieldAnnotateCreator.setText(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Period/Name");
                if (text != null) {
                    parent.textfieldAnnotatePeriod.setText(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Technique/Name");
                if (text != null) {
                    parent.textfieldAnnotateTechnique.setText(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Materials/Name");
                if (text != null) {
                     parent.textfieldAnnotateMaterials.setText(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Actor/Name");
                if (text != null) {
                    parent.textfieldAnnotateActor.setText(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Location/Name");
                if (text != null) {
                    parent.textfieldAnnotateLocation.setText(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Activity/Name");
                if (text != null) {
                    parent.textfieldAnnotateActivity.setText(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Theme/Name");
                if (text != null) {
                    parent.textfieldAnnotateTheme.setText(text);
                }
                text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Concept/Name");
                if (text != null) {
                    parent.textFieldAnnotateConcept.setText(text);
                }
                // semantics ...
                java.util.List results = AnnotationToolkit.xpathQuery(root, "//Image/Semantic", null);
                
                if (results.size() > 0) {
                    debug("setting semantic description ...");
                    parent.graphicalAnnotation1.setSemantics((Element)((Element)results.get(0)).detach());
                }

            }else{
                    parent.status.setText("No Mpeg-7 description exits yet");
        }
        } catch (IOException e) {
            debug("IOException while searching and reading existing MPEG-7 description " + e.toString());
        } catch (JDOMException e) {
            debug("Exception parsing existing MPEG-7 description" + e.toString());
        }
        
        AardvarkGui.setDirty(false);
        parent.setTitle(AardvarkGui.TITLE_BAR + ": " + filePath);

    }

    private void debug(String message) {
        if (parent.DEBUG) {
            System.out.println("uib.gui.FileUtilities] " + message);
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
    
    public void shutDown() {
        this.interrupt();
    }

}



