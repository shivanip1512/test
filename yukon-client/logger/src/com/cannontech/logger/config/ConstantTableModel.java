package com.cannontech.logger.config;

/**
 * Insert the type's description here.
 * Creation date: (7/20/00 1:37:52 PM)
 * @author: 
 */
import java.util.ArrayList;
import java.util.Vector;

public class ConstantTableModel extends javax.swing.table.AbstractTableModel 
{

	private ArrayList readOnlyColumns = null;
	private Vector rows = null;
	private String columnNames[] = null;
/**
 * ConstantTableModel constructor comment.
 */
public ConstantTableModel() {
	super();
}
/**
 * ConstantTableModel constructor comment.
 */
public ConstantTableModel( String[] columns )
{
	super();

	columnNames = new String[ columns.length ];
	
	for( int i = 0; i < columns.length; i++ )
		columnNames[i] = columns[i];

}
/**
 * Insert the method's description here.
 * Creation date: (7/20/00 1:56:12 PM)
 * @return boolean
 * @param cellValues java.lang.Object[]
 */
public boolean addRow(Object[] cellValues) 
{
	if( cellValues.length != getColumnCount() )
		return false;

	getRowVector().addElement( cellValues );
	return true;
}
/**
 * getColumnClass method comment.
 */
public Class getColumnClass(int col)
{
	return String.class;	
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() 
{
	if( columnNames == null )
		return 0;
	else
		return columnNames.length;
}
/**
 * Insert the method's description here.
 * Creation date: (7/20/00 2:20:12 PM)
 * @return java.util.Vector
 */
public Object[] getColumnData( int colIndex )
{
	Object[] columns = new Object[getRowCount()];

	for( int i = 0; i < columns.length; i++ )
	{
		Object[] row = (Object[])getRowVector().elementAt(i);
		columns[i] = row[colIndex];
	}
	
	return columns;
}
/**
 * getColumnName method comment.
 */
public String getColumnName(int col)
{
	if( columnNames == null )
		return null;
	else
		return columnNames[col];
}
/**
 * getRowCount method comment.
 */
public int getRowCount() 
{
	return getRowVector().size();
}
/**
 * Insert the method's description here.
 * Creation date: (7/20/00 2:20:12 PM)
 * @return java.util.Vector
 */
public Object[] getRowData( int rowIndex )
{
	Object[] row = (Object[])getRowVector().elementAt(rowIndex);
	return row;
}
/**
 * Insert the method's description here.
 * Creation date: (7/20/00 2:20:12 PM)
 * @return java.util.Vector
 */
private Vector getRowVector() 
{
	if( rows == null )
		rows = new Vector(20);
		
	return rows;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int aRow, int aColumn) 
{
	Object[] row = (Object[])getRowVector().elementAt(aRow);
	return row[ aColumn ];
}
/* Returns the value of a certain ell
 * being editable or not
*/
public synchronized boolean isCellEditable(int row, int column) 
{
	if( readOnlyColumns == null )
		return false;
	
	if( readOnlyColumns.contains( String.valueOf(column) ) )		
		return false;
	else
		return true;

}
/**
 * Insert the method's description here.
 * Creation date: (7/21/00 12:51:03 PM)
 */
public void removeAllRows() 
{
	fireTableRowsDeleted( 0, getRowCount()-1 );
	getRowVector().removeAllElements();
}
/**
 * Insert the method's description here.
 * Creation date: (7/20/00 1:40:29 PM)
 * @param columnNumber int
 */
public synchronized void setReadOnlyColumns(int[] columnNumbers) 
{
	readOnlyColumns = new java.util.ArrayList( columnNumbers.length );
	
	for( int i = 0; i < columnNumbers.length; i++ )
	{
		readOnlyColumns.add( String.valueOf( columnNumbers[i] ) );
	}
}
/**
 * getValueAt method comment.
 */
public void setValueAt(Object value, int aRow, int aColumn) 
{
	Object[] row = (Object[])getRowVector().elementAt(aRow);
	row[ aColumn ] = value;
}
}
