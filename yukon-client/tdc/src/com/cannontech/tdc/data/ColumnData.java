package com.cannontech.tdc.data;

/**
 * Insert the type's description here.
 * Creation date: (1/24/2002 9:00:37 AM)
 * @author: 
 */
public class ColumnData
{
	private String columnName = null;
	private int ordering = 0;
	private int width = 0;

/**
 * ColumnData constructor comment.
 */
protected ColumnData() {
	super();
}
/**
 * ColumnData constructor comment.
 */
public ColumnData(String columnName_, int ordering_, int width_ )
{
	super();

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
 * Creation date: (1/24/2002 9:07:55 AM)
 * @param newColumnName java.lang.String
 */
public void setColumnName(java.lang.String newColumnName) {
	columnName = newColumnName;
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
}
