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
public class MissedMeter implements Reportable
{	
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 4;
		
	public String collGroup = null;
	public String deviceName = null;
	public String pointName = null;
	public Integer pointID = null;

	
	/** Enum values for column representation */
	public final static int COLL_GROUP_NAME_COLUMN = 0;
	public final static int DEVICE_NAME_COLUMN = 1;
	public final static int POINT_NAME_COLUMN = 2;
	public final static int POINT_ID_COLUMN = 3;

	/** String values for column representation */
	public final static String COLL_GROUP_NAME_STRING = "Collection Group";
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String POINT_NAME_STRING = "Point Name";
	public final static String POINT_ID_STRING = "Point ID";

	/** Array of Strings representing the column names */
	private static String[] columnNames = null;
	/** Array of ColumnProperties representing the column width/height/pos/etc */
	private static ColumnProperties[] columnProperties = null;
	/** Array of Classes representing the data in each column */
	private static Class[] columnTypes = null;
	/** A string for the title of the data */
	private static String title = "Missed Meter Reads By Collection Group";

	/**
	 * @param collGroup_
	 * @param deviceName_
	 * @param pointName_
	 * @param pointID_
	 */
	public MissedMeter(String collGroup_, String deviceName_, String pointName_, Integer pointID_)
	{
		collGroup = collGroup_;
		deviceName = deviceName_;
		pointName = pointName_;
		pointID = pointID_;			
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof MissedMeter)
		{
			MissedMeter meter = ((MissedMeter)o); 
			switch( columnIndex)
			{
				case COLL_GROUP_NAME_COLUMN:
					return meter.collGroup;
		
				case DEVICE_NAME_COLUMN:
					return meter.deviceName;
	
				case POINT_NAME_COLUMN:
					return meter.pointName;
	
				case POINT_ID_COLUMN:
					return meter.pointID;
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
				COLL_GROUP_NAME_STRING,
				DEVICE_NAME_STRING,
				POINT_NAME_STRING,
				POINT_ID_STRING
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
				Integer.class
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
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(0, 1, 100, 18, null),
				new ColumnProperties(100, 1, 100, 18, null),
				new ColumnProperties(200, 1, 100, 18, null),
				new ColumnProperties(300, 1, 100, 18, null)
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