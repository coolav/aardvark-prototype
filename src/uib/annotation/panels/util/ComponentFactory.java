/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.panels.util;

import uib.annotation.panels.util.TitleLabel;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.*;
/**
 *
 * @author Olav
 */
public class ComponentFactory {
    public static JPanel createTitledPanel(String title, JPanel panel) {
        JPanel result = new JPanel(new BorderLayout());
        result.add(panel, BorderLayout.CENTER);
//        JLabel titleLabel = new JLabel(title);
//        Font font = titleLabel.getFont();
//        titleLabel.setFont(font.deriveFont(Font.BOLD, font.getSize2D() + 1f));
//        titleLabel.setBorder(BorderFactory.createEmptyBorder(9, 0, 3, 0));
//        ((Graphics2D) titleLabel.getGraphics()).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        result.add(new TitleLabel(title, new JLabel().getFont().deriveFont(Font.BOLD)), BorderLayout.NORTH);
        return result;
    }

}
