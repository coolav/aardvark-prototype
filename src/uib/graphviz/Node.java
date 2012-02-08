/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.graphviz;

/**
 *
 * @author Olav
 */
public interface Node{
    /**
     * X has to be in normalized [0,1]
     *
     * @return x from [0,1]
     */
    public double getX();

    /**
     * Y has to be in normalized [0,1]
     *
     * @return y from [0,1]
     */
    public double getY();

    /**
     * X has to be in normalized [0,1]
     *
     * @param x from [0,1]
     */
    public void setX(double x);

    /**
     * Y has to be in normalized [0,1]
     *
     * @param y from [0,1]
     */
    public void setY(double y);

    /**
     * Calculates the distance between two nodes.
     *
     * @param node
     * @return a double telling the distance in euclidean metrics.
     */
    public double distance(Node node);

    /**
     * returns a pair of doubles (x,y) telling the direction vector from this node to
     * parameter node
     *
     * @param node
     * @return
     */
    public Vector2D direction(Node node);

    /**
     * Moves node along given vector
     *
     * @param whereToMove
     */
    public void move(Vector2D whereToMove);

}
