/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.panels.dialogs;

import org.jdom.Element;
import org.jdom.Namespace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 *
 * @author Olav
 */
public class NewTimeDialog extends JDialog implements ActionListener, NewDescriptorDialogInterface {
//    JTextField name, date;
//    JTextArea freeText;
    TimePanel panel;
    JButton ok, cancel;
    Element timeDescriptor = null;

    public NewTimeDialog(Frame owner) {
        super(owner, true);
        init();
    }

    private void init() {
        setTitle("Create new time object");
        panel = new TimePanel();
        JPanel buttonPane = new JPanel(new FlowLayout());

        ok = new JButton("OK");
        ok.setActionCommand("ok");
        ok.addActionListener(this);
        ok.setMnemonic('o');
        cancel = new JButton("Cancel");
        cancel.setActionCommand("cancel");
        cancel.addActionListener(this);

        buttonPane.add(ok);
        buttonPane.add(cancel);

        this.getContentPane().add(panel, BorderLayout.CENTER);
        this.getContentPane().add(buttonPane, BorderLayout.SOUTH);

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("ok")) {
            if (createDocument())
                this.setVisible(false);
        } else if (e.getActionCommand().equals("cancel")) {
            this.setVisible(false);
        }

    }


    private boolean createDocument() {
        boolean desc_created = false;
        if (panel.getLabel().length() > 0) {
            Namespace mpeg7, xsi;
            mpeg7 = Namespace.getNamespace("", "urn:mpeg:mpeg7:schema:2001");
            xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            timeDescriptor = new Element("SemanticBase", mpeg7).setAttribute("type", "SemanticTimeType", xsi);
            Element label = new Element("Label", mpeg7).addContent(new Element("Name", mpeg7).addContent(panel.getLabel()));
            timeDescriptor.addContent(label);
            if (panel.getDescription().length() > 0) {
                timeDescriptor.addContent(new Element("Definition", mpeg7).addContent(new Element("FreeTextAnnotation", mpeg7).addContent(panel.getDescription())));
            }
            if (panel.getDate().length() > 0) {
                timeDescriptor.addContent(new Element("Time", mpeg7).addContent(new Element("TimePoint", mpeg7).addContent(panel.getDate())));
            }
            desc_created = true;
        } else {
            JOptionPane.showMessageDialog(this, "At least a name is required!");
        }

        return desc_created;
    }

    public Element createXML() {
        return timeDescriptor;
    }

}
