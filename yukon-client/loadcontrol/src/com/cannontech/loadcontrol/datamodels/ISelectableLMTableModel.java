package com.cannontech.loadcontrol.datamodels;

/**
 * Insert the type's description here.
 * Creation date: (1/2/2002 9:22:44 AM)
 * @author: 
 */
public interface ISelectableLMTableModel extends javax.swing.table.TableModel
{

/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:40:31 AM)
 */
void clear();
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 9:50:55 AM)
 * @param row int
 * @param col int
 */
java.awt.Color getCellBackgroundColor(int row, int col);
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 9:51:37 AM)
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
java.awt.Color getCellForegroundColor(int row, int col);
/**
 * getColumnCount method comment.
 */
int getColumnCount();
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param index int
 */
String getColumnName(int index);

/**
 * Insert the method's description here.
 * Creation date: (1/2/2002 9:23:28 AM)
 * @return int
 */
int getRowCount();
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 9:50:55 AM)
 * @param row int
 * @param col int
 */
java.awt.Color getSelectedRowColor(int row, int col);
}
