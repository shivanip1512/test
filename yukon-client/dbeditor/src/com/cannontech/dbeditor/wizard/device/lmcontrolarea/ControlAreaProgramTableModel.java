package com.cannontech.dbeditor.wizard.device.lmcontrolarea;

import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Insert the type's description here.
 * Creation date: (3/19/2001 4:37:05 PM)
 * @author: 
 */
public class ControlAreaProgramTableModel extends javax.swing.table.AbstractTableModel 
{
	private java.util.Vector rows = null;

	public static final String[] COLUMNS = { "Program", "Stop Priority", "Start Priority" };
	public static final int COLUMN_PROGRAM = 0;
	public static final int COLUMN_STOP = 1;
	public static final int COLUMN_START = 2;
	
	public class ProgramRow
	{
		private com.cannontech.database.db.device.lm.LMControlAreaProgram programList = null;
		private com.cannontech.database.data.lite.LiteYukonPAObject liteDevice = null;
		
		public ProgramRow(com.cannontech.database.db.device.lm.LMControlAreaProgram list, com.cannontech.database.data.lite.LiteYukonPAObject device)
		{
			super();

			programList = list;
			liteDevice = device;
		}

		public com.cannontech.database.db.device.lm.LMControlAreaProgram getProgramList()
		{
			return programList;
		}

		public com.cannontech.database.data.lite.LiteYukonPAObject getLiteDevice()
		{
			return liteDevice;
		}

	};
/**
 * TriggerTableModel constructor comment.
 */
public ControlAreaProgramTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 4:57:45 PM)
 * @param type java.lang.String
 * @param point com.cannontech.database.data.lite.LitePoint
 * @param val java.lang.Object
 */
public boolean addRow(com.cannontech.database.db.device.lm.LMControlAreaProgram list, com.cannontech.database.data.lite.LiteYukonPAObject device)
{
	for( int i = 0; i < getRowCount(); i++ )
		if( getRowAt(i).getLiteDevice().getYukonID() == device.getYukonID() )
			return false; //row already exists, did not insert the new one
	
	ProgramRow row = new ProgramRow( list, device );
	getRows().add(row);
	
	fireTableDataChanged();
	return true;
}
/**
 *  Returns the class for the column
 */
public Class getColumnClass(int col) 
{

	if( getRowCount() > 0 )
	{
		ProgramRow row = getRowAt(0);
		
		switch( col )
		{
			case COLUMN_PROGRAM:
			return row.getLiteDevice().getPaoName().getClass();

			case COLUMN_STOP:
			return row.getProgramList().getStopPriority().getClass();

			case COLUMN_START:
			return row.getProgramList().getStartPriority().getClass();
		}
	}

	return Object.class;
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
public ProgramRow getRowAt(int rowNumber) 
{
	if( rowNumber >= 0 && rowNumber < getRowCount() )
		return (ProgramRow)getRows().elementAt(rowNumber);
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
		case COLUMN_PROGRAM:
		return getRowAt(row).getLiteDevice().getPaoName();

		case COLUMN_STOP:
		return getRowAt(row).getProgramList().getStopPriority();

		case COLUMN_START:
		return getRowAt(row).getProgramList().getStartPriority();
	
	}
	
	return null;
}
/**
 */
public boolean isCellEditable(int rowIndex, int columnIndex) 
{
	return ( columnIndex == COLUMN_START 
			 	|| columnIndex == COLUMN_STOP );

}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 4:57:45 PM)
 * @param type int
 */
public LiteYukonPAObject removeRow( int rowNumber )
{
	LiteYukonPAObject removed = getRowAt(rowNumber).getLiteDevice(); 
	getRows().remove( rowNumber );

	fireTableDataChanged();
	
	return removed;
}
/**
 *  What to store when a cell is edited.
 */
public void setValueAt(Object aValue, int row, int col) 
{
	if( row >= getRowCount() || col >= getColumnCount() )
		return;

	Integer val = new Integer(0);
	try
	{
		val = new Integer( Integer.parseInt( aValue.toString() ) );
	}
	catch( NumberFormatException e )
	{}

	switch( col )
	{
		case COLUMN_STOP:
			getRowAt(row).getProgramList().setStopPriority( val );
			break;

		case COLUMN_START:
			getRowAt(row).getProgramList().setStartPriority( val );
			break;
	}

}
}
