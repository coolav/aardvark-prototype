/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.graphviz;

/**
 *
 * @author Olav
 */
public class LabeledNode  implements Node {
    double x, y;
    String label;

    public LabeledNode(double x, double y) {
        this.x = x;
        this.y = y;
        this.label = null;
    }

    public LabeledNode(double x, double y, String label) {
        this.x = x;
        this.y = y;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double distance(Node node) {
        double dx = x - node.getX();
        double dy = y - node.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public Vector2D direction(Node node) {
        return new Vector2D(node.getX() - x, node.getY() - y);
    }

    public void move(Vector2D whereToMove) {
        x += whereToMove.getX();
        y += whereToMove.getY();
    }

}
