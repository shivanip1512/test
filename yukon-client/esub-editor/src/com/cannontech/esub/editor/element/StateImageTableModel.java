package com.cannontech.esub.editor.element;

import java.util.ArrayList;

import javax.swing.table.TableModel;
/**
 * Creation date: (1/21/2002 11:47:15 AM)
 * @author: 
 */
public class StateImageTableModel implements TableModel {

	Class[] columnClass = { String.class, String.class };
	String[] columnName = { "State", "Image" };
	
	//contains String[2] for each row
	ArrayList rows = new ArrayList();
/**
 * StateImageTableModel constructor comment.
 */
public StateImageTableModel() {
	super();
}
/**
 * Creation date: (1/21/2002 11:57:41 AM)
 * @param state java.lang.String
 * @param image java.lang.String
 */
public void addState(String state, String image) {
	String[] stateImage = new String[2];

	stateImage[0] = state;
	stateImage[1] = image;

	rows.add(stateImage);
}
	/**
	 * Add a listener to the list that's notified each time a change
	 * to the data model occurs.
	 *
	 * @param	l		the TableModelListener
	 */
public void addTableModelListener(javax.swing.event.TableModelListener l) {}
/**
 * Creation date: (1/21/2002 12:56:47 PM)
 */
public void clear() {
	rows = new ArrayList();
}
	/**
	 * Returns the lowest common denominator Class in the column.  This is used
	 * by the table to set up a default renderer and editor for the column.
	 *
	 * @return the common ancestor class of the object values in the model.
	 */
public Class getColumnClass(int columnIndex) {
	return columnClass[columnIndex];
}
	/**
	 * Returns the number of columns managed by the data source object. A
	 * <B>JTable</B> uses this method to determine how many columns it
	 * should create and display on initialization.
	 *
	 * @return the number or columns in the model
	 * @see #getRowCount
	 */
public int getColumnCount() {
	return columnClass.length;
}
	/**
	 * Returns the name of the column at <i>columnIndex</i>.  This is used
	 * to initialize the table's column header name.  Note, this name does
	 * not need to be unique.  Two columns on a table can have the same name.
	 *
	 * @param	columnIndex	the index of column
	 * @return  the name of the column
	 */
public String getColumnName(int columnIndex) {
	return columnName[columnIndex];
}
/**
 * Creation date: (1/21/2002 1:08:53 PM)
 * @return java.lang.String
 * @param state java.lang.String
 */
public String getImage(String state) {
	for( int i = 0; i < rows.size(); i++ ) {
		if( ((String[]) rows.get(i))[0].equalsIgnoreCase(state) ) {
			return ((String[]) rows.get(i))[1];
		}
	}

	return null;
}
	/**
	 * Returns the number of records managed by the data source object. A
	 * <B>JTable</B> uses this method to determine how many rows it
	 * should create and display.  This method should be quick, as it
	 * is call by <B>JTable</B> quite frequently.
	 *
	 * @return the number or rows in the model
	 * @see #getColumnCount
	 */
public int getRowCount() {
	return rows.size();
}
/**
 * Creation date: (1/21/2002 1:07:27 PM)
 * @return java.lang.String[]
 */
public String[] getStates() {
	String[] states = new String[rows.size()];
	for( int i = 0; i < rows.size(); i++ ) {
		states[i] = ((String[]) rows.get(i))[0];
	}

	return states;
}
	/**
	 * Returns an attribute value for the cell at <I>columnIndex</I>
	 * and <I>rowIndex</I>.
	 *
	 * @param	rowIndex	the row whose value is to be looked up
	 * @param	columnIndex 	the column whose value is to be looked up
	 * @return	the value Object at the specified cell
	 */
public Object getValueAt(int rowIndex, int columnIndex) {
	return ((String[]) rows.get(rowIndex))[columnIndex];
}
	/**
	 * Returns true if the cell at <I>rowIndex</I> and <I>columnIndex</I>
	 * is editable.  Otherwise, setValueAt() on the cell will not change
	 * the value of that cell.
	 *
	 * @param	rowIndex	the row whose value is to be looked up
	 * @param	columnIndex	the column whose value is to be looked up
	 * @return	true if the cell is editable.
	 * @see #setValueAt
	 */
public boolean isCellEditable(int rowIndex, int columnIndex) {
	return false;
}
	/**
	 * Remove a listener from the list that's notified each time a
	 * change to the data model occurs.
	 *
	 * @param	l		the TableModelListener
	 */
public void removeTableModelListener(javax.swing.event.TableModelListener l) {}
/**
 * Creation date: (1/21/2002 3:42:22 PM)
 * @param state java.lang.String
 * @param image java.lang.String
 */
public void setImage(String state, String image) {
	for(int i = 0; i < rows.size(); i++ ) {
		String[] row = (String[]) rows.get(i);
		if( row[0].equalsIgnoreCase(state) ) {
			row[1] = image;
		}
	}
}
	/**
	 * Sets an attribute value for the record in the cell at
	 * <I>columnIndex</I> and <I>rowIndex</I>.  <I>aValue</I> is
	 * the new value.
	 *
	 * @param	aValue		 the new value
	 * @param	rowIndex	 the row whose value is to be changed
	 * @param	columnIndex 	 the column whose value is to be changed
	 * @see #getValueAt
	 * @see #isCellEditable
	 */
public void setValueAt(Object aValue, int rowIndex, int columnIndex) 
{
	((String[]) rows.get(rowIndex))[columnIndex] = aValue.toString();
}
}
