/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.util;



import uib.annotation.panels.AnnotationPanel;
import uib.graphviz.DefaultNode;
import uib.graphviz.Node;
import uib.graphviz.SpringEmbedder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import uib.gui.AardvarkGui;
import uib.annotation.model.SemanticEntityRepresentation;

import static java.lang.Thread.sleep;
/**
 *
 * @author Olav
 */
public class EmbedderThread implements Runnable {
    AnnotationPanel parent;
    SpringEmbedder se;
    double x, y;
    boolean running = true;
    LinkedList<EmbedderThread> currentlyRunning = null;

    public EmbedderThread(AnnotationPanel parent, SpringEmbedder se, LinkedList<EmbedderThread> running) {
        this.parent = parent;
        for (Iterator<EmbedderThread> iterator = running.iterator(); iterator.hasNext();) {
            iterator.next().endEmbedding();
        }
        currentlyRunning = running;
        running.add(this);
        
        this.se = se;
        x = parent.getSize().getWidth() - (SemanticEntityRepresentation.WIDTH << 1);
        y = parent.getSize().getHeight() - SemanticEntityRepresentation.HEIGHT * 3;
    }

    public void run() {
        while (se.step() > 0 && running) {
            List<? extends Node> list = se.getNodeList();

            double xMin = 1.0, xMax = 0.0, yMin = 1.0, yMax = 0.0;
            for (Iterator<? extends Node> iterator = list.iterator(); iterator.hasNext();) {
                Node node = iterator.next();
                if (node.getX() < xMin) xMin = node.getX();
                if (node.getX() > xMax) xMax = node.getX();
                if (node.getY() < yMin) yMin = node.getY();
                if (node.getY() > yMax) yMax = node.getY();
            }


            for (Iterator<? extends Node> it = list.iterator(); it.hasNext();) {
                Node node = it.next();
                double nx = (node.getX() - xMin) / (xMax - xMin);
                double ny = (node.getY() - yMin) / (yMax - yMin);

                SemanticEntityRepresentation sor = (SemanticEntityRepresentation) ((DefaultNode) node).getNodeObject();
//                sor.getP().setLocation(x * nx , y * ny );
                sor.getP().setLocation(x * nx + (SemanticEntityRepresentation.WIDTH >> 1), y * ny + (SemanticEntityRepresentation.HEIGHT >> 1));
            }
            parent.repaint();
            try {
                sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (currentlyRunning.contains(this)) {
            currentlyRunning.remove(this);
        }
    }

    public void endEmbedding() {
        running = false;
    }

}
