/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.model;

import org.jdom.Element;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 *
 * @author Olav
 */
public class RelationTableModel extends AbstractTableModel {
    private ArrayList agents;
//    Logger logger = Logger.getLogger(AgentTableModel.class);

    public ArrayList getAgents() {
        return agents;
    }

    /**
     * New TableModel with empty Vector
     */
    public RelationTableModel() {
        agents = new ArrayList();
    }

    public RelationTableModel(Element node) {
        agents = new ArrayList();
        agents.add(node);
    }

    public RelationTableModel(ArrayList agents) {
        this.agents = agents;
    }

    @Override
    public String getColumnName(int column) {
        return null;
    }

    public int getColumnCount() {
        return 1;
    }

    public int getRowCount() {
        return agents.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            String s = new String();
            Element e = (Element) agents.get(rowIndex);
            try {
                s = e.getChild("Label", e.getNamespace()).getChildText("Name", e.getNamespace());
                if (!(s != null)) {
                    s = e.getChild("Label", e.getNamespace()).getChildText("Definition", e.getNamespace());
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return s;
        } else
            return null;
    }

    public void addAgent(Element node) {
        boolean exists = false;
        for (Iterator i = agents.iterator(); i.hasNext();) {
            Element elem = (Element) i.next();
            String str1, str2;
            str1 = elem.getChild("Label", elem.getNamespace()).getChild("Name", elem.getNamespace()).getTextTrim();
            str2 = node.getChild("Label", node.getNamespace()).getChild("Name", node.getNamespace()).getTextTrim();
            if (str1.equals(str2))
                exists = true;
        }
        if (!exists) agents.add(node);
    }

    public void addAllAgents(ArrayList all) {
        agents.addAll(all);
    }
    

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void detachAll() {
        for (Iterator i = agents.iterator(); i.hasNext();) {
            Element e = (Element) i.next();
            e.detach();
        }
    }

    public Element getNodeAt(int row) {
        return (Element) agents.get(row);
    }

    public void sort() {
        ArrayList tmpAgents = new ArrayList();
        boolean hasElements = !agents.isEmpty();
        while (hasElements) {
            Element min = null;
            hasElements = false;
            for (Iterator i = agents.iterator(); i.hasNext();) {
                hasElements = true;
                Element e = (Element) i.next();
                if (!(min != null))
                    min = e;
                else {
                    String s1 = e.getChild("Label", e.getNamespace()).getChildText("Name", e.getNamespace());
                    String minStr = min.getChild("Label", min.getNamespace()).getChildText("Name", min.getNamespace());
                    if (s1.compareTo(minStr) < 0)
                        min = e;
                }
            }
            if (min != null) {
                tmpAgents.add(min);
                agents.remove(min);
            }
        }
        agents = tmpAgents;
    }

}
