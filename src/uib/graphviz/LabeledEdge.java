/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.graphviz;

/**
 *
 * @author Olav
 */
public class LabeledEdge implements Edge {
    Node start, end;
    String label;

    public LabeledEdge(Node start, Node end) {
        this.start = start;
        this.end = end;
        label = null;
    }

    public LabeledEdge(Node start, Node end, String label) {
        this.start = start;
        this.end = end;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
