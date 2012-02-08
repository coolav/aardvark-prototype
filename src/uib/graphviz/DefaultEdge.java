/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.graphviz;

/**
 *
 * @author Olav
 */
public class DefaultEdge implements Edge {
    Node start, end;

    public DefaultEdge(Node start, Node end) {
        this.start = start;
        this.end = end;
    }

    public Node getStartNode() {
        return start;
    }

    public Node getEndNode() {
        return end;
    }

    public void setNodes(Node start, Node end) {
        this.start = start;
        this.end = end;
    }

}
