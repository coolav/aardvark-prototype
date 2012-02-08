/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.graphviz;

/**
 *
 * @author Olav
 */
public interface Edge {
    /**
     * Returns the starting node of the edge
     *
     * @return the starting node
     */
    public Node getStartNode();

    /**
     * Returns the ending node of the edge
     *
     * @return the ending node
     */
    public Node getEndNode();

    /**
     * Setter for the Nodes which are connected through the edge. Start and end are only
     * interesting in case of directed graphs.
     *
     * @param start
     * @param end
     */
    public void setNodes(Node start, Node end);
}
