package com.cannontech.common.gui.util;

import java.util.*;
import javax.swing.table.AbstractTableModel;

import com.cannontech.database.data.device.lm.LMxmlParameter;

public class XmlParameterTableModel extends AbstractTableModel
{
    
    private Vector<String> columnNames = new Vector<String>();  
    private Vector<LMxmlParameter> rows = new Vector<LMxmlParameter>();
    
    public XmlParameterTableModel() {
        super();
    }
    
    public void addRow(LMxmlParameter newRow) {
        rows.add(newRow);
        fireTableRowsInserted(rows.size()-1,rows.size()-1);
    }

    public void addRows(List<LMxmlParameter> newRows) {
        rows.addAll(newRows);
        fireTableRowsInserted(rows.size()-1,rows.size()-1);
    }
    
    public void editRow(int position, LMxmlParameter newRow) {
        rows.remove(position);
        rows.add(position, newRow);
        fireTableRowsUpdated(position, position);
    }
    
    public int getColumnCount() {
        return columnNames.size();
    }

    public String getColumnName(int column) {
        return columnNames.elementAt(column).toString();
    }

    public int getRowCount() {
        return rows.size();
    }
    
    public LMxmlParameter getRow( int aRow ) {
        LMxmlParameter row = rows.elementAt(aRow);
        return row;
    }
    
    public List<LMxmlParameter> getRows() {
        return new ArrayList<LMxmlParameter>(rows);
    }
    
    @Override
    public Object getValueAt(int aRow, int aColumn) {
        LMxmlParameter param = rows.elementAt(aRow);
        
        String ret = "---";
        
        if (aColumn == 1) {
            ret = param.getParameterValue();
        }
        if (aColumn == 0) {
            ret = param.getParameterName();
        }
        
        return ret;
    }

    private void initColumns()  {   
        rows.clear();
        columnNames.clear();
        
        columnNames.add("Parameter");
        columnNames.add("Value");

        fireTableStructureChanged(); // Tell the listeners a new table has arrived.
    }
    
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void makeTable() {
        initColumns();
    }

    public void removeRow(int position) {
        rows.remove(position);
        fireTableRowsDeleted(position, position);
    }

    public void reset() {
        columnNames.clear();
        rows.clear();
        fireTableChanged(null);
    }    
}