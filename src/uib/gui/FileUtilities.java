/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.gui;

import java.awt.Cursor;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import uib.annotation.panels.GraphicalAnnotation;
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
    private SemanticAnnotation textPanel; // Not instantiated yet..
    GraphicalAnnotation graphicalAnnotation;
    private Document d;
    private final static String NEW_LINE = System.getProperty("line.separator");

    ;

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
        if (img != null) {
            parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            status.setText("Please wait while loading imageinformation " + img.toString());

            try {
                d = parent.reader.document(parent.currentDocument);
                String file = d.getField(net.semanticmetadata.lire.DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();

                if (!file.startsWith("http:")) {
                    extractInformation();
                    status.setText("Finished"); 
                } else {

                    extractInformation();
                }
            } catch (CorruptIndexException ex) {
                Logger.getLogger(FileUtilities.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FileUtilities.class.getName()).log(Level.SEVERE, null, ex);
            }  finally {
                parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
            status.setText("Finished");
        } else {
            parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            status.setText("No files in index, please open or create an index");
        }
    }

    public synchronized void resetTextFields() {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                parent.textAreaAnnotateFreetext.setText("");
                Iterator<JTextField> iterator = parent.annotationFields.iterator();
                while (iterator.hasNext()) {
                    JTextField currentItem = iterator.next();
                    currentItem.setText("");
                }

            }
        };
        SwingUtilities.invokeLater(runnable);
    }
    
    /*
     *Since the image descriptions are inserted into jTextFields, this action 
     * need to take place in the Event Dispatch Thread
     */     
    synchronized void extractInformation() {
        Runnable runnable = new Runnable() {
            private String filePath;

            @Override
            public void run() {               
                if (parent.checkboxShowDescriptions.isSelected()) {
                    try {
                        filePath = img.getCanonicalPath();

                        String mp7name = filePath.substring(0, filePath.lastIndexOf('.')) + ".mp7.xml";
                        String text = "";

                        File mp7file = new File(mp7name);
                        if (mp7file.exists()) {
                            debug("Reading existing MPEG-7 information " + mp7name);
                            status.setText("Reading existing MPEG-7 information " + mp7name);
                            // ----------------------------
                            // Reading from the CIDOC/CRM file
                            // ----------------------------
                            SAXBuilder builder = new SAXBuilder();
                            Element root = builder.build(mp7file).getRootElement();

                            // textual description of the File ..
                            text = getSingleValue(root, "//Image/TextAnnotation/FreeTextAnnotation");
                            if (text != null) {
                                parent.textAreaAnnotateFreetext.setText(text.replaceAll("\\s+", " "));

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
                            text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Height/Name");
                            if (text != null) {
                                parent.textfieldAnnotateHeight.setText(text);
                            }
                            text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Date/Name");
                            if (text != null) {
                                parent.textfieldAnnotateDate.setText(text);
                            }
                            text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Actor/Name");
                            if (text != null) {
                                parent.textfieldAnnotateActor.setText(text);
                            }
                            text = getSingleValue(root, "//Image/TextAnnotation/StructuredAnnotation/Width/Name");
                            if (text != null) {
                                parent.textfieldAnnotateWidth.setText(text);
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
                                parent.graphicalAnnotation1.setSemantics((Element) ((Element) results.get(0)).detach());
                            }

                        } else {
                            parent.status.setText("No Mpeg-7 description exits yet");
                            resetTextFields();
                        }
                    } catch (IOException e) {
                        debug("IOException while searching and reading existing MPEG-7 description " + e.toString());
                    } catch (JDOMException e) {
                        debug("Exception parsing existing MPEG-7 description" + e.toString());
                    }

                    AardvarkGui.setDirty(false);
                    parent.setTitle(AardvarkGui.TITLE_BAR + ": " + filePath);

                } else {
                    resetTextFields();
                }
            }
        };
        SwingUtilities.invokeLater(runnable);
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

    private String getWrappedText() {
        int len = parent.textAreaAnnotateFreetext.getDocument().getLength();
        int offset = 0;
        StringBuilder buf = new StringBuilder((int) (len * 1.10));

        while (offset < len) {
            int end = 0;
            try {
                end = Utilities.getRowEnd(parent.textAreaAnnotateFreetext, offset);
                if (end < 0) {
                    break;
                }
                end = Math.min(end + 1, len);
                String line;
                line = parent.textAreaAnnotateFreetext.getDocument().getText(offset, end - offset);
                line =
                        line.replaceAll(
                        NEW_LINE,
                        " " + NEW_LINE);

                buf.append(line);
                if (!line.endsWith(NEW_LINE)) {
                    buf.append(NEW_LINE);
                }
            } catch (BadLocationException e1) {
                e1.printStackTrace();
                break;
            }
            offset = end;

        }
        return buf.toString();
    }
}
