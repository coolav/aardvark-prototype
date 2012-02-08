/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.annotation.util.image;

import javax.swing.*;
import java.awt.*;
/**
 * 
 *
 * @author Olav
 */
public class GradientButton extends JButton {

    public GradientButton(String text) {
        super(text);
// Prevents Swing from painting the background
        setContentAreaFilled(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
// Creates a two-stops gradient
        GradientPaint p;
        p = new GradientPaint(0, 0, new Color(0xFFFFFF),
                0, getHeight(), new Color(0xC8D2DE));
// Saves the state
        Paint oldPaint = g2.getPaint();
// Paints the background
        g2.setPaint(p);
        g2.fillRect(0, 0, getWidth(), getHeight());
// Restores the state
        g2.setPaint(oldPaint);
// Paints borders, text...
        super.paintComponent(g);
    }
}

