/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.model;

import uib.annotation.util.image.IconCache;
import org.jdom.Element;
import org.jdom.Namespace;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;



import javax.swing.*;

/**
 *
 * @author Olav
 */
public class SemanticEntityRepresentation {



    public static int WIDTH = 110;
    public static int HEIGHT = 50 ;
    private boolean marked = false;
    private Point p;
    private String label;
    private Element node;
    private String type;
    private Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");


    private boolean highlighted = false;

    public static final Color COLOR_BOX_FILL = new Color(204, 230, 255);
    public static final Color COLOR_BOX_FILL_HIGHLIGHT = Color.red;

    public SemanticEntityRepresentation(Point p, String l) {
        this.p = p;
        this.label = l;
        node = new Element("SemanticBase");
        node.addContent(new Element("Label").addContent(new Element("Name").setText(l)));
        type = null;
    }

    public SemanticEntityRepresentation(Point point, Element semanticObject) {
        this.p = point;
        this.label = semanticObject.getChild("Label", semanticObject.getNamespace()).getChildText("Name", semanticObject.getNamespace()).trim();
        type = semanticObject.getAttribute("type", xsi).getValue();
        this.node = semanticObject;
    }

    public Element getNode() {
        return node;
    }

    public void drawObject(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (!marked) {
            if (highlighted) {
                g2.setColor(Color.blue);
                g2.fill(new RoundRectangle2D.Double(p.x, p.y, WIDTH, HEIGHT, 8, 8));
                g2.setColor(COLOR_BOX_FILL);
                g2.fill(new RoundRectangle2D.Double(p.x + 4, p.y + 4, WIDTH - 8, HEIGHT - 8, 8, 8));
            } else {
                g2.setColor(COLOR_BOX_FILL);
                g2.fill(new RoundRectangle2D.Double(p.x, p.y, WIDTH, HEIGHT, 8, 8));
                g2.setColor(Color.black);
                g2.draw(new RoundRectangle2D.Double(p.x, p.y, WIDTH, HEIGHT, 8, 8));
            }
        } else {
            if (highlighted) {
                g2.setColor(Color.blue);
                g2.fill(new RoundRectangle2D.Double(p.x, p.y, WIDTH, HEIGHT, 8, 8));
                g2.setColor(COLOR_BOX_FILL_HIGHLIGHT);
                g2.fill(new RoundRectangle2D.Double(p.x + 4, p.y + 4, WIDTH - 8, HEIGHT - 8, 8, 8));
            } else {
                g2.setColor(COLOR_BOX_FILL_HIGHLIGHT);
                g2.fill(new RoundRectangle2D.Double(p.x, p.y, WIDTH, HEIGHT, 8, 8));
                g2.setColor(Color.black);
                g2.draw(new RoundRectangle2D.Double(p.x, p.y, WIDTH, HEIGHT, 8, 8));
            }
        }
        if (type != null) {
            ImageIcon icon;
            if (type.contains("Agent")) {
                icon = IconCache.getInstance().getAgentIcon();
            } else if(type.contains("Place")) {
                icon = IconCache.getInstance().getPlaceIcon();
            } else if(type.contains("Event")) {
                icon = IconCache.getInstance().getEventIcon();
            } else if(type.contains("Time")) {
                icon = IconCache.getInstance().getTimeIcon();
            } else  {
                icon = IconCache.getInstance().getObjectIcon();
            }
            g2.drawImage(icon.getImage(), p.x + WIDTH - 8, p.y - 8, null);
        }
        g2.setColor(Color.black);
        int width = g2.getFontMetrics().stringWidth(label);
        // Falls der String zu lang ist wird er gesplittet ...
        if (width > WIDTH) {
            String l1, l2;
            int center1, center, half;
            half = label.length() >> 1;
            center1 = label.indexOf(' ', half);
            center = label.lastIndexOf(' ', half);
            if (Math.abs(half - center1) < Math.abs(half - center))
                center = center1;
            if (center > 0) {
                l1 = label.substring(0, center);
                l2 = label.substring(center + 1);
//                width = g2.getFontMetrics().stringWidth(label);
                width = g2.getFontMetrics().stringWidth(l1);
                int height = g2.getFontMetrics().getFont().getSize();
                g2.drawString(l1, (p.x + (WIDTH >> 1)) - (width >> 1), (p.y + (HEIGHT >> 1)) - 2);
                width = g2.getFontMetrics().stringWidth(l2);
                g2.drawString(l2, (p.x + (WIDTH >> 1)) - (width >> 1), (p.y + (HEIGHT >> 1)) + height + 2);
            } else {
                int height = g2.getFontMetrics().getFont().getSize();
                g2.drawString(label, (p.x + (WIDTH >> 1)) - (width >> 1), (p.y + (HEIGHT >> 1)) + (height >> 1));
            }
        } else {
            int height = g2.getFontMetrics().getFont().getSize();
            g2.drawString(label, (p.x + (WIDTH >> 1)) - (width >> 1), (p.y + (HEIGHT >> 1)) + (height >> 1));
        }
    }

    public Rectangle getR() {
        return new Rectangle(p.x, p.y, WIDTH, HEIGHT);
    }

    public void setP(Point p) {
        this.p = p;
    }

    public void setLabel(String l) {
        this.label = l;
    }

    public String getLabel() {
        return label;
    }

    public Point getP() {
        return p;
    }

    public Color getFillColor() {
        return COLOR_BOX_FILL;
    }

    /**
     * not supported any more
     *
     * @param fillColor
     */
    public void setFillColor(Color fillColor) {
//        this.COLOR_BOX_FILL = COLOR_BOX_FILL;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean checkIfInside(Point check) {
        if (check.x <= (p.x + WIDTH) && check.x >= p.x && check.y >= p.y && check.y <= (p.y + HEIGHT)) {
            highlighted = true;
        } else {
            highlighted = false;
        }
        return highlighted;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }
}


