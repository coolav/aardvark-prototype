/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.download;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.semanticmetadata.lire.DocumentBuilder;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import uib.gui.AardvarkGui;
import uib.gui.ReadWriteLock;
import uib.indexing.MetadataBuilder;


/**
 *
 * @author Olav
 */
public class ImageIndexingThread extends Thread {
   
    public static final String cacheDirectory = "/artimages/";
    public static String installPath = "";
    AardvarkGui parent;
    private ImageDownloadThread idt;
    private int numberOfPhotosToIndex = 200;
    private ReadWriteLock lock;

    public ImageIndexingThread(AardvarkGui parent) {
        this.parent = parent;
    }

    public ImageIndexingThread(AardvarkGui parent, int maxNumberOfPhotos) {
        this.parent = parent;
        this.numberOfPhotosToIndex = maxNumberOfPhotos;
    }

    @Override
    public void run() {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.setMaximumFractionDigits(0);
        df.setMinimumFractionDigits(0);
        DocumentBuilder builder = new MetadataBuilder();
        lock = new ReadWriteLock();
        idt = new ImageDownloadThread(builder);
        try {
            if (parent.reader != null) {
                parent.reader.close();
            }
            // In order to get the absolute path to the images, get the path to 
            //the install directory 
            installPath = new File(".").getCanonicalPath();
            
            File cacheDir = new File(installPath + cacheDirectory);
            if (!cacheDir.exists()) {
                cacheDir.mkdir();
            }
            parent.progressBarDownloadImages.setValue(0);
            parent.progressBarDownloadImages.setString("Downloading photos from server");
            parent.progressBarDownloadImages.setString("Downloading photos from server: " + ImageDownloadThread.images.size() + " found.");

            PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new SimpleAnalyzer());
            wrapper.addAnalyzer("tags", new WhitespaceAnalyzer());

            boolean create = !parent.checkBoxAddToExistingIndex.isSelected();
            IndexWriter iw = new IndexWriter(FSDirectory.open(new File(parent.textfieldIndexDirectory.getText())), wrapper,
                    create, IndexWriter.MaxFieldLength.UNLIMITED);
            int builderIdx = parent.selectboxDocumentBuilder.getSelectedIndex();

            int count = 0;
            long time = System.currentTimeMillis();
            ImageDownloadThread downloader = new ImageDownloadThread(builder);
            new Thread(downloader).start();
            Document doc = null;
            while ((doc = downloader.getCurrentDoc()) != null) {
                try {
                    iw.addDocument(doc);
                } catch (Exception e) {
                    System.err.println("Could not add document");
                    lock.unlockWrite();
                    parent.status.setText("Download failed, please try again");                            
                    
                }
                count++;
                float percentage = (float) count / (float) ImageDownloadThread.images.size();
                parent.progressBarDownloadImages.setValue((int) Math.floor(100f * percentage));
                float msleft = (float) (System.currentTimeMillis() - time) / percentage;
                float secLeft = msleft * (1 - percentage) / 1000f;
                String toPaint;
                if (secLeft > 60) {
                    toPaint = "~ " + Math.ceil(secLeft / 60) + " min. left";
                } else if (secLeft > 30) {
                    toPaint = "< 1 min. left";
                } else {
                    toPaint = "< 30 sec. left";
                }
                parent.progressBarDownloadImages.setString(toPaint);
            }
            long timeTaken = (System.currentTimeMillis() - time);
            float sec = ((float) timeTaken) / 1000f;
            parent.progressBarDownloadImages.setValue(100);
            parent.progressBarDownloadImages.setString(Math.round(sec) + " sec. for " + count + " files");
            parent.progressBarDownloadImages.setEnabled(true);
            iw.optimize();
            iw.close();            


        } catch (IOException ex) {
            Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
        catch(InterruptedException ex){
             Logger.getLogger("global").log(Level.SEVERE, null, ex);
        }
    }

    private BufferedImage readFile(String path) throws IOException {
        BufferedImage image = ImageIO.read(new URL(path));
        return image;
    }
    public void setPath(){
    }
}
