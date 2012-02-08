/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.model;

import javax.swing.table.AbstractTableModel;
import java.util.*;
/**
 *
 * @author Olav
 */
public class Mpeg7RelationsTableModel extends AbstractTableModel {
    private String[] relations;


    public Mpeg7RelationsTableModel(String[] relations) {
        this.relations = relations;
        Arrays.sort(relations);
    }

    

    public String[] getRelations() {
        return relations;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public int getColumnCount() {
        return 1;
    }

    public int getRowCount() {
        return relations.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object returnVal = null;
        if (columnIndex < 1) {
            returnVal = relations[rowIndex];
        }
        return returnVal;
    }

    @Override
    public String getColumnName(int column) {
        return null;
    }
}
