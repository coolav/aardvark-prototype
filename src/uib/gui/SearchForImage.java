
package uib.gui;

import java.awt.CardLayout;
import java.awt.Rectangle;
import java.io.*;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;
import org.apache.lucene.index.*;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author Olav
 */
public class SearchForImage extends Thread {

    private AardvarkGui parent;
    final String path;
    public JPanel frame;

    public SearchForImage(AardvarkGui parent, String imagePath)
            throws FileNotFoundException, IOException {
        this.parent = parent;
        path = imagePath;
        frame = parent.topPanel;
    }

    public SearchForImage(AardvarkGui parent) {
        this.parent = parent;
        frame = parent.topPanel;
        path = null;
    }

    @Override
    public void run() {
        // setting to search panel:
        ((CardLayout) parent.cardPanel.getLayout()).first(parent.cardPanel);
        ((CardLayout) parent.cardPanel.getLayout()).next(parent.cardPanel);
        // switching away from search results ...
        ((CardLayout) parent.topPanel.getLayout()).first(parent.topPanel);
        try {
            parent.status.setText("Searching");
            long time = System.currentTimeMillis();
            IndexReader reader = IndexReader.open(FSDirectory.open
                    (new File(parent.textfieldIndexDirectory.getText())));
            int numDocs = reader.numDocs();
            System.out.println("numDocs = " + numDocs);

            ImageSearcher searcher = getSearcher();
            ImageSearchHits hits = searcher.search
                    (ImageIO.read(new FileInputStream(path)), reader);
            parent.tableModel.setHits
                    (hits, parent.progressBarSearchProgress);
            reader.close();

            Rectangle bounds = parent.resultsTable.getCellRect(2, 2, true);
            parent.jScrollPanelResults.getViewport().setViewPosition(bounds.getLocation());
            long timeTaken = (System.currentTimeMillis() - time);
            parent.status.setText("Finished search in " + timeTaken + 
                    " miliseconds" + " found " + numDocs + " documents");
        } catch (Exception e) {

        } finally {
            parent.resultsTable.setRowHeight(128);
            parent.resultsTable.getColumnModel().getColumn(1).setMaxWidth(256);
            parent.resultsTable.getColumnModel().getColumn(1).setMinWidth(256);
            parent.resultsTable.getColumnModel().getColumn(2).setMaxWidth(64);
            ((CardLayout) parent.topPanel.getLayout()).last(frame);
            parent.resultsTable.setEnabled(true);
        }
    }

    public ImageSearcher getSearcher() {
        int numResults = 50;
        float scalableColorWeight = parent.jsliderScalableColor.getValue() / 100;
        float edgeHistogramWeight = parent.jsliderEdgeHistogram.getValue() / 100;
        float scalableColorLayout = parent.jsliderColorLayout.getValue() / 100;
        try {
            numResults = Integer.parseInt(parent.textfieldNumberOfResults.getText());
        } catch (Exception e) {
        }
        ImageSearcher searcher = ImageSearcherFactory.createWeightedSearcher
                (numResults, scalableColorLayout, scalableColorWeight, edgeHistogramWeight);
        if (parent.selectboxDocumentBuilder.getSelectedIndex() == 1) {
            parent.jsliderScalableColor.setValue(100);
            parent.jsliderEdgeHistogram.setValue(0);
            parent.jsliderColorLayout.setValue(0);
            searcher = ImageSearcherFactory.createWeightedSearcher(numResults, 1f, 0f, 0f);
        } else if (parent.selectboxDocumentBuilder.getSelectedIndex() == 2) {
            parent.jsliderScalableColor.setValue(0);
            parent.jsliderEdgeHistogram.setValue(0);
            parent.jsliderColorLayout.setValue(100);
            searcher = ImageSearcherFactory.createWeightedSearcher(numResults, 0f, 0f, 1f);
        } else if (parent.selectboxDocumentBuilder.getSelectedIndex() == 3) {
            parent.jsliderScalableColor.setValue(0);
            parent.jsliderEdgeHistogram.setValue(100);
            parent.jsliderColorLayout.setValue(0);
            searcher = ImageSearcherFactory.createWeightedSearcher(numResults, 0f, 1f, 0f);
        } else if (parent.selectboxDocumentBuilder.getSelectedIndex() == 4) {
            searcher = ImageSearcherFactory.createDefaultCorrelogramImageSearcher(numResults);
        } else if (parent.selectboxDocumentBuilder.getSelectedIndex() == 5) {
            searcher = ImageSearcherFactory.createCEDDImageSearcher(numResults);
        } else if (parent.selectboxDocumentBuilder.getSelectedIndex() == 6) {
            searcher = ImageSearcherFactory.createFCTHImageSearcher(numResults);
        } else if (parent.selectboxDocumentBuilder.getSelectedIndex() == 7) {
            searcher = ImageSearcherFactory.createColorHistogramImageSearcher(numResults);
        } else if (parent.selectboxDocumentBuilder.getSelectedIndex() == 8) {
            searcher = ImageSearcherFactory.createTamuraImageSearcher(numResults);
        } else if (parent.selectboxDocumentBuilder.getSelectedIndex() > 8) {
            searcher = ImageSearcherFactory.createGaborImageSearcher(numResults);
        }
        return searcher;
    }
}
