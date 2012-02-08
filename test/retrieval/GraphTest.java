/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package retrieval;

import junit.framework.TestCase;
import uib.retrieval.model.Graph;

/**
 *
 * @author Olav
 */
public class GraphTest extends TestCase {
    final static String GRAPH1 = "[9] [15] [26] [30] [31] [32] [locationOf 30 9] [locationOf 32 30] [locationOf 9 15] [locationOf 9 26] [timeOf 31 30]";

    public void testCreateFromString() {
        Graph g = new Graph(GRAPH1);
    }
}
