package com.cannontech.analysis.data;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.Reportable;


/**
 * Created on Dec 15, 2003
 * SystemLogModel TableModel object
 * Innerclass object for row data is SystemLog: 
 *  java.util.Date dateTime	- SystemLog.dateTime
 *  Integer pointID 		- SystemLog.pointID
 *  Integer priority 		- SystemLog.priority
 *  String action 			- SystemLog.action
 *  String description 		- SystemLog.description
 *  String userName 		- SystemLog.userName
 * @author snebben
 */
public class SystemLog extends com.cannontech.database.db.point.SystemLog implements Reportable
{	
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 7;
	
	/** Enum values for column representation */
	public final static int DATE_COLUMN = 0;
	public final static int TIME_COLUMN = 1;
	public final static int POINT_ID_COLUMN = 2;
	public final static int PRIORITY_COLUMN = 3;
	public final static int ACTION_COLUMN = 4;
	public final static int DESCRIPTION_COLUMN = 5;	
	public final static int USERNAME_COLUMN = 6;

	/** String values for column representation */
	public final static String DATE_STRING = "Date";
	public final static String TIME_STRING = "Time";
	public final static String POINT_ID_STRING = "PointID";
	public final static String PRIORITY_STRING = "Priority";
	public final static String ACTION_STRING = "Action";
	public final static String DESCRIPTION_STRING = "Description";
	public final static String USERNAME_STRING = "UserName";
	
	/** Array of Strings representing the column names */
	private static String[] columnNames = null;
	/** Array of ColumnProperties representing the column width/height/pos/etc */
	private static ColumnProperties[] columnProperties = null;
	/** Array of Classes representing the data in each column */
	private static Class[] columnTypes = null;
	/** A string for the title of the data */
	private static String title = "SYSTEM LOG";
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[]{
				DATE_STRING,
				TIME_STRING,
				POINT_ID_STRING,
				PRIORITY_STRING,
				ACTION_STRING,
				DESCRIPTION_STRING,
				USERNAME_STRING
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
				java.util.Date.class,
				java.util.Date.class,
				Integer.class,
				Integer.class,
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
				//posX, posY, width, height, numberFormatString
				new ColumnProperties(100, 1, 100, 18, "MMMMM dd, yyyy"),
				new ColumnProperties(0, 1, 50, 18, "hh:mm:ss"),
				new ColumnProperties(50, 1, 50, 18, null),
				new ColumnProperties(100, 1, 50, 18, null),
				new ColumnProperties(150, 1, 200, 18, null),
				new ColumnProperties(350, 1, 200, 18, null),
				new ColumnProperties(550, 1, 65, 18, null)
			};				
		}
		return columnProperties;
	}


	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof SystemLog)
		{
			SystemLog sl = ((SystemLog)o);
			switch( columnIndex)
			{
				case DATE_COLUMN:
					return sl.getDateTime();
				case TIME_COLUMN:
					return sl.getDateTime();
				case POINT_ID_COLUMN:
					return sl.getPointID();
				case PRIORITY_COLUMN:
					return sl.getPriority();
				case ACTION_COLUMN:
					return sl.getAction();					
				case DESCRIPTION_COLUMN:
					return sl.getDescription();
				case USERNAME_COLUMN:
					return sl.getUserName();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
	}
}
