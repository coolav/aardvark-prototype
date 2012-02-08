/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.retrieval.analyze;


import java.io.Reader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
/**
 *
 * @author Olav
 */
public class GraphAnalyzer extends Analyzer {

    public GraphAnalyzer() {
    }

    /**
     * Return the TokenStream
     * @param s some String or not, is not used anyway
     * @param reader
     * @return
     */
    public TokenStream tokenStream(String s, Reader reader) {
        return new GraphTokenizer(reader);
    }
    
}
