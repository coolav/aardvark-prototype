/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.gui;

/**
 *
 * @author Olav
 */

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.StringWriter;

public class TextPreviewDialog extends JDialog {
    private JTextArea text;

    public TextPreviewDialog(Frame frame, Document d) {
        super(frame);
        this.setTitle("MPEG-7");

        text = new JTextArea();
        text.setTabSize(2);

        XMLOutputter op = new XMLOutputter(Format.getPrettyFormat());
        StringWriter sw = new StringWriter();
        try {
            op.output(d, sw);
            text.setText(sw.toString());
        } catch (IOException e) {
            text.setText(e.toString());
        }


        this.getContentPane().add(new JScrollPane(text), BorderLayout.CENTER);
    }
}
