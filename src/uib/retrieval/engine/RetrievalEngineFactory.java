/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.retrieval.engine;


/**
 *
 * @author Olav
 */
class RetrievalEngineFactory {
    
    
    public static RetrievalEngine getLuceneRetrievalEngine() {
        return new LuceneRetrievalEngine(40);
    }

    public static RetrievalEngine getPathIndexRetrievalEngine() {
        return new LucenePathIndexRetrievalEngine(40);
    }

    
    
}
