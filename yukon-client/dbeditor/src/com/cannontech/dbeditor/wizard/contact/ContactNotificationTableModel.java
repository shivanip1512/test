package com.cannontech.dbeditor.wizard.contact;

/**
 * Insert the type's description here.
 * Creation date: (11/9/00 4:55:25 PM)
 * @author: 
 */
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.database.cache.functions.YukonListFuncs;
import com.cannontech.database.db.contact.ContactNotification;

public class ContactNotificationTableModel extends javax.swing.table.AbstractTableModel 
{
	public final static int COLUMN_NOTIFICATION = 0;
	public final static int COLUMN_TYPE= 1;
	public final static int COLUMN_DISABLED = 2;

	private String[] COLUMN_NAMES = 
	{
		"Notification",
		"Type",
		"Disabled" 
	};


	//contians com.cannontech.database.db.contact.ContactNotification
	private Vector rows = null;

	private class RowValue
	{
		private ContactNotification cntNotif = null;
		
		public RowValue(ContactNotification cntNotif_ )
		{
			super();
			this.cntNotif = cntNotif_;
		}

		public ContactNotification getContactNotification()
		{  return cntNotif; };
		
	}
	
/**
 * PointAlarmOptionsEditorTableModel constructor comment.
 */
public ContactNotificationTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 5:17:31 PM)
 * @param alarm java.lang.String
 * @param generate boolean
 * @param notify boolean
 */
public void addRowValue(ContactNotification cntNotif) 
{
	if( !rowExists(cntNotif) )
	{
		getRows().add( new RowValue(cntNotif) );
		fireTableDataChanged();
	}
	
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return COLUMN_NAMES.length;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param index int
 */
public String getColumnName(int index) {
	return COLUMN_NAMES[index];
}
/**
 * Insert the method's description here.
 * Creation date: (11/28/00 2:20:00 PM)
 */
public ContactNotification getContactNotificationRow(int row) 
{

	if( row < getRows().size() )
	{
		return ((RowValue)getRows().elementAt(row)).getContactNotification();
	}
	else
		return null;
	
}
/**
 * getRowCount method comment.
 */
public int getRowCount() {
	return getRows().size();
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 5:07:51 PM)
 * @return java.util.Vector
 */
private java.util.Vector getRows() 
{
	if( rows == null )
		rows = new Vector(10);
		
	return rows;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) 
{

	if( row < getRows().size() )
	{
		ContactNotification cntNotif = getContactNotificationRow(row);
		
		switch( col )
		{
		 	case COLUMN_NOTIFICATION:
				return cntNotif.getNotification();

		 	case COLUMN_TYPE:
				return YukonListFuncs.getYukonListEntry(
						cntNotif.getNotificationCatID().intValue());

		 	case COLUMN_DISABLED:
				return cntNotif.getDisableFlag();
	
			default:
				return null;
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
public boolean isCellEditable(int row, int column) 
{
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 5:17:31 PM)
 * @param rowNumber int
 */
public void removeRowValue(int rowNumber )
{
	if( rowNumber >=0 && rowNumber < getRowCount() )
	{
		getRows().removeElementAt( rowNumber );
		fireTableDataChanged();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 5:07:51 PM)
 * @return boolean
 */
public boolean rowExists(ContactNotification cntNotif )
{
	for( int i = 0; i < getRowCount(); i++ )
	{
		if( getContactNotificationRow(i).toString().equalsIgnoreCase(cntNotif.toString()) )
		{
			return true;
		}
	}

	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 5:17:31 PM)
 * @param rowNumber int
 * @param alarm java.lang.String
 * @param generate boolean
 * @param notify boolean
 */
public void setRowValue(int rowNumber, ContactNotification cntNotif) 
{
	if( rowNumber >=0 && rowNumber < getRowCount() )
	{
		getRows().setElementAt( new RowValue(cntNotif), rowNumber );
		fireTableDataChanged();
	}
}

public void setValueAt(Object value, int row, int col) 
{
	if( row < getRows().size() && col < getColumnCount() )
	{
		ContactNotification cntNotif = getContactNotificationRow(row);
				
		switch( col )
		{
			case COLUMN_NOTIFICATION:
				cntNotif.setNotification( value.toString() );
				break;
				
			case COLUMN_TYPE:
				cntNotif.setNotificationCatID(
					new Integer(((YukonListEntry)value).getEntryID()) );
//					YukonListFuncs.getYukonListEntry(
//						cntNotif.getNotificationCatID().intValue()).getEntryText();
				break;
					
			case COLUMN_DISABLED:
				cntNotif.setDisableFlag( value.toString() );
				break;
				
			default:
				CTILogger.info(this.getClass() + " tried to set value for an invalid column, column number " + col );
		}
	}
}

}
