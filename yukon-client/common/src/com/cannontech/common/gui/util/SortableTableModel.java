package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (4/8/2002 10:05:09 AM)
 * @author: 
 */
public interface SortableTableModel extends javax.swing.table.TableModel
{
/**
 * Insert the method's description here.
 * Creation date: (4/8/2002 10:07:38 AM)
 * @return boolean
 * @param rowNumber int
 */
boolean isRowSelectedBlank(int rowNumber);
/**
 * Insert the method's description here.
 * Creation date: (4/8/2002 10:08:34 AM)
 * @param oldRow int
 * @param newRow int
 */
void rowDataSwap(int oldRow, int newRow);
}
