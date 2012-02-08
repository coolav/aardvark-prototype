/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.graphviz;

/**
 *
 * @author Olav
 */
public class Vector2D {
    private double x, y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void addVector2D(Vector2D vector2D) {
        x += vector2D.getX();
        y += vector2D.getY();
    }

    public void normalize() {
        double length = getLength();
        if (length != 0.0) {
            x = x / length;
            y = y / length;
        }
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y);
    }

    public void multiply(double scalar) {
        if (scalar != 0.0) {
            x *= scalar;
            y *= scalar;
        } else {
            x = 0.0;
            y = 0.0;
        }
    }

}
