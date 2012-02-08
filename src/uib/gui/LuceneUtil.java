/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.gui;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;


/**
 *
 * @author Olav
 */
public class LuceneUtil {

    /**
         * Searches for a document matching Term and updates it with the fields it has in common with
         * the document parameter.
         * @param writer
         * @param searcher
         * @param updateDoc contained fields will be replacements for document identified by term
         * @param term identifies a single document in the index which will be updated
         * @throws IOException
         * @throws IllegalArgumentException if given term matches more than 1 indexed document or none.
         */
        @SuppressWarnings("unchecked")
        public void searchAndUpdateDocument(IndexWriter writer, IndexSearcher searcher,
                        Document updateDoc, Term term) throws IOException {
                TermQuery query = new TermQuery(term);
                List<Fieldable> replacementFields; // Swhy dose'nt Field work as the type for list?!
                TopDocs hits = searcher.search(query, 10);

                if (hits.scoreDocs.length == 0) {
                        throw new IllegalArgumentException("No matches in the index for the given Term.");
                } else if (hits.scoreDocs.length > 1) {
                        throw new IllegalArgumentException("Given Term matches more than 1 document in the index.");
                } else {
                        int docId = hits.scoreDocs[0].doc;

                        //retrieve the old document
                        Document doc = searcher.doc(docId);

                        replacementFields = updateDoc.getFields();
                        for (Fieldable field : replacementFields) {
                                String name = field.name();
                                String currentValue = doc.get(name);
                                if (currentValue != null) {
                                        //replacement field value

                                        //remove all occurrences of the old field
                                        doc.removeFields(name);

                                        //insert the replacement
                                        doc.add(field);
                                } else {
                                        //new field
                                        doc.add(field);
                                }
                        }

                        //write the old document to the index with the modifications
                        writer.updateDocument(term, doc);
                }
        }


}
