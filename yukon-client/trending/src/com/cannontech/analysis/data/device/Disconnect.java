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
 * @author bjonasson
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Disconnect implements Reportable
{	
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 5;
		
	public String collGroup = null;
	public String deviceName = null;
	public String pointName = null;
	public java.util.Date timeStamp = null;
	public String valueString = null;

	
	/** Enum values for column representation */
	public final static int COLL_GROUP_NAME_COLUMN = 0;
	public final static int DEVICE_NAME_COLUMN = 1;
	public final static int POINT_NAME_COLUMN = 2;
	public final static int TIMESTAMP_COLUMN = 3;
	public final static int DISCONNECT_STATUS_COLUMN = 4;

	/** String values for column representation */
	public final static String COLL_GROUP_NAME_STRING = "Collection Group";
	public final static String DEVICE_NAME_STRING = "Device Name";
	public final static String POINT_NAME_STRING = "Point Name";
	public final static String TIMESTAMP_STRING = "Timestamp";
	public final static String DISCONNECT_STATUS_STRING = "Disconnect Status";

	/** Array of Strings representing the column names */
	private static String[] columnNames = null;
	/** Array of ColumnProperties representing the column width/height/pos/etc */
	private static ColumnProperties[] columnProperties = null;
	/** Array of Classes representing the data in each column */
	private static Class[] columnTypes = null;
	/** A string for the title of the data */
	private static String title = "Disconnect Status By Collection Group";
	



	/**
	 * 
	 */
	public Disconnect()
	{
		super();
	}

	/**
	 * @param collGroup_
	 * @param deviceName_
	 * @param pointName_
	 * @param pointID_
	 * @param timestamp_
	 * @param value_
	 */
	public Disconnect(String collGroup_, String deviceName_, String pointName_, java.util.Date timeStamp_, String value_)
	{
		collGroup = collGroup_;
		deviceName = deviceName_;
		pointName = pointName_;
		timeStamp = timeStamp_;
		valueString = value_;			
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if ( o instanceof Disconnect)
		{
			Disconnect meter = ((Disconnect)o); 
			switch( columnIndex)
			{
				case COLL_GROUP_NAME_COLUMN:
					return meter.collGroup;
		
				case DEVICE_NAME_COLUMN:
					return meter.deviceName;
	
				case POINT_NAME_COLUMN:
					return meter.pointName;
						
				case TIMESTAMP_COLUMN:
					return meter.timeStamp;
					
				case DISCONNECT_STATUS_COLUMN:
					return meter.valueString;
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
				TIMESTAMP_STRING,
				DISCONNECT_STATUS_STRING
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
				java.util.Date.class,
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
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(0, 1, 100, 18, null),  //Collection Group
				new ColumnProperties(0, 1, 200, 18, null),	//MCT
				new ColumnProperties(15, 1, 150, 18, null),	//Point
				new ColumnProperties(155, 1, 100, 18, "MM/dd/yyyy HH:MM:SS"),   //Timestamp
				new ColumnProperties(260, 1, 100, 18, null)   // Rawpointhistory.value
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