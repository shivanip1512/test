package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (5/7/2001 10:07:12 AM)
 * @author: 
 */
public interface AddRemoveJTableModel extends javax.swing.table.TableModel {
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 10:10:37 AM)
 * @param obj java.lang.Object
 */
void addRow(Object obj);
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 10:29:40 AM)
 * @return java.lang.Object
 * @param index int
 */
Object getRowAt(int index);
/**
 * Insert the type's description here.
 * Creation date: (5/14/2001 11:34:32 AM)
 * @author: 
 */
 /* This method returns the Class that may be inserted as the rows */
Class getRowClass();
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 10:25:11 AM)
 * @param index int
 */
void removeRow(int index);
}
