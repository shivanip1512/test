/*
 * Created on Feb 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis.data.device;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.Reportable;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/** Inner class container of table model data*/
public class Carrier implements Reportable
{	
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 6;
	
	/** Enum values for column representation */
	public final static int PAO_NAME_COLUMN = 0;
	public final static int PAO_TYPE_COLUMN = 1;
	public final static int ADDRESS_COLUMN = 2;
	public final static int ROUTE_NAME_COLUMN = 3;
	public final static int COLL_GROUP_NAME_COLUMN = 4;
	public final static int TEST_COLL_GROUP_NAME_COLUMN = 5;
	
	/** String values for column representation */
	public final static String PAO_NAME_STRING = "MCT Name";
	public final static String PAO_TYPE_STRING = "Type";
	public final static String ADDRESS_STRING  = "Address";
	public final static String ROUTE_NAME_STRING = "Route Name";
	public final static String COLL_GROUP_NAME_STRING = "Collection Group";
	public final static String TEST_COLL_GROUP_NAME_STRING = "Alternate Group";
	
	/** Array of Strings representing the column names */
	private static String[] columnNames = null;
	/** Array of ColumnProperties representing the column width/height/pos/etc */
	private static ColumnProperties[] columnProperties = null;
	/** Array of Classes representing the data in each column */
	private static Class[] columnTypes = null;
	/** A string for the title of the data */
	private static String title = "Database Report - Carrier";

	public String paoName = null;
	public String paoType = null;
	public String address = null;			
	public String routeName = null;
	public String collGroup = null;
	public String testCollGroup = null;
	/**
	 * @param paoName_
	 * @param paoType_
	 * @param address_
	 * @param routeName_
	 * @param collGroup_
	 * @param testCollGroup_
	 */
	public Carrier(String paoName_, String paoType_, String address_, String routeName_, String collGroup_, String testCollGroup_)
	{
		paoName = paoName_;
		paoType = paoType_;
		address = address_;
		routeName = routeName_;
		collGroup = collGroup_;
		testCollGroup = testCollGroup_;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof Carrier)
		{
			Carrier carrier = ((Carrier)o); 
			switch( columnIndex)
			{
				case PAO_NAME_COLUMN:
					return carrier.paoName;
		
				case PAO_TYPE_COLUMN:
					return carrier.paoType;
	
				case ADDRESS_COLUMN:
					return carrier.address;
	
				case ROUTE_NAME_COLUMN:
					return carrier.routeName;
				
				case COLL_GROUP_NAME_COLUMN:
					return carrier.collGroup;
				
				case TEST_COLL_GROUP_NAME_COLUMN:
					return carrier.testCollGroup;
			}
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[]{
				PAO_NAME_STRING,
				PAO_TYPE_STRING,
				ADDRESS_STRING,
				ROUTE_NAME_STRING,
				COLL_GROUP_NAME_STRING,
				TEST_COLL_GROUP_NAME_STRING,
			};
		}
		return columnNames;
	}
	

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnTypes()
	 */
	public Class[] getColumnTypes()
	{
		if( columnTypes == null)
		{
			columnTypes = new Class[]{
				String.class,
				String.class,
				String.class,
				String.class,
				String.class,
				String.class
			};
		}
		return columnTypes;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties()
	{

		if(columnProperties == null)
		{
			columnProperties = new ColumnProperties[]{
				new ColumnProperties(0, 1, 175, 20, null),
				new ColumnProperties(175, 1, 75, 20, "#,##0"),
				new ColumnProperties(240, 1, 50, 20, "#,##0"),
				new ColumnProperties(305, 1, 50, 20, "##0.00%"),
				new ColumnProperties(370, 1, 55, 20, "##0.00%"),
				new ColumnProperties(425, 1, 55, 20, "##0.00%")
			};
		}
		return columnProperties;
	}	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
	}	
}