package com.cannontech.loadcontrol.datamodels;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;

public class ControlAreaTriggerTableModel extends javax.swing.table.AbstractTableModel
{
	private java.util.Vector rows = null;
/**
 * ScheduleTableModel constructor comment.
 */
public ControlAreaTriggerTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:40:31 AM)
 */
public void addRow( ControlAreaRowData row ) 
{
	if( row != null )
	{
		getRows().add( row );

		fireTableDataChanged();
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:40:31 AM)
 */
public void clear() 
{
	getRows().removeAllElements();

	fireTableDataChanged();
}
/**
 * This method was created in VisualAge.
 * @param event TableModelEvent
 */
public void fireTableChanged(javax.swing.event.TableModelEvent event) {
	super.fireTableChanged(event);
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellForegroundColor(int row, int col) 
{
	if( getRowAt(row).isFiring() )
		return Color.red;
	else
		return Color.black;
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return 1;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param index int
 */
public String getColumnName(int index) {
	return "Triggers";
}
/**
 * This method returns the value of a row in the form of a LMProgramBase object.
 */
public ControlAreaRowData getRowAt(int rowIndex) 
{
	if( rowIndex < 0 )
		return null;

	return (ControlAreaRowData)getRows().get(rowIndex);
}
/**
 * getRowCount method comment.
 */
public int getRowCount() 
{
	return getRows().size();
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/2001 11:48:54 AM)
 * @return java.util.Vector
 */
private java.util.Vector getRows() 
{
	if( rows == null )
		rows = new java.util.Vector(3);

	return rows;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) 
{
	if( row <= getRowCount() )
	{
		switch( col )
		{
		 	case 0:
			default:
				return getRowAt(row).toString();
		}
				
	}
	else
		return null;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param row int
 * @param column int
 */
public boolean isCellEditable(int row, int column) {
	return false;
}
}
