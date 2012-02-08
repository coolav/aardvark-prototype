/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import net.semanticmetadata.lire.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;


/**
 *
 * @author Olav
 */
public class SearchForDocument extends Thread {

    private AardvarkGui parent;
    private Document document;
    private JPanel frame;


    public SearchForDocument(AardvarkGui parent, Document searchDocument) throws FileNotFoundException, IOException {
        this.parent = parent;
        document = searchDocument;
        frame = parent.topPanel;
    }

    public SearchForDocument(AardvarkGui parent, int searchRow) {
        this.parent = parent;
        
    }

    @Override
    public void run() {
        SearchForImage searchImage = new SearchForImage(parent);
        // setting to search panel:
        ((CardLayout) parent.cardPanel.getLayout()).first(parent.cardPanel);
        ((CardLayout) parent.cardPanel.getLayout()).next(parent.cardPanel);
        // switching away from search results ...
        ((CardLayout) parent.topPanel.getLayout()).first(parent.topPanel);
        parent.resultsTable.setEnabled(false);
        try {
            parent.status.setText("Searching");
            long time = System.currentTimeMillis();
            parent.progressBarSearchProgress.setValue(0);
            IndexReader reader = IndexReader.open(FSDirectory.open
                    (new File(parent.textfieldIndexDirectory.getText())));
            ImageSearcher searcher = searchImage.getSearcher();
            parent.progressBarSearchProgress.setString
                    ("Searching for matching images: " + searcher.getClass().getName());
            ImageSearchHits hits = searcher.search(document, reader);
            parent.tableModel.setHits(hits, parent.progressBarSearchProgress);
            reader.close();
            // scroll to first row:
            Rectangle bounds = parent.resultsTable.getCellRect(0, 0, true);
            parent.jScrollPanelResults.getViewport().setViewPosition(bounds.getLocation());
            long timeTaken = (System.currentTimeMillis() - time);
            parent.status.setText("Finished search in " + timeTaken + " miliseconds");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog
                    (null, "An error occurred while searching: "
                    + e.getMessage(), "Error while searching", JOptionPane.ERROR_MESSAGE);

        } finally {
            parent.resultsTable.setRowHeight(128);
            parent.resultsTable.getColumnModel().getColumn(1).setMaxWidth(256);
            parent.resultsTable.getColumnModel().getColumn(1).setMinWidth(256);
            parent.resultsTable.getColumnModel().getColumn(2).setMaxWidth(64);

            ((CardLayout) parent.topPanel.getLayout()).last(frame);
            parent.resultsTable.setEnabled(true);
            
        }
    }
}
