/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.indexing;

/**
 *
 * @author Olav
 *
 * To do, exchange Hashtable with synchronizedMap?
 */
import net.semanticmetadata.lire.DocumentBuilder;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.exif.ExifReader;
import net.semanticmetadata.lire.DocumentBuilder;
import org.apache.lucene.document.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Olav
 */
public class ParallelIndexer implements Runnable {
    // Vectors are already synchronized, so that's the cheap solution.

    Vector<String> imageFiles;
    private int NUMBER_OF_SYNC_THREADS = 10;
    Hashtable<String, Boolean> indexThreads = new Hashtable<String, Boolean>(3);
    DocumentBuilder builder;
    Vector<Document> finished = new Vector<Document>();
    private boolean started = false;
    private final ExecutorService pool;

    public ParallelIndexer(List<String> imageFiles, DocumentBuilder b) {
        this.imageFiles = new Vector<String>();
        assert (imageFiles != null);
        this.imageFiles.addAll(imageFiles);
        builder = b;
        pool = Executors.newFixedThreadPool(NUMBER_OF_SYNC_THREADS);
    }

    public void run() {
        for (int i = 1; i < NUMBER_OF_SYNC_THREADS; i++) {
            PhotoIndexer runnable = new PhotoIndexer(this);
//            Thread t = new Thread(runnable);
//            t.start();
//            indexThreads.put(t.getName(), false);
            pool.submit(runnable);
        }
        started = true;
    }

    public void addDoc(Document doc, String photofile) {
        if (doc != null) {
            finished.add(doc);
        }
        Thread.yield();
    }

    public Document getNext() {
        if (imageFiles.size() < 1) {
            boolean fb = true;
            for (String t : indexThreads.keySet()) {
                fb = fb && indexThreads.get(t);
            }
            if (started && fb) {
                return null;
            }
        }
        while (finished.size() < 1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return finished.remove(0);
    }

    private String getNextImage() {
        if (imageFiles.size() > 0) {
            return imageFiles.remove(0);

        } else {
            return null;
        }
    }

    class PhotoIndexer implements Runnable {

        String photo;
        ParallelIndexer parent;
        private boolean hasFinished = false;

        PhotoIndexer(ParallelIndexer parent) {
            this.parent = parent;

        }

        public void run() {
            while ((photo = parent.getNextImage()) != null) {
                try {
                    BufferedImage image = readFile(photo);
                    if (image != null) {
                        Document doc = parent.builder.createDocument(image, photo);
                        parent.addDoc(doc, photo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    parent.addDoc(null, photo);
                }
            }
            parent.indexThreads.put(Thread.currentThread().getName(), true);
        }

        private BufferedImage readFile(String path) throws IOException {
            BufferedImage image = null;
            if (path.toLowerCase().endsWith(".jpg")) {
                FileInputStream jpegFile = new FileInputStream(path);
                Metadata metadata = new Metadata();
                try {
                    new ExifReader(jpegFile).extract(metadata);
                    byte[] thumb = ((ExifDirectory) metadata.getDirectory(ExifDirectory.class)).getThumbnailData();
                    if (thumb != null) {
                        image = ImageIO.read(new ByteArrayInputStream(thumb));
                    }
                  
                } catch (JpegProcessingException e) {
                    System.err.println("Could not extract EXIF data for " + path);
                    System.err.println("\t" + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Could not extract EXIF data for " + path);
                    System.err.println("\t" + e.getMessage());
                }
                jpegFile.close();
            }
            // Fallback & PNGs:
            if (image == null) {
                try {
                    image = ImageIO.read(new File(path));
                } catch (Exception e) {
                    System.err.println("Error reading file " + path + "\n\t" + e.getMessage());
                }
            }
            return image;
        }
    }
}
