/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import junit.framework.TestCase;
import uib.retrieval.engine.LuceneRetrievalEngine;

/**
 *
 * @author Olav
 */
public class LuceneRetrievalEngineTest extends TestCase {
    private LuceneRetrievalEngine engine;
    private String pathToIndex;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        engine = new LuceneRetrievalEngine(40);
        pathToIndex = "E:/Oppgaave/MPEG-7/CaliphEmir/testdata";
    }

    public void testSemanticIndexing() {
        engine.indexFilesSemantically(pathToIndex);
    }
    
    public void testRelations(){
        //System.out.println(LuceneRetrievalEngine.relationMapping.values().toString());
         Map relationMappingInverse = new HashMap<String, String>(LuceneRetrievalEngine.relationMapping.size());
        for (Iterator<String> iterator = LuceneRetrievalEngine.relationMapping.keySet().iterator(); iterator.hasNext();) {
            String s = iterator.next();
            relationMappingInverse.put(LuceneRetrievalEngine.relationMapping.get(s), s);
            System.out.println(LuceneRetrievalEngine.relationMapping.keySet());
        }
    }
    
}
