package com.cannontech.tdc.data;

/**
 * Insert the type's description here.
 * Creation date: (1/24/2002 9:00:37 AM)
 * @author: 
 */
public class ColumnData implements java.io.Externalizable
{
	private static final String FIELD_DELIMITER = String.valueOf( (char)16 );

	private long displayNumber = Display.UNKNOWN_DISPLAY_NUMBER;
	private String columnName = null;
	private int ordering = 0;
	private int width = 0;
/**
 * ColumnData constructor comment.
 */
public ColumnData() {
	super();
}
/**
 * ColumnData constructor comment.
 */
public ColumnData(long displayNum_, String columnName_, int ordering_, int width_ )
{
	super();

	setDisplayNumber( displayNum_ );
	setColumnName( columnName_ );
	setOrdering( ordering_ );
	setWidth( width_ );
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 2:34:50 PM)
 * @return boolean
 * @param o java.lang.Object
 */
public boolean equals(Object o) 
{
	return ( (o != null) &&
			   (o instanceof ColumnData) &&
			   ( ((ColumnData)o).getDisplayNumber() == getDisplayNumber() ) &&
			   ( ((ColumnData)o).getColumnName().equals(getColumnName()) ) );
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 9:07:55 AM)
 * @return java.lang.String
 */
public java.lang.String getColumnName() {
	return columnName;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 11:34:10 AM)
 * @return long
 */
public long getDisplayNumber() {
	return displayNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 9:07:55 AM)
 * @return int
 */
public int getOrdering() {
	return ordering;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 9:07:55 AM)
 * @return int
 */
public int getWidth() {
	return width;
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 3:31:06 PM)
 * @param oi java.io.ObjectInput
 */
public void readExternal(java.io.ObjectInput oIn)
{

	try
	{
		int length = oIn.readInt();
		byte[] data = new byte[length];

		oIn.read( data );

		java.util.StringTokenizer statTokenizer = new java.util.StringTokenizer( new String(data), FIELD_DELIMITER );

		setDisplayNumber( Long.parseLong(statTokenizer.nextToken()) );
		setColumnName( statTokenizer.nextToken() );
		setOrdering( Integer.parseInt(statTokenizer.nextToken()) );
		setWidth( Integer.parseInt(statTokenizer.nextToken()) );
	}
	catch( Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 9:07:55 AM)
 * @param newColumnName java.lang.String
 */
public void setColumnName(java.lang.String newColumnName) {
	columnName = newColumnName;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 11:34:10 AM)
 * @param newDisplayNumber long
 */
protected void setDisplayNumber(long newDisplayNumber) {
	displayNumber = newDisplayNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 9:07:55 AM)
 * @param newOrdering int
 */
public void setOrdering(int newOrdering) {
	ordering = newOrdering;
}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 9:07:55 AM)
 * @param newWidth int
 */
public void setWidth(int newWidth) {
	width = newWidth;
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 3:30:46 PM)
 * @param oo java.io.ObjectOutput
 */
public void writeExternal(java.io.ObjectOutput oOut)
{
	try
	{
		StringBuffer buf = new StringBuffer();
		buf.append( getDisplayNumber() + FIELD_DELIMITER );
		buf.append( getColumnName() + FIELD_DELIMITER );
		buf.append( getOrdering() + FIELD_DELIMITER );
		buf.append( getWidth() + FIELD_DELIMITER );

		oOut.writeInt( buf.length() );
		oOut.writeBytes( buf.toString() );
	}
	catch( Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

}
}
