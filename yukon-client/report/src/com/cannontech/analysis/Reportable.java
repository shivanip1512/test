/*
 * Created on Feb 9, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface Reportable
{
	/**
	 * Return the Object from the columnIndex.
	 * (Return the field representing the "column")
	 * @param columnIndex int
	 * @param o Object
	 * @return Object
	 */
	public Object getAttribute(int columnIndex, Object o);
	
	/**
	 * Return an array of column Names
	 * @return String[]
	 */
	public String[] getColumnNames();
	
	/**
	 * Return an array of column class types
	 * @return Class[]
	 */
	public Class[] getColumnTypes();
	
	/**
	 * Return an array of column Properties
	 * representing the column width/height/pos/etc
	 * @return com.cannontech.analysis.ColumnProperties
	 */
	public ColumnProperties[] getColumnProperties();
	
	/**
	 * Return a string for the title of the data
	 * @return String
	 */
	public String getTitleString();	
}
