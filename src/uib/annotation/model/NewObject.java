/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.model;

import uib.annotation.util.DataExchange;
import org.jdom.Element;
import org.jdom.Namespace;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 *
 * @author Olav
 */
public class NewObject extends JDialog implements ActionListener {
    Element node;
    private JPanel buttonPanel, gnp, fnp, cp, fp, tmpPanel;
    private JComboBox types;
    private JTextField gnf;
    private JTextArea fta;
    private JButton ok, cancel;
    private DataExchange bde;

    public NewObject(DataExchange bde) throws HeadlessException {
        this.bde = bde;
        this.setTitle("Create a new object");
        init();
    }

    private void init() {
        // this.setSize(640, 480);
        node = null;

        String[] availabletypes = {"SemanticPlaceType", "SemanticTimeType", "ObjectType"};
        types = new JComboBox(availabletypes);

        cp = new JPanel(new BorderLayout());
        cp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Type"));
        cp.add(new JLabel("Type: "), BorderLayout.WEST);
        cp.add(types, BorderLayout.CENTER);

        gnf = new JTextField(20);
        gnp = new JPanel(new BorderLayout());
        gnp.add(new JLabel("Name: "), BorderLayout.WEST);
        gnp.add(gnf, BorderLayout.CENTER);


        ok = new JButton("Ok");
        cancel = new JButton("Cancel");
        ok.addActionListener(this);
        cancel.addActionListener(this);
        ok.setActionCommand("ok");
        cancel.setActionCommand("cancel");

        fta = new JTextArea(10, 20);
        fp = new JPanel(new BorderLayout());
        fp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Description"));
        fp.add(new JScrollPane(fta), BorderLayout.CENTER);
        buttonPanel = new JPanel();
        buttonPanel.add(ok);
        buttonPanel.add(cancel);

        JPanel tempgnp = new JPanel(new BorderLayout());
        tempgnp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Name"));
        tempgnp.add(gnp, BorderLayout.CENTER);
        // tmpPanel.add(fnp, BorderLayout.CENTER);


        tmpPanel = new JPanel(new BorderLayout());
        tmpPanel.add(cp, BorderLayout.NORTH);
        tmpPanel.add(tempgnp, BorderLayout.CENTER);

        this.getContentPane().add(tmpPanel, BorderLayout.NORTH);
        this.getContentPane().add(fp, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void generateDocument() {
        Namespace mpeg7 = Namespace.getNamespace("", "urn:mpeg:mpeg7:schema:2001");
        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        node = new Element("SemanticBase", mpeg7);
        node.setAttribute("type", types.getSelectedItem().toString(), xsi);

        Element label = new Element("Label", mpeg7).addContent(new Element("Name", mpeg7).addContent(gnf.getText()));
        node.addContent(label);

        Element definition = new Element("Definition", mpeg7);
        Element ta = new Element("FreeTextAnnotation", mpeg7);
        ta.addContent(fta.getText());
        definition.addContent(ta);

        node.addContent(definition);
    }

    public Element getNode() {
        return node;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("cancel")) {
            setVisible(false);
        } else if (e.getActionCommand().equals("ok")) {
            generateDocument();
            ArrayList v = new ArrayList();
            v.add(node);
            bde.addEntities(v);
            setVisible(false);
        }
    }

}
