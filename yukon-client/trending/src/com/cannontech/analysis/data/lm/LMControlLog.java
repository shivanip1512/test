package com.cannontech.analysis.data.lm;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.data.SystemLog;

/**
 * Created on Dec 15, 2003
 * @author snebben 
 * LMControlLogData TableModel object
 * extends the SystemlLogData (please see notes there)
 */
public class LMControlLog extends SystemLog
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 4;
	
	/** Class fields */

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
	
	/** Array of Strings representing the column names */
	private static String[] columnNames = null;
	/** Array of ColumnProperties representing the column width/height/pos/etc */
	private static ColumnProperties[] columnProperties = null;
	/** Array of Classes representing the data in each column */
	private static Class[] columnTypes = null;
	/** A string for the title of the data */
	private static String title = "LOAD MANAGEMENT CONTROL LOG";


	/**
	 * 
	 */
	public LMControlLog()
	{
		this(null);
	}

	/**
	 * @param pointID
	 */
	public LMControlLog(Integer pointID)
	{
		super(pointID);
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
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
	}
}
