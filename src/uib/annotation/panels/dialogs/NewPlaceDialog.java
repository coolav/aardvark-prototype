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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
/**
 *
 * @author Olav
 */
public class NewPlaceDialog extends JDialog implements ActionListener, NewDescriptorDialogInterface {
    JButton ok, cancel;
    PlacePanel panel;
    Element placeDescriptor = null;

    public NewPlaceDialog(Frame owner) {
        super(owner, true);
        init();
    }

    private void init() {
        setTitle("Create new place object");
        JPanel buttonPane = new JPanel(new FlowLayout());
        panel = new PlacePanel();
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
            ArrayList<String> lines = null;
            if (panel.getAddress().length() > 0) {
                lines = new ArrayList<String>();
                StringTokenizer t = new StringTokenizer(panel.getAddress(), "\n", false);
                while (t.hasMoreElements()) {
                    String s = t.nextToken();
                    lines.add(s.trim());
                }
            }

            Namespace mpeg7, xsi;
            mpeg7 = Namespace.getNamespace("", "urn:mpeg:mpeg7:schema:2001");
            xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            placeDescriptor = new Element("SemanticBase", mpeg7).setAttribute("type", "SemanticPlaceType", xsi);
            Element label = new Element("Label", mpeg7).addContent(new Element("Name", mpeg7).addContent(panel.getLabel()));
            placeDescriptor.addContent(label);
            if (panel.getDescription().length() > 0) {
                placeDescriptor.addContent(new Element("Definition", mpeg7).addContent(new Element("FreeTextAnnotation", mpeg7).addContent(panel.getDescription())));
            }

            if (lines != null) {
                Element elem_address = new Element("PostalAddress", mpeg7);
                for (Iterator it1 = lines.iterator(); it1.hasNext();) {
                    String s = (String) it1.next();
                    elem_address.addContent(new Element("AddressLine", mpeg7).addContent(s));
                }
                placeDescriptor.addContent(new Element("Place", mpeg7).addContent(elem_address));
            }
            desc_created = true;
        } else {
            JOptionPane.showMessageDialog(this, "At least a name is required!");
        }

        return desc_created;
    }

    public Element createXML() {
        return placeDescriptor;
    }
}
