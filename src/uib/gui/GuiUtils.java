/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.gui;

import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.ExifReader;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import uib.annotation.model.Mpeg7ImageDescription;
import uib.annotation.panels.GraphicalAnnotation;
import uib.annotation.util.AnnotationToolkit;
import uib.annotation.util.image.IconCache;
import uib.gui.util.ImageResize;

/**
 *
 * @author Olav
 */
public class GuiUtils {

    private AardvarkGui parent;
    private BufferedImage image;
    private BufferedImage defaultImage;
    ImageSearchHits hits = null;
    public static final boolean DEBUG = false;
    private SemanticAnnotation semanticAnnotation;
    private GraphicalAnnotation graphicalAnnotation;
    private File currentFile;

    public GuiUtils(AardvarkGui parent) {
        this.parent = parent;

    }

    public void initReader(JSpinner spinnerMax, JSpinner spinnerCurrent, JLabel label) {
        try {
            if (parent.reader == null && parent.checkboxRamIndex.isSelected() == false) {

                parent.reader = IndexReader.open(FSDirectory.open(new File(parent.textfieldIndexDirectory.getText())));
            }

            if (parent.checkboxRamIndex.isSelected() == false) {
                parent.reader = IndexReader.open(new RAMDirectory(FSDirectory.open(new File(parent.textfieldIndexDirectory.getText()))));
            } else {
                parent.reader.close();
                parent.reader = IndexReader.open(FSDirectory.open(new File(parent.textfieldIndexDirectory.getText())));
            }
            spinnerMax.setValue(parent.reader.maxDoc());
            if (parent.reader != null) {
                setDocumentImageIcon(((Integer) spinnerCurrent.getValue()).intValue(), label);
                //BufferedImage img = parent.browseimagePanel.getImageParent(((Integer) spinnerCurrent.getValue()).intValue());
                //parent.browseimagePanel.setImage(img);

            } else {
                setDefaultImage(label);
            }
            setExifFields(((Integer) spinnerCurrent.getValue()).intValue());
            setCurrentFile(getCurrentDocumentFile(((Integer) spinnerCurrent.getValue()).intValue()));

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent, "Could not open index. "
                    + "Please ensure that an index has been created.", "Error opening index", JOptionPane.ERROR_MESSAGE);
        }
    }

    public File getCurrentDocumentFile(int docId) {

        if (docId < 0) {
            return null;
        }
        if (docId > parent.reader.maxDoc()) {
            return null;
        }
        try {
            Document d = parent.reader.document(docId);
            String file = d.getField(net.semanticmetadata.lire.DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
            currentFile = new File(file);

        } catch (Exception e) {

            JOptionPane.showConfirmDialog(parent, "Error loading image:\n" + e.toString(),
                    "An error occurred", JOptionPane.ERROR_MESSAGE);
            System.err.println(e.toString());
        }
        return currentFile;

    }

    public BufferedImage getImage(int docId) {
        try {
            Document d = parent.reader.document(docId);
            defaultImage = ImageIO.read(new java.io.FileInputStream(""));
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

    public void setDocumentImageIcon(int docId, JLabel label) {
        if (docId < 0) {

            return;
        }
        if (docId > parent.reader.maxDoc()) {

            return;
        }
        try {
            Document d = parent.reader.document(docId);
            ImageIcon icon = null;
            BufferedImage img, image = null;
            String file = d.getField(net.semanticmetadata.lire.DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
            if (!file.startsWith("http:")) {
                img = ImageIO.read(new java.io.FileInputStream(file));
            } else {
                img = ImageIO.read(new URL(file));
            }
            if (img != null) {
                icon = new ImageIcon(ImageResize.scale(img, label.getWidth(), label.getHeight()));
                //icon = new ImageIcon(ImageUtils.scaleImage(img, label.getWidth(), label.getHeight()));
            } else {
                icon = IconCache.getInstance().getDefaultBrowserIcon();
            }
            label.setIcon(icon);
            parent.browseimagePanel.setImage(img);
            parent.browseimagePanel.repaint();

        } catch (Exception e) {
            JOptionPane.showConfirmDialog(parent, "Error loading image:\n" + e.toString(),
                    "An error occurred", JOptionPane.ERROR_MESSAGE);
            System.err.println(e.toString());
        }
    }

    public void setDefaultImage(JLabel label) {
        ImageIcon icon = null;
        icon = IconCache.getInstance().getDefaultBrowserIcon();

    }

    public void writeQrels(String fileName) {

        try {
            File qrelsFile = new File(fileName);
            Writer output = new BufferedWriter(new FileWriter(qrelsFile));
            output.write("");

            for (int i = 0; i < parent.tableModel.hits.length(); i++) {
                String text = parent.tableModel.hits.doc(i).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();

                if (parent.tableModel.hits.doc(i).getField("FlickrURL") != null) {
                    text = parent.tableModel.hits.doc(i).getField("FlickrTitle").stringValue() + " - "
                            + parent.tableModel.hits.doc(i).getField("FlickrURL").stringValue();
                }
                output.write("0 \t 0 \t " + text
                        + "     \t " + parent.tableModel.getIsRelevant(i) + "\n");

            }

            output.close();

        } catch (IOException ex) {
            Logger.getLogger(AardvarkGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeLuceneAnnotation(int docId) throws IOException {

        Document newDoc = new Document();
        IndexWriter writer = null;

        newDoc.add(new Field("Name",
                parent.textfieldAnnotateName.getText(),
                Field.Store.YES,
                Field.Index.ANALYZED));
        newDoc.add(new Field("Creator",
                parent.textfieldAnnotateCreator.getText(),
                Field.Store.YES,
                Field.Index.ANALYZED));
        newDoc.add(new Field("Period",
                parent.textfieldAnnotatePeriod.getText(),
                Field.Store.YES,
                Field.Index.ANALYZED));
        newDoc.add(new Field("Technique",
                parent.textfieldAnnotateTechnique.getText(),
                Field.Store.YES,
                Field.Index.ANALYZED));
        newDoc.add(new Field("Material",
                parent.textfieldAnnotateMaterials.getText(),
                Field.Store.YES,
                Field.Index.ANALYZED));
        newDoc.add(new Field("Height",
                parent.textfieldAnnotateHeight.getText(),
                Field.Store.YES,
                Field.Index.ANALYZED));
        newDoc.add(new Field("Date",
                parent.textfieldAnnotateDate.getText(),
                Field.Store.YES,
                Field.Index.ANALYZED));
        newDoc.add(new Field("Actor",
                parent.textfieldAnnotateActor.getText(),
                Field.Store.YES,
                Field.Index.ANALYZED));
        newDoc.add(new Field("Width",
                parent.textfieldAnnotateWidth.getText(),
                Field.Store.YES,
                Field.Index.ANALYZED));
        newDoc.add(new Field("Location",
                parent.textfieldAnnotateLocation.getText(),
                Field.Store.YES,
                Field.Index.ANALYZED));
        newDoc.add(new Field("Activity",
                parent.textfieldAnnotateActivity.getText(),
                Field.Store.YES,
                Field.Index.ANALYZED));
        newDoc.add(new Field("Theme",
                parent.textfieldAnnotateTheme.getText(),
                Field.Store.YES,
                Field.Index.ANALYZED));
        newDoc.add(new Field("Concept",
                parent.textFieldAnnotateConcept.getText(),
                Field.Store.YES,
                Field.Index.ANALYZED));
        newDoc.add(new Field("FreeText",
                parent.textAreaAnnotateFreetext.getText(),
                Field.Store.YES,
                Field.Index.ANALYZED));

        try {
            writer = new IndexWriter(FSDirectory.open(new File(parent.textfieldIndexDirectory.getText())),
                    new SimpleAnalyzer(), false, IndexWriter.MaxFieldLength.UNLIMITED);
            Document doc = parent.reader.document(docId);

            String docID = Integer.toString(docId);
            String file = doc.getField(net.semanticmetadata.lire.DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
            //Term term = new Term("descriptorImageIdentifier", file);
            Term term = new Term("docID", docID);
            updateLuceneDocument(writer, newDoc, docId, term);
        } catch (IOException e) {
        }
        writer.commit();
        writer.close();
    }

    public void updateLuceneDocument(IndexWriter writer, Document updateDoc, int docID, Term term) throws IOException {
        Document doc = parent.reader.document(docID);

        List<Fieldable> oldField = doc.getFields();
        for (Fieldable field : oldField) {
            String name = field.name();
            String currentValue = doc.get(name);
            updateDoc.add(new Field(name, currentValue, Field.Store.YES, Field.Index.ANALYZED));
        }

        writer.updateDocument(term, updateDoc);
    }

    public void resetTextFields() {
        Iterator<JTextField> iterator = parent.annotationFields.iterator();
        while (iterator.hasNext()) {
            JTextField currentItem = iterator.next();
            currentItem.setText("");
        }

    }

    public void setExifFields(int docId) throws CorruptIndexException, IOException {

        if (docId < 0) {
            return;
        }
        if (docId > parent.reader.maxDoc()) {
            return;
        }
        try {

            Document d = parent.reader.document(parent.currentDocument);
            String file = d.getField(net.semanticmetadata.lire.DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
            String URL = d.getField("FlickrURL").stringValue();
            //System.out.println(file + " " + URL);
            if (URL == null) {
                //(!file.startsWith("http:")) {
                parent.textfieldAnnotateDate.setText(getDate(docId));
                parent.textfieldAnnotateWidth.setText(getExifInt(docId, ExifDirectory.TAG_EXIF_IMAGE_WIDTH));
                parent.textfieldAnnotateHeight.setText(getExifInt(docId, ExifDirectory.TAG_EXIF_IMAGE_HEIGHT));
            } else {
                parent.textfieldAnnotateName.setText(getFlickrData(docId, "FlickrTitle"));
                parent.textfieldAnnotateDate.setText("");
                parent.textfieldAnnotateWidth.setText("");
                parent.textfieldAnnotateHeight.setText("");
            }
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(parent, "Error loading image information:\n" + e.toString(),
                    "An error occurred", JOptionPane.ERROR_MESSAGE);
            System.err.println(e.toString());
        }

    }

    private String getDate(int docId) {

        Date imageDate = null;
        try {
            Document d = parent.reader.document(docId);
            String fileIdentifier = d.getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
            Metadata metaData = new ExifReader(new FileInputStream(fileIdentifier)).extract();
            if (metaData.containsDirectory(ExifDirectory.class)) {
                ExifDirectory exifDirectory = (ExifDirectory) metaData.getDirectory(ExifDirectory.class);
                if (exifDirectory.containsTag(ExifDirectory.TAG_DATETIME_ORIGINAL)) {
                    imageDate = exifDirectory.getDate(ExifDirectory.TAG_DATETIME_ORIGINAL);

                }
            } else if (!metaData.containsDirectory(ExifDirectory.class)) {
                imageDate = new Date();
            }
        } catch (Exception e) {
            Logger.getLogger("global").log(Level.SEVERE, "Could not extract metadata", e);
        }
        return imageDate.toString();

    }

    private String getExifInt(int docId, int tag) {
        int value = 0;
        try {
            Document d = parent.reader.document(docId);
            String fileIdentifier = d.getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
            Metadata metaData = new ExifReader(new FileInputStream(fileIdentifier)).extract();
            if (metaData.containsDirectory(ExifDirectory.class)) {
                ExifDirectory exifDirectory = (ExifDirectory) metaData.getDirectory(ExifDirectory.class);
                if (exifDirectory.containsTag(tag)) {
                    value = exifDirectory.getInt(tag);

                }
            } else if (!metaData.containsDirectory(ExifDirectory.class)) {
                value = 0;
            }
        } catch (Exception e) {
            Logger.getLogger("global").log(Level.SEVERE, "Could not extract metadata", e);
        }
        return Integer.toString(value);

    }

    private String getFlickrData(int docId, String field) {
        String title = null;

        try {
            Document d = parent.reader.document(docId);
            title = d.getField(field).stringValue();
        } catch (CorruptIndexException ex) {
        } catch (IOException e) {
        }

        return title;
    }

    public void setCurrentFile(File f) throws IOException {
        boolean loadIt = true;
        if (AardvarkGui.DIRTY) {
            loadIt = askIfSave();
        }
        if (loadIt) {
            FileUtilities t = new FileUtilities(parent.status, parent, f, semanticAnnotation,
                    graphicalAnnotation);

            t.start();
            parent.currentFile = f;
            t.shutDown();

        }
    }

    public void loadCurrentFile(File f) throws IOException {
        FileUtilities t = new FileUtilities(parent.status, parent, f, semanticAnnotation,
                graphicalAnnotation);
        t.start();

        parent.currentFile = f;
        t.shutDown();
    }

    public void saveFile() throws IOException {
        if (parent.currentFile != null) {
            try {
                XMLOutputter op = new XMLOutputter(Format.getPrettyFormat());
                String imgName = parent.currentFile.getCanonicalPath();
                String fname = imgName.substring(0, imgName.lastIndexOf("."));
                fname = fname + ".mp7.xml";
                File saveFile = new File(fname);
                // check if file is arleady in existence and ask if we should overwrite:
//                boolean write = false;
//                if (saveFile.exists()) {
//                    if (JOptionPane.showConfirmDialog(this,
//                            "Overwrite existing file?", "Overwrite?",
//                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//                        write = true;
//                    }
//                } else {
//                    write = true;
//                }
                if (true) {
                    FileOutputStream fos = new FileOutputStream(saveFile);
                    OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
                    op.output(createDocument(), osw);
                    osw.close();
                    fos.close();
                    debug("File written to " + saveFile.toString());
                    parent.status.setText("File written to " + saveFile.toString());
                    setDirty(false);
                    if (parent.getTitle().indexOf('*') > -1) {
                        parent.setTitle(parent.getTitle().substring(2));
                    }
                }
            } catch (IOException e) {
                debug("Exception while saving " + e.toString());
            }
        }

    }

    public File getCurrentFile() {
        return parent.currentFile;
    }

    private boolean askIfSave() {
        int returnVal = JOptionPane.showConfirmDialog(parent,
                "Would you like to save your changes? Otherwise your changes will be lost.",
                "Save description?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (returnVal == JOptionPane.YES_OPTION) {
            debug("Save file ...");
            try {
                saveFile();
            } catch (IOException ex) {
                debug("Exception while saving " + ex.toString());
            }
            return true;
        } else if (returnVal == JOptionPane.NO_OPTION) {
            debug("Quit without saving ...");
            return true;
        } else {
            debug("no quitting, no saving :)");
            return false;
        }

    }

    public org.jdom.Document createDocument() {
        Element tmp, semantics;
        tmp = parent.graphicalAnnotation1.getSemanticsDocument().getRootElement();
        semantics = ((Element) tmp.getChild("Description", tmp.getNamespace()).getChild("Semantics",
                tmp.getNamespace()).clone());
        semantics.setName("Semantic");

        File f = parent.getCurrentFile();
        System.out.println(f.toString());

        for (JTextField field : parent.annotationFields) {
            String output = field.getText();
            System.out.println(output);
        }

        Mpeg7ImageDescription m7id = new Mpeg7ImageDescription(AnnotationToolkit.getMpeg7MediaInstance(f),
                semantics);
        // semanticAnnotation.createXML(),

        org.jdom.Document document = m7id.createDocument();
        return document;
    }

    private void debug(String message) {
        if (DEBUG) {
            System.out.println("[uib.gui.AardvarkGui] " + message);
        }
    }

    public static void setDirty(boolean isDirty) {
        AardvarkGui.DIRTY = isDirty;
    }
}
