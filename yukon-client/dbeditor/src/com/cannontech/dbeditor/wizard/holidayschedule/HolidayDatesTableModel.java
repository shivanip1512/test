package com.cannontech.dbeditor.wizard.holidayschedule;

/**
 * Insert the type's description here.
 * Creation date: (3/19/2001 4:37:05 PM)
 * @author: 
 */
import com.cannontech.database.db.holiday.DateOfHoliday;

public class HolidayDatesTableModel extends javax.swing.table.AbstractTableModel 
{
	public static final java.text.DateFormatSymbols DATE_SYMBOLS = new java.text.DateFormatSymbols();

	private java.util.Vector rows = null;

	public static final String[] COLUMNS = { "Name", "Month", "Day", "Year" };
	public static final int COLUMN_NAME = 0;
	public static final int COLUMN_MONTH = 1;
	public static final int COLUMN_DAY = 2;
	public static final int COLUMN_YEAR = 3;

/**
 * TriggerTableModel constructor comment.
 */
public HolidayDatesTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/19/2001 4:57:45 PM)
 * @param type java.lang.String
 * @param point com.cannontech.database.data.lite.LitePoint
 * @param val java.lang.Object
 */
public void addRow( DateOfHoliday dHoliday )
{
	getRows().addElement( dHoliday );

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
public DateOfHoliday getRowAt(int rowNumber) 
{
	if( rowNumber >= 0 && rowNumber < getRowCount() )
		return (DateOfHoliday)getRows().elementAt(rowNumber);
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
		case COLUMN_NAME:
		return getRowAt(row).getHolidayName();

		case COLUMN_MONTH:
		return DATE_SYMBOLS.getMonths()[getRowAt(row).getHolidayMonth().intValue() - 1];

		case COLUMN_DAY:
		return getRowAt(row).getHolidayDay();

		case COLUMN_YEAR:
		return ( getRowAt(row).getHolidayYear().intValue() < 0
					? com.cannontech.common.util.CtiUtilities.STRING_NONE
					: getRowAt(row).getHolidayYear().toString() );

	}
	
	return null;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param row int
 * @param column int
 */
public boolean isCellEditable(int row, int column) 
{
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
 * Creation date: (4/2/2001 9:42:19 AM)
 * @param dHoliday DateOfHoliday
 */
public void setDateOfHolidayRow(DateOfHoliday dHoliday, int row)
{	
	if( row >= 0 && row < getRowCount() )
	{
		getRows().setElementAt( dHoliday, row );

		fireTableDataChanged();
	}

}
}
