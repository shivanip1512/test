package com.cannontech.common.util;

import java.util.Vector;

/**
 * Insert the type's description here.
 * Creation date: (10/23/2001 9:10:18 AM)
 * @author: 
 */
public class KeysAndValuesTableModel extends javax.swing.table.AbstractTableModel
{
	public final static int COMMON_COMMAND_COLUMN = 0;
	public final static int EXECUTE_COMMAND_COLUMN = 1;
	
	public static String[] columnNames =
	{
		"Common Command",
		"Execute Command"
	};

	public static Class[] columnTypes =
	{
		String.class,
		String.class
	};	

	//KeyAndValue rows
	public Vector rows = new Vector(10);	//KeysAndValues.
	
/**
 * KeysAndValuesTableModel constructor comment.
 */
public KeysAndValuesTableModel()
{
	super();
}

/**
 * Adds the keyAndValue to model.rows array
  * Creation date: (10/25/00 11:21:21 AM)
 */
public void addRow(KeyAndValue keyAndValue) 
{
	// always insert at the front
	getRows().add(0, keyAndValue);
	fireTableDataChanged();
}

/**
 * Insert the method's description here.
 * Creation date: (2/6/2001 11:03:39 AM)
 * @return 
 */
public KeysAndValues getKeysAndValues()
{
	KeysAndValues returnKAV = new KeysAndValues();

	java.util.Iterator iter = getRows().iterator();

	int i = 0;
	while( iter.hasNext() )
	{
		returnKAV.getKeysAndValues().add( (KeyAndValue)iter.next());
	}

	return returnKAV;
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/00 1:19:28 PM)
 * @return java.lang.Class
 * @param column int
 */
public Class getColumnClass(int column)
{
	return getColumnTypes()[column];
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() 
{
	return getColumnNames().length;
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/00 2:54:48 PM)
 * @return java.lang.String
 * @param col int
 */
public String getColumnName(int col) {
	return getColumnNames()[col];
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 4:10:08 PM)
 * @return java.lang.Class[]
 */
public String[] getColumnNames()
{
	return columnNames;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 4:10:08 PM)
 * @return java.lang.Class[]
 */
public Class[] getColumnTypes()
{
	return columnTypes;
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/00 11:26:55 AM)
 * @return java.lang.Object
 * @param index int
 */
public Object getKeyAndValueAttribute(int index, KeyAndValue keyAndValue_) {
	switch( index )
	{
		case COMMON_COMMAND_COLUMN:
			return keyAndValue_.getKey();
			
		case EXECUTE_COMMAND_COLUMN:
			return keyAndValue_.getValue();
	}
	return null;
}

/**
 * Insert the method's description here.
 * Creation date: (10/25/00 11:21:02 AM)
 * @return KeyAndValue
 * @param row int
 */
public KeyAndValue getRow(int row)
{
	return ( row < getRows().size() ? (KeyAndValue) getRows().get(row) : null );
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
 * Creation date: (10/23/2001 4:05:03 PM)
 * @return java.util.ArrayList
 */
public Vector getRows()
{
	return rows;
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col)
{
	if( row < getRows().size() && col < getColumnCount() )
	{
		return getKeyAndValueAttribute(col, (KeyAndValue) getRows().get(row) );
	}
	else
		return null;
			
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 9:26:48 AM)
 * @return boolean
 * @param row int
 * @param column int
 */
public boolean isCellEditable(int row, int column)
{
	return ( column == COMMON_COMMAND_COLUMN ||
			 column == EXECUTE_COMMAND_COLUMN );
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/00 4:42:04 PM)
 * @param row int
 */
public void removeRow(int[] row) 
{
	Object[] toRemove = new Object[row.length]; 
	
	for( int i = 0; i < row.length; i++ )
		toRemove[i] = getRows().get( row[i] );

	for( int i = 0; i < toRemove.length; i++ )
		getRows().remove(toRemove[i]);

	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/00 4:42:04 PM)
 * @param row int
 */
public void removeRow(int row) 
{
	if( row < getRows().size() )
		getRows().remove(row);

	fireTableDataChanged();
}

/**
 * Insert the method's description here.
 * Creation date: (11/6/00 10:07:31 AM)
 * @param value java.lang.Object
 * @param row int
 * @param col int
 */
public void setValueAt(Object value, int row, int col) 
{	
	KeyAndValue kav = getRow(row);
	if( kav == null)
		kav = new KeyAndValue();
	String valueString = value.toString();	

	switch( col )
	{
		case COMMON_COMMAND_COLUMN:
			kav.setKey( valueString );
			break;
		case EXECUTE_COMMAND_COLUMN:
			kav.setValue(valueString);
			break;
	}
	fireTableRowsUpdated(row, row);	
}

}
