package com.cannontech.dbeditor.wizard.season;

/**
 * Insert the type's description here.
 * Creation date: (6/22/2004 4:37:05 PM)
 * @author: 
 */
import com.cannontech.database.db.season.DateOfSeason;

public class SeasonDatesTableModel extends javax.swing.table.AbstractTableModel 
{
	public static final java.text.DateFormatSymbols DATE_SYMBOLS = new java.text.DateFormatSymbols();

	private java.util.Vector rows = null;

	public static final String[] COLUMNS = { "Name", "Start Date", "End Date" };
	public static final int COLUMN_NAME = 0;
	public static final int COLUMN_START_DATE = 1;
	public static final int COLUMN_END_DATE = 2;

/**
 * TriggerTableModel constructor comment.
 */
public SeasonDatesTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 4:57:45 PM)
 * @param type java.lang.String
 * @param point com.cannontech.database.data.lite.LitePoint
 * @param val java.lang.Object
 */
public void addRow( DateOfSeason dSeason )
{
	getRows().addElement( dSeason );

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
 * Creation date: (6/22/2004 10:25:01 AM)
 * @return java.lang.String
 */
public String getColumnName(int loc) 
{
	return COLUMNS[loc];
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 4:51:08 PM)
 * @param rowNumber int
 */
public DateOfSeason getRowAt(int rowNumber) 
{
	if( rowNumber >= 0 && rowNumber < getRowCount() )
		return (DateOfSeason)getRows().elementAt(rowNumber);
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
 * Creation date: (6/22/2004 4:39:14 PM)
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
			return getRowAt(row).getSeasonName();

		case COLUMN_START_DATE:
			String startDate = DATE_SYMBOLS.getMonths()[getRowAt(row).getSeasonStartMonth().intValue()] + " " + getRowAt(row).getSeasonStartDay().toString();
			return startDate;
				
		case COLUMN_END_DATE:
			String endDate = DATE_SYMBOLS.getMonths()[getRowAt(row).getSeasonEndMonth().intValue()] + " " + getRowAt(row).getSeasonEndDay().toString();
			return endDate;	
		
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
 * Creation date: (6/22/2004 4:57:45 PM)
 * @param type int
 */
public void removeRow( int rowNumber )
{
	getRows().remove( rowNumber );

	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 9:42:19 AM)
 * @param dSeason DateOfSeason
 */
public void setDateOfSeasonRow(DateOfSeason dSeason, int row)
{	
	if( row >= 0 && row < getRowCount() )
	{
		getRows().setElementAt( dSeason, row );

		fireTableDataChanged();
	}

}
}
