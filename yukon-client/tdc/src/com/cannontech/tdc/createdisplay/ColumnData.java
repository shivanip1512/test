package com.cannontech.tdc.createdisplay;

/**
 * Insert the type's description here.
 * Creation date: (1/25/00 11:18:04 AM)
 * @author: 
 */
public class ColumnData 
{
	private Object columnType = null;
	private Object columnTypeNumber = null;
	private Object columnNum = null;
	private Object columnWidth = null;
	private String columnTitle = null;

	// constants
   public static final int COLUMN_TITLE = 20;
   public static final int COLUMN_TYPE_NUMBER = 21;
   public static final int COLUMN_NUMBER = 22;
   public static final int COLUMN_WIDTH = 23;
   public static final int COLUMN_TYPE = 24;
	
/**
 * ColumnData constructor comment.
 */
public ColumnData() {
	super();
}
/**
 * ColumnData constructor comment.
 */
public ColumnData( Object colNumber, Object typeName, Object width, String title, Object typeNumber ) {
	super();

	columnTitle = title;
	columnNum = colNumber;
	columnType = typeName;
	columnWidth = width;
	columnTypeNumber = typeNumber;
	
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/00 11:46:28 AM)
 * @return java.lang.Object
 */
public Object getColumnNum() {
	return columnNum;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/00 11:46:28 AM)
 * @return java.lang.Object
 */
public String getColumnTitle() {
	return columnTitle;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/00 11:46:28 AM)
 * @return java.lang.Object
 */
public Object getColumnType() {
	return columnType;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/00 11:46:28 AM)
 * @return java.lang.Object
 */
public Object getColumnTypeNumber() {
	return columnTypeNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/00 11:46:28 AM)
 * @return java.lang.Object
 */
public Object getColumnWidth() {
	return columnWidth;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/00 11:46:28 AM)
 * @return java.lang.Object
 */
public void setColumnNumber( int i ) 
{
	Integer value = new Integer( i );
	columnNum = value;
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/00 11:46:28 AM)
 * @return java.lang.Object
 */
public void setColumnTitle( String newValue ) 
{
	
	columnTitle = newValue;
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/00 11:46:28 AM)
 * @return java.lang.Object
 */
public void setColumnType( Object newValue ) 
{
	
	columnType = newValue;
	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/00 11:46:28 AM)
 * @return java.lang.Object
 */
public void setColumnTypeNumber( Object newValue ) 
{
	
	columnTypeNumber = newValue;
	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (1/25/00 11:46:28 AM)
 * @return java.lang.Object
 */
public void setColumnWidth( Object newValue ) 
{
	
	columnWidth = newValue;
	
	return;
}
}
