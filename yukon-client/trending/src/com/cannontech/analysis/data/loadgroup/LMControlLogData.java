package com.cannontech.analysis.data.loadgroup;

import com.cannontech.analysis.data.ColumnProperties;

/**
 * Created on Dec 15, 2003
 * @author snebben 
 * LMControlLogData TableModel object
 * extends the SystemlLogData (please see notes there)
 */
public class LMControlLogData extends com.cannontech.analysis.data.SystemLogData
{
	/** Class fields */
	/** Overriding the SystemLogData.logType field */
	protected int logType = com.cannontech.database.db.point.SystemLog.TYPE_LOADMANAGEMENT;
	
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 4;

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
	public LMControlLogData(long startTime_, long stopTime_)
	{
		super(startTime_, stopTime_);		
	}	
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 * @param logType_ SYSTEMLOG.type
	 */
	public LMControlLogData(long startTime_, long stopTime_, Integer logType_)
	{
		super(startTime_, stopTime_, logType_);
	}
	/**
	 * Constructor class
	 * @param startTime_ SYSTEMLOG.dateTime
	 * @param stopTime_ SYSTEMLOG.dateTime
	 * @param logType_ SYSTEMLOG.type
	 */
	public LMControlLogData(long startTime_, long stopTime_, Integer logType_, boolean orderDesc_)
	{
		this(startTime_, stopTime_, logType_);
		setOrderDescending(orderDesc_);
	}
		
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[NUMBER_COLUMNS];
			columnNames[0] = DATE_STRING;
			columnNames[1] = TIME_STRING;
			columnNames[2] = DESCRIPTION_STRING;
			columnNames[3] = ACTION_STRING;
		}
		return columnNames;
	}
	

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getColumnTypes()
	 */
	public Class[] getColumnTypes()
	{
		if( columnTypes == null)
		{
			columnTypes = new Class[NUMBER_COLUMNS];
			columnTypes[0] = java.util.Date.class;
			columnTypes[1] = java.util.Date.class;
			columnTypes[2] = String.class;
			columnTypes[3] = String.class;
		}
			
		return columnTypes;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getColumnProperties()
	 */
	public ColumnProperties[] getColumnProperties()
	{
		if(columnProperties == null)
		{
			columnProperties = new ColumnProperties[NUMBER_COLUMNS];
			//posX, posY, width, height, numberFormatString
			columnProperties[0] = new ColumnProperties(0, 1, 150, 18, "MMMMM dd, yyyy");
			columnProperties[1] = new ColumnProperties(0, 1, 65, 18, "hh:mm:ss");
			columnProperties[2] = new ColumnProperties(65, 1, 200, 18, null);
			columnProperties[3] = new ColumnProperties(265, 1, 300, 18, null);
		}
		return columnProperties;
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.StatisticReportDataBase#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof SystemLog)
		{
			SystemLog sl = ((SystemLog)o);
			switch( columnIndex)
			{
				case DATE_COLUMN:
					return sl.dateTime;
				case TIME_COLUMN:
					return sl.dateTime;
				case ACTION_COLUMN:
					return sl.action;					
				case DESCRIPTION_COLUMN:
					return sl.description;
			}
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getTitleString()
	 */
	public String getTitleString()
	{
		return "LOAD MANAGEMENT CONTROL LOG";
	}
}
