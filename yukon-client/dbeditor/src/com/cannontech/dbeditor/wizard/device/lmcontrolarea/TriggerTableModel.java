package com.cannontech.dbeditor.wizard.device.lmcontrolarea;

/**
 * Insert the type's description here.
 * Creation date: (3/19/2001 4:37:05 PM)
 * @author: 
 */
public class TriggerTableModel extends javax.swing.table.AbstractTableModel 
{
	private java.util.Vector rows = null;

	public static final String[] COLUMNS = { "Type", "Point", "Value" };
	public static final int COLUMN_TYPE = 0;
	public static final int COLUMN_POINT = 1;
	public static final int COLUMN_VALUE = 2;
	
	public class TriggerRow
	{
		private String type = null;
		private com.cannontech.database.data.lite.LitePoint litePoint = null;
		private Object value = null;
		
		public TriggerRow(String triggerType, com.cannontech.database.data.lite.LitePoint point, Object triggerValue)
		{
			super();

			type = triggerType;
			litePoint = point;
			value = triggerValue;
		}

		public String getType()
		{
			return type;
		}

		public com.cannontech.database.data.lite.LitePoint getLitePoint()
		{
			return litePoint;
		}

		public Object getValue()
		{
			return value;
		}
	};
/**
 * TriggerTableModel constructor comment.
 */
public TriggerTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 4:57:45 PM)
 * @param type java.lang.String
 * @param point com.cannontech.database.data.lite.LitePoint
 * @param val java.lang.Object
 */
public void addRow(String type, com.cannontech.database.data.lite.LitePoint point, Object val) 
{
	TriggerRow row = new	TriggerRow( type, point, val );

	getRows().addElement( row );

	fireTableDataChanged();
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
public TriggerRow getRowAt(int rowNumber) 
{
	if( rowNumber >= 0 && rowNumber < getRowCount() )
		return (TriggerRow)getRows().elementAt(rowNumber);
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
private java.util.Vector getRows() 
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

	switch( col )
	{
		case COLUMN_TYPE:
		return getRowAt(row).getType();

		case COLUMN_POINT:
		return getRowAt(row).getLitePoint().getPointName();

		case COLUMN_VALUE:
		return getRowAt(row).getValue().toString();
	}
	
	return null;
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
}
