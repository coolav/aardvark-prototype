
package uib.gui;

/**
 *
 * @author Olav
 */

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.*;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.ExifReader;
import uib.indexing.*;
import net.semanticmetadata.lire.DocumentBuilder;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.*;



public class IndexingThread extends Thread {
    AardvarkGui parent;

    
    public IndexingThread(AardvarkGui parent) {
        this.parent = parent;
    }

    @Override
    public void run() {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.setMaximumFractionDigits(0);
        df.setMinimumFractionDigits(0);
        try {
            parent.progressBarIndexing.setValue(0);
            java.util.ArrayList<java.lang.String> images =
                    getAllImages(
                            new java.io.File(parent.textfieldIndexDir.getText()), true);
            if (images == null) {
                JOptionPane.showMessageDialog(parent, "Could not find any files in "
                        + parent.textfieldIndexDir.getText(), "No files found", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean create = !parent.checkBoxAddToExistingIndex.isSelected();
            IndexWriter iw = new IndexWriter(FSDirectory.open
                    (new File(parent.textfieldIndexDirectory.getText())),
                    new SimpleAnalyzer(), create, IndexWriter.MaxFieldLength.UNLIMITED);
            int builderIdx = parent.selectboxDocumentBuilder.getSelectedIndex();
            DocumentBuilder builder = new MetadataBuilder();
            int count = 0;
            long time = System.currentTimeMillis();
            Document doc;
            ParallelIndexer indexer = new ParallelIndexer(images, builder);
            new Thread(indexer).start();
            while ((doc = indexer.getNext()) != null) {
                try {
                    iw.addDocument(doc);
                } catch (Exception e) {
                    System.err.println("Could not add document." + e);                  
                }
                count++;
                float percentage = (float) count / (float) images.size();
                parent.progressBarIndexing.setValue((int) Math.floor(100f * percentage));
                float msleft = (float) (System.currentTimeMillis() - time) / percentage;
                float secLeft = msleft * (1 - percentage) / 1000f;
                String toPaint = "~ " + df.format(secLeft) + " sec. left";
                if (secLeft > 90) toPaint = "~ " + Math.ceil(secLeft / 60) + " min. left";
                parent.progressBarIndexing.setString(toPaint);
            }
            long timeTaken = (System.currentTimeMillis() - time);
            float sec = ((float) timeTaken) / 1000f;
            System.out.println("Finished indexing ...");
            parent.progressBarIndexing.setString(Math.round(sec) + " sec. for " + count + " files");
            parent.buttonStartIndexing.setEnabled(true);
            parent.progressBarIndexing.setValue(100);
            iw.optimize();
            iw.close();

        } catch (IOException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
    }

    public static ArrayList<String> getAllImages(File directory, boolean descendIntoSubDirectories) throws IOException {
        ArrayList<String> resultList = new ArrayList<String>(256);
        File[] f = directory.listFiles();
        for (File file : f) {
            if (file != null && (file.getName().toLowerCase().endsWith(".jpg") || file.getName().toLowerCase().endsWith(".png")) && !file.getName().startsWith("tn_")) {
                resultList.add(file.getCanonicalPath());
            }
            if (descendIntoSubDirectories && file.isDirectory()) {
                ArrayList<String> tmp = getAllImages(file, true);
                if (tmp != null) {
                    resultList.addAll(tmp);
                }
            }
        }
        if (resultList.size() > 0)
            return resultList;
        else
            return null;
    }

    private BufferedImage readFile(String path) throws IOException {
        BufferedImage image = null;
        FileInputStream jpegFile = new FileInputStream(path);
        Metadata metadata = new Metadata();
        try {
            new ExifReader(jpegFile).extract(metadata);
            byte[] thumb = ((ExifDirectory) metadata.getDirectory(ExifDirectory.class)).getThumbnailData();
            if (thumb != null) image = ImageIO.read(new ByteArrayInputStream(thumb));

        } catch (JpegProcessingException e) {
            System.err.println("Could not extract thumbnail" + e);
        } catch (MetadataException e) {
            System.err.println("Could not extract thumbnail" + e);
        } catch (Exception e) {
            System.err.println("Could not extract thumbnail"+ e);
        }
        // Fallback:
        if (image == null) image = ImageIO.read(new FileInputStream(path));
        return image;
    }

}
