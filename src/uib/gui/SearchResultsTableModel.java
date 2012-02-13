/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.gui;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.utils.ImageUtils;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.ExifDirectory;
import com.thebuzzmedia.imgscalr.Scalr;
import java.awt.RenderingHints;
import java.util.Date;
import uib.gui.util.ImageResize; 


/**
 *
 * @author Olav
 */
public class SearchResultsTableModel extends DefaultTableModel {

    DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
    ImageSearchHits hits = null;
    
    private ArrayList<ImageIcon> icons;
    private ArrayList<Boolean> relevance;

    /**
     * Creates a new instance of SearchResultsTableModel
     */
    public SearchResultsTableModel() {
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "File";
        } else if (column == 1) {
            return "Preview";
        } else if (column == 2) {
            return "Is relevant";
        }
        return "";
    }

    @Override
    public Class getColumnClass(int column) {
        /*
        if (column == 0) {
            return String.class;
        }
        if(column == 2){
            return JComboBox.class;
        }
        else {
            return ImageIcon.class;
        }*/
        return getValueAt(0, column).getClass();
    }

    @Override
    public int getRowCount() {
        if (hits == null) {
            return 0;
        }
        return hits.length();
    }

    /**
     *
     * @param row
     * @param col
     * @return
     * ToDo - format col(0) with default tablecellrenderer ?
     */
    @Override
    public Object getValueAt(int row, int col) {
        if (col == 0) {

            String text = hits.doc(row).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();


            if (hits.doc(row).getField("FlickrURL") != null) {
                text = hits.doc(row).getField("FlickrTitle").stringValue() + " - "
                        + hits.doc(row).getField("FlickrURL").stringValue();
            }
                return "<html>Similarity measure : <b>" + df.format(hits.score(row))
                    + "</b><br>Image Path         : <b>" + text + "</b></br>" +
                    "<br> Date : <b>" + getDate(row) + "</b></html>";

        } else if (col == 1) {
            return icons.get(row);
        }
        else if(col == 2){
            return relevance.get(row);
            }
        
        return null;
    }

    /**
     * @param hits
     * @param progress
     */
    public void setHits(ImageSearchHits hits, JProgressBar progress) {
        this.hits = hits;
        icons = new ArrayList<ImageIcon>(hits.length());
        relevance = new ArrayList<Boolean>(hits.length());
        if (progress != null)progress.setString("Searching finished. Loading images for result list.");
        for (int i = 0; i < hits.length(); i++) {
            ImageIcon icon = null;
            JCheckBox checkBox = null;
            try {
                BufferedImage img = null;
                String fileIdentifier = hits.doc(i).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
                if (!fileIdentifier.startsWith("http:")) {
                    // check isf it is a jpg file ...
                    if (fileIdentifier.toLowerCase().endsWith(".jpg")) {
                        Metadata metadata = new ExifReader(new FileInputStream(fileIdentifier)).extract();
                        if (metadata.containsDirectory(ExifDirectory.class)) {
                            ExifDirectory exifDirectory = (ExifDirectory) metadata.getDirectory(ExifDirectory.class);
                            if (exifDirectory.containsThumbnail()) {
                                img = ImageIO.read(new ByteArrayInputStream(exifDirectory.getThumbnailData()));
                            }
                        }
                    }
                    if (img == null) {
                        img = ImageIO.read(new FileInputStream(fileIdentifier));
                    }
                } else {
                    img = ImageIO.read(new URL(fileIdentifier));
                }           
                icon = new ImageIcon(ImageResize.scale(img, 256, 192));
                if (progress != null)progress.setValue((i * 100) / hits.length());
            } catch (Exception ex) {
                Logger.getLogger("global").log(Level.SEVERE, null, ex);
            }
            icons.add(icon);
            relevance.add(i, false);
        
        }
        if (progress != null)progress.setValue(100);
        fireTableDataChanged();
    }

    /**
     * @return
     */
    public ImageSearchHits getHits() {
        return hits;
    }

    public String getIsRelevant(int row){
       
       if(relevance.get(row) == true){
        return "1";
       }
      return "0";
    }

    public void setIsRelevant(Boolean relevant, int row){
        relevance.set(row, relevant);
    }

    

    @Override
    public boolean isCellEditable(int row, int column) {
         if (column < 2) {
            return false;
        } else {
            return true;
        }
    }


    @Override
     public void setValueAt(Object value, int row, int col) {
        Boolean relevant = (Boolean)relevance.get(row);
        setIsRelevant((Boolean)value, row);
        fireTableDataChanged();
    }

    public String getDate(int row) {
        String fileIdentifier = hits.doc(row).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
        Date imageDate = null;
        try {
            Metadata metaData = new ExifReader(new FileInputStream(fileIdentifier)).extract();
            if (metaData.containsDirectory(ExifDirectory.class)) {
                ExifDirectory exifDirectory = (ExifDirectory) metaData.getDirectory(ExifDirectory.class);
                if(exifDirectory.containsTag(ExifDirectory.TAG_DATETIME_ORIGINAL)){
                    imageDate = exifDirectory.getDate(ExifDirectory.TAG_DATETIME_ORIGINAL);
                }
            }else if(!metaData.containsDirectory(ExifDirectory.class)){
                     imageDate = new Date();
                }
        } catch (Exception e) {
            Logger.getLogger("global").log(Level.SEVERE, "Could not extract metadata", e);
        }
        return imageDate.toString();

    }
}
