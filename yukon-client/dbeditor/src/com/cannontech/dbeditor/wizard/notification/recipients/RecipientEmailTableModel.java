package com.cannontech.dbeditor.wizard.notification.recipients;

/**
 * Insert the type's description here.
 * Creation date: (11/9/00 4:55:25 PM)
 * @author: 
 */
import java.util.Vector;
import com.cannontech.database.data.lite.LiteNotificationRecipient;

public class RecipientEmailTableModel extends javax.swing.table.AbstractTableModel 
{
	public final static int NAME_COLUMN = 0;
	public final static int TYPE_COLUMN = 1;
	public final static int ADDRESS_COLUMN = 2;
	private String[] COLUMN_NAMES = {"Name", "Type", "Address"};

	private Vector rows = null;

	private class RowValue
	{
		private LiteNotificationRecipient liteRecipient = null;
		
		public RowValue(LiteNotificationRecipient lite )
		{
			super();
			this.liteRecipient = lite;
		}

		public LiteNotificationRecipient getLiteRecipient()
		{  return liteRecipient; };
		
	}
	
/**
 * PointAlarmOptionsEditorTableModel constructor comment.
 */
public RecipientEmailTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 5:17:31 PM)
 * @param alarm java.lang.String
 * @param generate boolean
 * @param notify boolean
 */
public void addRowValue(com.cannontech.database.data.lite.LiteNotificationRecipient lite) 
{
	if( !rowExists(lite) )
		getRows().addElement( new RowValue(lite) );
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
public LiteNotificationRecipient getLiteRowRecipient(int row) 
{
	if( getRows() == null )
		return null;

	if( row <= getRows().size() )
	{
		return ((RowValue)getRows().elementAt(row)).getLiteRecipient();
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
	if( getRows() == null )
		return null;

	if( row <= getRows().size() )
	{
		RowValue rowVal = ((RowValue)getRows().elementAt(row));
		
		switch( col )
		{
		 	case NAME_COLUMN:
				return getLiteRowRecipient(row).getRecipientName();

		 	case TYPE_COLUMN:
				if( getLiteRowRecipient(row).getEmailSendType() == com.cannontech.database.db.notification.NotificationRecipient.PAGER_NOTIFYTYPE )
					return "Pager";
				else
					return "Email";
	
			case ADDRESS_COLUMN:
				return getLiteRowRecipient(row).getEmailAddress();
				
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
	return false;
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
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 5:07:51 PM)
 * @return boolean
 */
public boolean rowExists(LiteNotificationRecipient liteRec )
{
	for( int i = 0; i < getRowCount(); i++ )
	{
		if( getLiteRowRecipient(i).getRecipientName().equalsIgnoreCase(liteRec.getRecipientName()) )
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
public void setRowValue(int rowNumber, com.cannontech.database.data.lite.LiteNotificationRecipient lite) 
{
	if( rowNumber >=0 && rowNumber < getRowCount() )
	{
		getRows().setElementAt( new RowValue(lite), rowNumber );
	}
}
}
