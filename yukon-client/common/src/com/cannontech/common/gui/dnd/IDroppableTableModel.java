package com.cannontech.common.gui.dnd;

/**
 * Insert the type's description here.
 * Creation date: (6/17/2002 3:16:17 PM)
 * @author: 
 */
public interface IDroppableTableModel {
/**
 * Insert the method's description here.
 * Creation date: (6/17/2002 3:16:41 PM)
 * @param ptId java.lang.String
 */
void insertNewRow(String ptId);
/**
 * Insert the method's description here.
 * Creation date: (6/17/2002 3:17:59 PM)
 * @return boolean
 * @param ptID java.lang.String
 */
boolean pointExists(String ptID);
}
