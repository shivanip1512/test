package com.cannontech.dbeditor.editor.defaults;

/**
 * Insert the type's description here.
 * Creation date: (6/28/2002 1:22:43 PM)
 * @author: 
 */
public class DefaultRoutesTableModel extends javax.swing.table.AbstractTableModel implements javax.swing.event.TableModelListener {


	private java.util.Vector rows = null;

	public static final String[] COLUMNS = { "Route Name", "Current", "Recommended", "Transmitter" };
	public static final int TRANSMITTER = 3;
	public static final int ROUTE_NAME = 0;
	public static final int CURRENT_DEFAULT = 1;
	public static final int RECOMMENDED_DEFAULT = 2;

/**
 * TriggerTableModel constructor comment.
 */
public DefaultRoutesTableModel() {
	super();
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 4:57:45 PM)
 * @param type java.lang.String
 * @param point com.cti.data.lite.LitePoint
 * @param val java.lang.Object
 */
public void addRow(java.util.Vector row )
{
	getRows().addElement( row );

	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (7/1/2002 9:49:50 AM)
 * @return java.lang.Class
 * @param c int
 */
public Class getColumnClass(int c) {
	 return getValueAt(0, c).getClass();

}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() 
{
	return COLUMNS.length;
}
/**
 * Insert the method's description here.
 * Creation date: (3/21/2001 10:25:01 AM)
 * @return java.lang.String
 */
public String getColumnName(int loc) 
{
	return COLUMNS[loc];
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 4:51:08 PM)
 * @param rowNumber int
 */
public java.util.Vector getRowAt(int rowNumber) 
{
	if( rowNumber >= 0 && rowNumber < getRowCount() )
		return (java.util.Vector) getRows().elementAt(rowNumber);
	else
		return null;
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
 * Creation date: (3/19/2001 4:39:14 PM)
 * @return java.util.Vector
 */
public java.util.Vector getRows() 
{
	if( rows == null )
		rows = new java.util.Vector(10);
		
	return rows;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) 
{
	if( row >= getRowCount() || col >= getColumnCount() )
		return null;
	
	
	
	else
	  return ((java.util.Vector) rows.get(row)).get(col);
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param row int
 * @param column int
 */
public boolean isCellEditable(int row, int column) 
{
	if (column == RECOMMENDED_DEFAULT)
	return true;
		else 
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 4:57:45 PM)
 * @param type int
 */
public void removeRow( int rowNumber )
{
	getRows().remove( rowNumber );

	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (7/1/2002 4:14:22 PM)
 * @param o java.lang.Object
 * @param row int
 * @param col int
 */
public void setValueAt(Object value, int row, int col) {
	
	java.util.Vector r = getRowAt(row);
	Object oldValue = r.get(col);

	if (value.toString().equalsIgnoreCase("Y") || value.toString().equalsIgnoreCase("N"))
		r.set(col,value);
	else
	    r.set(col,oldValue);
}
/**
 * Insert the method's description here.
 * Creation date: (7/1/2002 3:55:18 PM)
 * @param e javax.swing.event.TableModelEvent
 */
public void tableChanged(javax.swing.event.TableModelEvent e) {
	
	fireTableDataChanged();
	
	}
}
