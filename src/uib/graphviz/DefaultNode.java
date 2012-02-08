/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.graphviz;

/**
 *
 * @author Olav
 */
public class DefaultNode implements Node {
    double x, y;
    Object nodeObject;

    public DefaultNode(double x, double y) {
        this.x = x;
        this.y = y;
        this.nodeObject = null;
    }

    public DefaultNode(double x, double y, Object nodeObject) {
        this.x = x;
        this.y = y;
        this.nodeObject = nodeObject;
    }

    public Object getNodeObject() {
        return nodeObject;
    }

    public void setNodeObject(Object nodeObject) {
        this.nodeObject = nodeObject;
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
