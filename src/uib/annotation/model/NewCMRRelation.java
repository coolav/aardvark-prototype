/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.model;

import uib.annotation.util.DataExchange;
import uib.annotation.util.mpeg7.CountryCodeConverter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;
/**
 *
 * @author Olav
 */
public class NewCMRRelation extends JDialog implements ActionListener {
    Element node;
    private String gname, fname, freetext, country;
    private JPanel buttonPanel, gnp, fnp, cp, fp;
    private JTextField gnf, fnf;
    private JTextArea fta;
    private JButton ok, cancel;
    private JComboBox countryBox;
    private DataExchange bde;
    HashMap countries;


    public NewCMRRelation(DataExchange bde) throws HeadlessException {
        this.bde = bde;
        this.setTitle("Create a new agent-object");
        init();
    }

    private void init() {
        this.setSize(640, 480);
        countries = new HashMap();
        Vector cl = new Vector();
        try {
            Document d = new SAXBuilder().build(NewCMRRelation.class.getResource("countrycodes.xml"));
            Element root = d.getRootElement();
            java.util.List l = root.getChildren("country");
            for (Iterator i = l.iterator(); i.hasNext();) {
                Element e = (Element) i.next();
                String cName = CountryCodeConverter.toName(e.getChildTextTrim("name").toLowerCase());
                String cCode = e.getChildTextTrim("code").toLowerCase();
                countries.put(cName, cCode);
                cl.add(cName);
            }
        } catch (JDOMException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        countryBox = new JComboBox(cl);
        JPanel ctlp = new JPanel(new BorderLayout());
        ctlp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Country"));
        ctlp.add(new JLabel("Select country: "), BorderLayout.WEST);
        ctlp.add(countryBox, BorderLayout.CENTER);

        node = null;

        gnf = new JTextField(20);
        gnp = new JPanel(new BorderLayout());
        gnp.add(new JLabel("given name: "), BorderLayout.WEST);
        gnp.add(gnf, BorderLayout.CENTER);

        fnf = new JTextField(20);
        fnp = new JPanel(new BorderLayout());
        fnp.add(new JLabel("familyname: "), BorderLayout.WEST);
        fnp.add(fnf, BorderLayout.CENTER);

        ok = new JButton("ok");
        cancel = new JButton("cancel");
        ok.addActionListener(this);
        cancel.addActionListener(this);
        ok.setActionCommand("ok");
        cancel.setActionCommand("cancel");

        fta = new JTextArea(10, 20);
        fp = new JPanel(new BorderLayout());
        fp.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Description"));
        fp.add(new JScrollPane(fta), BorderLayout.CENTER);
        // fp.add(new JLabel("Description:"), BorderLayout.NORTH);
        buttonPanel = new JPanel();
        buttonPanel.add(ok);
        buttonPanel.add(cancel);

        JPanel tmpPanel = new JPanel(new BorderLayout());
        tmpPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Name"));
        tmpPanel.add(gnp, BorderLayout.NORTH);
        tmpPanel.add(fnp, BorderLayout.CENTER);
        JPanel tmpP2 = new JPanel(new BorderLayout());
        tmpP2.add(tmpPanel, BorderLayout.CENTER);
        tmpP2.add(ctlp, BorderLayout.SOUTH);


        this.getContentPane().add(tmpP2, BorderLayout.NORTH);
        this.getContentPane().add(fp, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void generateDocument() {
        Namespace mpeg7 = Namespace.getNamespace("", "urn:mpeg:mpeg7:schema:2001");
        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        node = new Element("SemanticBase", mpeg7);
        node.setAttribute("type", "AgentObjectType", xsi);

        Element label = new Element("Label", mpeg7).addContent(new Element("Name", mpeg7).addContent(gnf.getText() + " " + fnf.getText()));
        node.addContent(label);

        Element definition = new Element("Definition", mpeg7);
        Element ta = new Element("FreeTextAnnotation", mpeg7);
        ta.addContent(fta.getText());
        definition.addContent(ta);

        Element agent = new Element("Agent", mpeg7);
        agent.setAttribute("type", "PersonType", xsi);
        Element agname = new Element("Name", mpeg7);
        agent.addContent(agname);
        agname.addContent(new Element("GivenName", mpeg7).addContent(gnf.getText()));
        if (fnf.getText().length() > 1)
            agname.addContent(new Element("FamilyName", mpeg7).addContent(fnf.getText()));

        String s = (String) countries.get(countryBox.getSelectedItem());
        agent.addContent(new Element("Citizenship", mpeg7).addContent(s));

        node.addContent(definition);
        node.addContent(agent);
    }

    /**
     * Gives back the JDOM-Element which holds the MPEG-7 structure for the newly created AgentObject, if the user hits
     * the cancel button or closes the dialog no AgentObject will be created and <code>null</code> is returned
     *
     * @return <code>org.jdom.Element</code> if new Agent was created, <code>null</code> otherwise
     */
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
            System.out.println(new XMLOutputter(Format.getPrettyFormat()).outputString(node));
            bde.addRelation(v);
            setVisible(false);
        }
    }
}
