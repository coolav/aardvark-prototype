/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.panels.util;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author Olav
 */
public class TitleLabel  extends JPanel {
    private String label;
    private Font font;
    private final int rightMargin;
    private final int bottomMargin;

    public TitleLabel(String label, Font font) {
        this.label = label;
        this.font = font;
        this.setPreferredSize(new Dimension(10, 22));
        rightMargin = 3;
        bottomMargin = 6;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(font);
        int width = g2.getFontMetrics().stringWidth(label);
        int height = (int) font.getSize2D();
        g2.drawString(label, 0, getHeight() - bottomMargin);
        int y1 = getHeight() - bottomMargin - height / 2;
        g2.setColor(Color.decode("#808080"));
        g2.drawLine(width + 6, y1, getWidth()-rightMargin, y1);
        g2.setColor(Color.decode("#FFFFFF"));
        g2.drawLine(width + 6, y1+1, getWidth()-rightMargin, y1+1);
    }

}
