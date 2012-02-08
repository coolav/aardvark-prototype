/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.model;

import javax.swing.table.AbstractTableModel;
import java.util.Iterator;
import java.util.ArrayList;
import org.jdom.Element;
/**
 *
 * @author Olav
 */
public class SemanticEntityTableModel extends AbstractTableModel {
    private ArrayList objects;
//    Logger logger = Logger.getLogger(SemanticObjectTableModel.class);

    public ArrayList getObjects() {
        return objects;
    }

    /**
     * New TableModel with empty Vector
     */
    public SemanticEntityTableModel() {
        objects = new ArrayList();
    }

    public SemanticEntityTableModel(Element node) {
        objects = new ArrayList();
        objects.add(node);
    }

    public SemanticEntityTableModel(ArrayList agents) {
        this.objects = agents;
    }

    @Override
    public String getColumnName(int column) {
        return null;
    }

    public int getColumnCount() {
        return 1;
    }

    public int getRowCount() {
        return objects.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            String s = new String();
            Element e = (Element) objects.get(rowIndex);
            try {
                s = e.getChild("Label", e.getNamespace()).getChildText("Name", e.getNamespace());
                if (!(s != null)) {
                    s = e.getChild("Label", e.getNamespace()).getChildText("Definition", e.getNamespace());
                }
                // logger.debug("Requested Element @ (" + rowIndex + "," + columnIndex + "): " + s);
            } catch (Exception e1) {
//                logger.error(e + ": " + e1);
            }
            return s;
        } else
            return null;
    }

    public void addObject(Element node) {
        boolean exists = false;
        for (Iterator i = objects.iterator(); i.hasNext();) {
            Element elem = (Element) i.next();
            String str1, str2;
            str1 = elem.getChild("Label", elem.getNamespace()).getChild("Name", elem.getNamespace()).getTextTrim();
            str2 = node.getChild("Label", node.getNamespace()).getChild("Name", node.getNamespace()).getTextTrim();
            if (str1.equals(str2))
                exists = true;
        }
        if (!exists) objects.add(node);
    }

    public void addAllObjects(ArrayList objectsToAdd) {
        objects.addAll(objectsToAdd);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void detachAll() {
        for (Iterator i = objects.iterator(); i.hasNext();) {
            Element e = (Element) i.next();
            e.detach();
        }
    }

    public Element getNodeAt(int row) {
        return (Element) objects.get(row);
    }

    public void sort() {
        ArrayList tmpObjectsVector = new ArrayList();
        boolean hasElements = !objects.isEmpty();
        while (hasElements) {
            Element min = null;
            hasElements = false;
            for (Iterator i = objects.iterator(); i.hasNext();) {
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
                tmpObjectsVector.add(min);
                objects.remove(min);
            }
        }
        objects = tmpObjectsVector;
    }

}
