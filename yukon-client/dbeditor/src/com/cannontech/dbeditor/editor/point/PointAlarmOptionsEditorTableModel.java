package com.cannontech.dbeditor.editor.point;

/**
 * Insert the type's description here.
 * Creation date: (11/9/00 4:55:25 PM)
 * @author: 
 */
import java.util.Vector;

public class PointAlarmOptionsEditorTableModel extends javax.swing.table.AbstractTableModel 
{
	public final static int CONDITION_COLUMN = 0;
	public final static int CATEGORY_COLUMN = 1;
	public final static int NOTIFY_COLUMN = 2;
	
	private String[] COLUMN_NAMES = 
	{
		"Condition", 
		"Category", 
		"Notify"
	};

	private Class[] COLUMN_CLASSES = {String.class, String.class, String.class};
	
	private Vector rows = null;

	private class RowValue
	{
		private String alarm = null;
		private String generate = null;
		private String excludeNotify = null;
		
		public RowValue(String alarm, String generate, String notify )
		{
			super();
			this.alarm = alarm;
			this.generate = generate;
			this.excludeNotify = notify;
		}

		// All getters
		public String getAlarm()
		{ return alarm; }

		public String getGenerate()
		{ return generate; }
		
		public String getExcludeNotify()
		{ return excludeNotify; }


		// All setters
		public void setAlarm(String val)
		{ alarm = val; }

		public void setGenerate(String val)
		{ generate = val; }

		public void setExcludeNotify(String val)
		{ excludeNotify = val; }
		
	}
	
/**
 * PointAlarmOptionsEditorTableModel constructor comment.
 */
public PointAlarmOptionsEditorTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 5:17:31 PM)
 * @param alarm java.lang.String
 * @param generate boolean
 * @param notify boolean
 */
public void addRowValue(String alarm, String generate, String notify) 
{
	getRows().addElement( new RowValue(alarm, generate, notify) );
}
/**
 * getValueAt method comment.
 */
public String getAlarmAt(int row ) 
{
	if( getRows() == null )
		return null;

	if( row <= getRows().size() )
	{
		return ((RowValue)getRows().elementAt(row)).getAlarm();		
	}
	else
		return null;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param index int
 */
public Class getColumnClass(int index) {
	return COLUMN_CLASSES[index];
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
 * getValueAt method comment.
 */
public String getExcludeNotifyAt(int row ) 
{
	if( getRows() == null )
		return null;

	if( row <= getRows().size() )
	{
		return ((RowValue)getRows().elementAt(row)).getExcludeNotify();
	}
	else
		return null;
}
/**
 * getValueAt method comment.
 */
public String getGenerateAt(int row ) 
{
	if( getRows() == null )
		return null;

	if( row <= getRows().size() )
	{
		return ((RowValue)getRows().elementAt(row)).getGenerate();
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
		 	case CONDITION_COLUMN:
				return rowVal.getAlarm();

		 	case CATEGORY_COLUMN:
				return rowVal.getGenerate();
	
			case NOTIFY_COLUMN:
				return rowVal.getExcludeNotify();
				
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
	if (column == CONDITION_COLUMN)
		return false;
	else
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
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 5:17:31 PM)
 * @param rowNumber int
 * @param alarm java.lang.String
 * @param generate boolean
 * @param notify boolean
 */
public void setRowValue(int rowNumber, String alarm, String generate, String notify) 
{
	if( rowNumber >=0 && rowNumber < getRowCount() )
	{
		getRows().setElementAt( new RowValue(alarm, generate, notify), rowNumber );
	}
}
/**
 * setValueAt method comment.
 */
// THIS METHOD IS NEEDED SINCE I AM USING A DEFAULTCELLEDITOR AND A CELLRENDERRER
//   THAT I CREATED, WHEN AN EDITING OCCURS, THIS METHOD IS CALLED.
public void setValueAt(Object value, int row, int col) 
{
	if( row <= getRows().size() && col < getColumnCount() )
	{

		switch( col )
		{
		 	case CONDITION_COLUMN:
				((RowValue)getRows().elementAt(row)).setAlarm(value.toString());
				break;
				
		 	case CATEGORY_COLUMN:
				((RowValue)getRows().elementAt(row)).setGenerate(value.toString());
				break;
				
			case NOTIFY_COLUMN:
				((RowValue)getRows().elementAt(row)).setExcludeNotify( value.toString() );
				break;
				
			default:
				com.cannontech.clientutils.CTILogger.info(this.getClass() + " tried to set value for an invalid column, column number " + col );
		}
	}
}
}
