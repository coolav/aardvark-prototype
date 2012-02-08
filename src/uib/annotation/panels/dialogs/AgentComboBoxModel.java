/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.panels.dialogs;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.List;
import uib.annotation.util.AnnotationToolkit;

/**
 *
 * @author Olav
 */
public class AgentComboBoxModel  extends DefaultComboBoxModel {
    private static Vector agents;
    private static Vector instances;
    private static Document agentsDoc = null;
    private static boolean canCreateNew = true;
    private JFrame parent;

    static {
        instances = new Vector();
        agents = new Vector();
        agentsDoc = null;
    }

    public AgentComboBoxModel(JFrame parent) {
        this.parent = parent;
        init();
    }

    private void init() {
        instances.add(this);
        if (!(agentsDoc != null)) {
            try {
                agentsDoc = new SAXBuilder().build(AnnotationToolkit.AGENTS_FILE);
                agents.addAll(AnnotationToolkit.xpathQuery(agentsDoc.getRootElement(), "//Agent", null));
            } catch (JDOMException e) {
                System.out.println("[at.lux.fotoannotation.AgentComboBoxModel] Error reading agents from disk: " + e.toString());
                e.printStackTrace(System.err);
            } catch (IOException e) {
                System.out.println("[at.lux.fotoannotation.AgentComboBoxModel] Error reading agents from disk: " + e.toString());
                e.printStackTrace(System.err);
            }
        }
        sortAgents();
        for (Iterator i = agents.iterator(); i.hasNext();) {
            Element e = (Element) i.next();
            String gname = e.getChild("Name", e.getNamespace()).getChildTextTrim("GivenName", e.getNamespace());
            String fname = e.getChild("Name", e.getNamespace()).getChildTextTrim("FamilyName", e.getNamespace());
            String label = fname + ", " + gname;
            this.addElement(label);
        }
        this.addElement("New person ...");
    }

    public static Vector getAgents() {
        return agents;
    }

    public void createNewAgent() {
        if (canCreateNew) {
            debug("starting NewAgentDialog");
            NewAgentDialog d = new NewAgentDialog(this, parent);
            d.pack();
            Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();

            d.setLocation((ss.width - d.getWidth()) / 2, (ss.height - d.getHeight()) / 2);
            d.setVisible(true);
        } else {
            debug("canCreateNew set to false :)");
        }
    }

    public void addAgent(Element e) {
        canCreateNew = false;
        Namespace mpeg7 = Namespace.getNamespace("", "urn:mpeg:mpeg7:schema:2001");
        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        String gname = e.getChild("Name", e.getNamespace()).getChildTextTrim("GivenName", e.getNamespace());
        String fname = e.getChild("Name", e.getNamespace()).getChildTextTrim("FamilyName", e.getNamespace());
        String label = fname + ", " + gname;
        this.removeElementAt(this.getSize() - 1);
        this.addElement(label);
        this.addElement("New agent ...");

        Element semBase = new Element("SemanticBase", mpeg7);
        semBase.setAttribute("type", "AgentObjectType", xsi);
        semBase.addContent(new Element("Label", mpeg7).addContent(new Element("Name", mpeg7).addContent(label)));

        List results = AnnotationToolkit.xpathQuery(agentsDoc.getRootElement(), "//Semantics", null);
        if (results.size() > 0) {
            ((Element) results.get(0)).addContent(semBase.addContent(e.detach()));
        }
        agents.addElement(e);
        XMLOutputter op = new XMLOutputter(Format.getPrettyFormat());
        try {
            FileOutputStream fos = new FileOutputStream(AnnotationToolkit.AGENTS_FILE);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            op.output(agentsDoc, osw);
            osw.close();
            fos.close();
        } catch (IOException e1) {
            debug("Error saving agents: " + e1.toString());
            e1.printStackTrace(System.err);
        }
        this.fireContentsChanged(this, 0, this.getSize());
        // Damit der neue Agent auch gleich ausgewählt ist :)
        this.addElement(label);
        this.setSelectedItem(label);
        for (Iterator i = instances.iterator(); i.hasNext();) {
            AgentComboBoxModel model = (AgentComboBoxModel) i.next();
            model.reReadAgents();
        }
        canCreateNew = true;
    }

    public void deleteAgent(String label) {
        canCreateNew = false;
        Element _toDelete = null;
        for (Iterator i = agents.iterator(); i.hasNext();) {
            Element e = (Element) i.next();
            String _gname = e.getChild("Name", e.getNamespace()).getChildTextTrim("GivenName", e.getNamespace());
            String _fname = e.getChild("Name", e.getNamespace()).getChildTextTrim("FamilyName", e.getNamespace());
            String _label = _fname + ", " + _gname;
            if (label.equals(_label)) {
                _toDelete = e;
            }
        }
        if (_toDelete != null) {
            agents.remove(_toDelete);
            _toDelete.detach();
            XMLOutputter op = new XMLOutputter(Format.getPrettyFormat());
            try {
                FileOutputStream fos = new FileOutputStream(AnnotationToolkit.AGENTS_FILE);
                OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
                op.output(agentsDoc, osw);
                osw.close();
                fos.close();
            } catch (IOException e1) {
                debug("Error saving agents: " + e1.toString());
                e1.printStackTrace(System.err);
            }
        }
        this.fireContentsChanged(this, 0, this.getSize());
        for (Iterator iterator = instances.iterator(); iterator.hasNext();) {
            AgentComboBoxModel model = (AgentComboBoxModel) iterator.next();
            model.reReadAgents();
        }
        canCreateNew = true;
    }

    public void reReadAgents() {
        String s = this.getSelectedItem().toString();
        debug("ReRead starts : " + s + " was selected");
        Object merker = null;
        this.removeAllElements();
        sortAgents();
        for (Iterator i = agents.iterator(); i.hasNext();) {
            Element e = (Element) i.next();
            String gname = e.getChild("Name", e.getNamespace()).getChildTextTrim("GivenName", e.getNamespace());
            String fname = e.getChild("Name", e.getNamespace()).getChildTextTrim("FamilyName", e.getNamespace());
            String label = fname + ", " + gname;
            if (label.equals(s)) merker = (Object) label;
            this.addElement(label);
        }
        this.addElement("New Agent ...");
        if (merker != null) {
            this.setSelectedItem(merker);
        } else {
            this.setSelectedItem(getElementAt(0));
            debug("merker was null!");
        }
        this.fireContentsChanged(this, 0, this.getSize());
    }

    private void sortAgents() {
        Collections.sort(agents, new Comparator() {
            public int compare(Object o1, Object o2) {
                Element e1 = (Element) o1;
                String g1name = e1.getChild("Name", e1.getNamespace()).getChildTextTrim("GivenName", e1.getNamespace());
                String f1name = e1.getChild("Name", e1.getNamespace()).getChildTextTrim("FamilyName", e1.getNamespace());
                String l1 = (f1name.toLowerCase() + ", " + g1name.toLowerCase());
                Element e2 = (Element) o2;
                String g2name = e2.getChild("Name", e2.getNamespace()).getChildTextTrim("GivenName", e2.getNamespace());
                String f2name = e2.getChild("Name", e2.getNamespace()).getChildTextTrim("FamilyName", e2.getNamespace());
                String l2 = (f2name.toLowerCase() + ", " + g2name.toLowerCase());
                return l1.compareTo(l2);
            }
        });
    }

    private void debug(String message) {
        //if (AnnotationFrame.DEBUG) System.out.println("[at.lux.fotoannotation.AgentComboBoxModel] " + message);
    }

}
