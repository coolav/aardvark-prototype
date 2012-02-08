/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.model;



import uib.annotation.graphics.Arrow;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
/**
 *
 * @author Olav
 */
public class SemanticRelationRepresentation {

    private SemanticEntityRepresentation source, target;
    private String label;
    private Line2D line;
    private boolean highlighted;
    private RoundRectangle2D.Double labelBackGround = null;
    public static final Color COLOR_ARROW_HIGHLIGHT = Color.red;
    public static final Color COLOR_ARROW = new Color(36, 74, 200);

    public SemanticRelationRepresentation(SemanticEntityRepresentation source, SemanticEntityRepresentation target, String label) {
        this.source = source;
        this.target = target;
        this.label = label;
        line = new Line2D.Double();
        highlighted = false;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public SemanticEntityRepresentation getTarget() {
        return target;
    }

    public void setTarget(SemanticEntityRepresentation target) {
        this.target = target;
    }

    public SemanticEntityRepresentation getSource() {
        return source;
    }

    public void setSource(SemanticEntityRepresentation source) {
        this.source = source;
    }

    public void drawRelation(Graphics2D g2) {
        ArrayList<Point> sPoints = new ArrayList<Point>();
        ArrayList<Point> tPoints = new ArrayList<Point>();
        Point sourcePoint, targetPoint;
        Point sp = source.getP();
        Point tp = target.getP();
        sourcePoint = null;
        targetPoint = null;

        sPoints.add(new Point(sp.x + 2 * SemanticEntityRepresentation.WIDTH / 3, sp.y));
        sPoints.add(new Point(sp.x + SemanticEntityRepresentation.WIDTH / 3, sp.y));
        sPoints.add(new Point(sp.x + 2 * SemanticEntityRepresentation.WIDTH / 3, sp.y + SemanticEntityRepresentation.HEIGHT));
        sPoints.add(new Point(sp.x + SemanticEntityRepresentation.WIDTH / 3, sp.y + SemanticEntityRepresentation.HEIGHT));

        sPoints.add(new Point(sp.x, sp.y + 2 * SemanticEntityRepresentation.HEIGHT / 3));
        sPoints.add(new Point(sp.x, sp.y + SemanticEntityRepresentation.HEIGHT / 3));
        sPoints.add(new Point(sp.x + SemanticEntityRepresentation.WIDTH, sp.y + 2 * SemanticEntityRepresentation.HEIGHT / 3));
        sPoints.add(new Point(sp.x + SemanticEntityRepresentation.WIDTH, sp.y + SemanticEntityRepresentation.HEIGHT / 3));

        sPoints.add(new Point(sp.x, sp.y));
        sPoints.add(new Point(sp.x, sp.y + SemanticEntityRepresentation.HEIGHT));
        sPoints.add(new Point(sp.x + SemanticEntityRepresentation.WIDTH, sp.y));
        sPoints.add(new Point(sp.x + SemanticEntityRepresentation.WIDTH, sp.y + SemanticEntityRepresentation.HEIGHT));

        tPoints.add(new Point(tp.x + 2 * SemanticEntityRepresentation.WIDTH / 3, tp.y));
        tPoints.add(new Point(tp.x + SemanticEntityRepresentation.WIDTH / 3, tp.y));
        tPoints.add(new Point(tp.x + 2 * SemanticEntityRepresentation.WIDTH / 3, tp.y + SemanticEntityRepresentation.HEIGHT));
        tPoints.add(new Point(tp.x + SemanticEntityRepresentation.WIDTH / 3, tp.y + SemanticEntityRepresentation.HEIGHT));

        tPoints.add(new Point(tp.x, tp.y + 2 * SemanticEntityRepresentation.HEIGHT / 3));
        tPoints.add(new Point(tp.x, tp.y + SemanticEntityRepresentation.HEIGHT / 3));
        tPoints.add(new Point(tp.x + SemanticEntityRepresentation.WIDTH, tp.y + 2 * SemanticEntityRepresentation.HEIGHT / 3));
        tPoints.add(new Point(tp.x + SemanticEntityRepresentation.WIDTH, tp.y + SemanticEntityRepresentation.HEIGHT / 3));

        tPoints.add(new Point(tp.x, tp.y));
        tPoints.add(new Point(tp.x, tp.y + SemanticEntityRepresentation.HEIGHT));
        tPoints.add(new Point(tp.x + SemanticEntityRepresentation.WIDTH, tp.y));
        tPoints.add(new Point(tp.x + SemanticEntityRepresentation.WIDTH, tp.y + SemanticEntityRepresentation.HEIGHT));


        double minDistance = -1.0;
        for (Iterator i1 = sPoints.iterator(); i1.hasNext();) {
            Point point = (Point) i1.next();
            for (Iterator it2 = tPoints.iterator(); it2.hasNext();) {
                Point pt = (Point) it2.next();
                if (minDistance > pt.distance(point) || minDistance < 0) {
                    minDistance = pt.distance(point);
                    sourcePoint = point;
                    targetPoint = pt;
                }
            }
        }
        if (highlighted)
            g2.setColor(COLOR_ARROW_HIGHLIGHT);
        else
            g2.setColor(COLOR_ARROW);
        g2.fill(new Arrow(new Line2D.Double((double) sourcePoint.x, (double) sourcePoint.y, (double) targetPoint.x, (double) targetPoint.y), 3.0));
        g2.drawLine(sourcePoint.x, sourcePoint.y, targetPoint.x, targetPoint.y);
        line.setLine((double) sourcePoint.x, (double) sourcePoint.y, (double) targetPoint.x, (double) targetPoint.y);
        g2.fillOval(sourcePoint.x - 3, sourcePoint.y - 3, 6, 6);

        // Arrowheads
        Composite comp = g2.getComposite();
        float alpha = 0.75f;
        int type = AlphaComposite.SRC_OVER;
        AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
        g2.setComposite(composite);

        g2.setColor(Color.white);
        Rectangle2D bounds = g2.getFontMetrics().getStringBounds(label, g2);
        int fillRectX = ((sourcePoint.x + targetPoint.x) >> 1) - (((int) bounds.getWidth()) >> 1) - 4;
        int fillRectY = ((sourcePoint.y + targetPoint.y) >> 1) - ((int) bounds.getHeight() + 1);
        int fillRectWidth = (int) bounds.getWidth() + 8;
        int fillRectHeight = (int) bounds.getHeight() + 8;
        labelBackGround = new RoundRectangle2D.Double(fillRectX, fillRectY, fillRectWidth, fillRectHeight, 12.0, 12.0);
        g2.fill(labelBackGround);
//        g2.fillRect(fillRectX, fillRectY, fillRectWidth, fillRectHeight);

        g2.setComposite(comp);

        g2.setColor(Color.black);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = ((sourcePoint.x + targetPoint.x) >> 1) - (g2.getFontMetrics().stringWidth(label) >> 1);
        g2.drawString(label, x, (sourcePoint.y + targetPoint.y) >> 1);
    }

    public boolean contains(Point p) {
        Arrow t = new Arrow(line, 3.0);
        boolean b = t.contains(p);
        if (labelBackGround != null && labelBackGround.contains(p)) {
            b = true;
        }
        if (b) {
            highlighted = true;
        } else {
            highlighted = false;
        }
        return highlighted;
    }
}



