package com.cannontech.analysis.tablemodel;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.database.db.point.SystemLog;

/**
 * Created on Dec 15, 2003
 * LMControlLogModel TableModel object
 * Innerclass object for row data is SystemLog: 
 *  java.util.Date dateTime	- SystemLog.dateTime
 *  Integer pointID 		- SystemLog.pointID
 *  Integer priority 		- SystemLog.priority
 *  String action 			- SystemLog.action
 *  String description 		- SystemLog.description
 *  String userName 		- SystemLog.userName
 * @author snebben
 */
public class LMControlLogModel extends SystemLogModel
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 4;
	
	/** A string for the title of the data */
	private static String title = "LOAD MANAGEMENT CONTROL LOG";

	/** Enum values for column representation */
	// MUST OVERRIDE THE COLUMM INT REFERENCES FOR GETATTRIBUTES TO WORK
	protected final static int DATE_COLUMN = 0;
	protected final static int TIME_COLUMN = 1;
	protected final static int DESCRIPTION_COLUMN = 2;
	protected final static int ACTION_COLUMN = 3;

	/** String values for column representation */
	// THESE ARE OVERRIDES OF THE EXTENDED CLASS SYSTEMLOGDATA
	protected final static String DATE_STRING = "Date";
	protected final static String TIME_STRING = "Time";
	protected final static String DESCRIPTION_STRING = "Control Name";
	protected final static String ACTION_STRING = "Action";
		
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 */
	public LMControlLogModel(long startTime_, long stopTime_)
	{
		super(startTime_, stopTime_, null, null);
	}	
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 * @param logType_ SYSTEMLOG.pointID
	 */
	public LMControlLogModel(long startTime_, long stopTime_, Integer logType_)
	{
		this(startTime_, stopTime_, logType_, null);
	}
	/**
	 * Constructor class
	 * @param logType_ SYSTEMLOG.pointID
	 */
	public LMControlLogModel()
	{
		super();
	}	
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 * @param pointID_ SYSTEM.pointID
	 * @param logType_ SYSTEMLOG.type
	 */
	public LMControlLogModel(long startTime_, long stopTime_, Integer logType_, Integer pointID_)
	{
		super(startTime_, stopTime_, logType_, pointID_);
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
				case ACTION_COLUMN:
					return sl.getAction();					
				case DESCRIPTION_COLUMN:
					return sl.getDescription();
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
				DATE_STRING,
				TIME_STRING,
				DESCRIPTION_STRING,
				ACTION_STRING
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
				new ColumnProperties(0, 1, 150, 18, "MMMMM dd, yyyy"),
				new ColumnProperties(0, 1, 65, 18, "hh:mm:ss"),
				new ColumnProperties(65, 1, 200, 18, null),
				new ColumnProperties(265, 1, 300, 18, null)
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
