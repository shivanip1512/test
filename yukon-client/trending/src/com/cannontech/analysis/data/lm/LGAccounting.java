/*
 * Created on Feb 9, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis.data.lm;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.Reportable;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LGAccounting implements Reportable
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 10;
	
	public String paoName = null;
	//date is intuitive from startDate
	public java.util.Date startDate = null;	//startTime
	public java.util.Date stopDate = null;	//stopTime
	public String duration = null;
	public String controlType = null; 
	public String dailyControl = null;
	public String monthlyControl = null;
	public String seasonalControl = null;
	public String annualControl = null;

	/** Enum values for column representation */
	public final static int PAO_NAME_COLUMN = 0;
	public final static int CONTROL_DATE_COLUMN = 1;
	public final static int CONTROL_START_TIME_COLUMN = 2;
	public final static int CONTROL_STOP_TIME_COLUMN = 3;
	public final static int CONTROL_DURATION_COLUMN = 4;
	public final static int CONTROL_TYPE_COLUMN = 5;
	public final static int DAILY_CONTROL_COLUMN = 6;
	public final static int MONTHLY_CONTROL_COLUMN = 7;
	public final static int SEASONAL_CONTROL_COLUMN = 8;
	public final static int ANNUAL_CONTROL_COLUMN = 9;

	/** String values for column representation */
	public final static String PAO_NAME_STRING = "Load Group";
	public final static String CONTROL_DATE_STRING = "Date";
	public final static String CONTROL_START_STRING = "Control Start";
	public final static String CONTROL_STOP_STRING = "Control Stop";
	public final static String CONTROL_DURATION_STRING = "Control Duration";
	public final static String CONTROL_TYPE_STRING= "Control Type";
	public final static String DAILY_CONTROL_STRING= "Daily Control";
	public final static String MONTHLY_CONTROL_STRING= "Monthly Control";
	public final static String SEASONAL_CONTROL_STRING= "Seasonal Control";
	public final static String ANNUAL_CONTROL_STRING= "Annual Control";

	/** Array of Strings representing the column names */
	private static String[] columnNames = null;
	/** Array of ColumnProperties representing the column width/height/pos/etc */
	private static ColumnProperties[] columnProperties = null;
	/** Array of Classes representing the data in each column */
	private static Class[] columnTypes = null;
	/** A string for the title of the data */
	private static String title = "LOAD GROUP ACCOUNTING";
		
	/**
	 * @param paoName_
	 * @param startDate_
	 * @param stopDate_
	 * @param duration_
	 * @param controlType_
	 * @param daily_
	 * @param monthly_
	 * @param seasonal_
	 * @param annual_
	 */
	public LGAccounting(String paoName_, java.util.Date startDate_, java.util.Date stopDate_,
						Integer duration_, String controlType_, Integer daily_, Integer monthly_,
						Integer seasonal_, Integer annual_)
	{
		paoName = paoName_;
		startDate = startDate_;
		stopDate = stopDate_;
		duration = convertSecondsToTimeString(duration_.intValue());
		controlType = controlType_;
		dailyControl = convertSecondsToTimeString(daily_.intValue());
		monthlyControl = convertSecondsToTimeString(monthly_.intValue());
		seasonalControl = convertSecondsToTimeString(seasonal_.intValue());
		annualControl = convertSecondsToTimeString(annual_.intValue());
	}

	/**
	 * Convert seconds of time into hh:mm:ss string.
	 * @param int seconds
	 * @return String in format hh:mm:ss
	 */
	private String convertSecondsToTimeString(int seconds)
	{
		java.text.DecimalFormat format = new java.text.DecimalFormat("00");
		int hour = seconds / 3600;
		int temp = seconds % 3600;
		int min = temp / 60;
		int sec = temp % 60; 
			
		return format.format(hour) + ":" + format.format(min) + ":" + format.format(sec);
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof LGAccounting)
		{
			LGAccounting lga = ((LGAccounting)o);
			switch( columnIndex)
			{
				case PAO_NAME_COLUMN:
					return lga.paoName;
				case CONTROL_DATE_COLUMN:
					return lga.startDate;
				case CONTROL_START_TIME_COLUMN:
					return lga.startDate;
				case CONTROL_STOP_TIME_COLUMN:
					return lga.stopDate;
				case CONTROL_DURATION_COLUMN:
					return lga.duration;
				case CONTROL_TYPE_COLUMN:
					return lga.controlType;
				case DAILY_CONTROL_COLUMN:
					return lga.dailyControl;
				case MONTHLY_CONTROL_COLUMN:
					return lga.monthlyControl;
				case SEASONAL_CONTROL_COLUMN:
					return lga.seasonalControl;
				case ANNUAL_CONTROL_COLUMN:
					return lga.annualControl;
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
				CONTROL_DATE_STRING,
				CONTROL_START_STRING,
				CONTROL_STOP_STRING,
				CONTROL_DURATION_STRING,
				CONTROL_TYPE_STRING,
				DAILY_CONTROL_STRING,
				MONTHLY_CONTROL_STRING,
				SEASONAL_CONTROL_STRING,
				ANNUAL_CONTROL_STRING
			};
		}
		return columnNames;
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
				new ColumnProperties(0, 1, 55, 18, null),
				new ColumnProperties(0, 1, 65, 18, "MM/dd/yyyy"),
				new ColumnProperties(65, 1, 55, 18, "hh:mm:ss"),
				new ColumnProperties(120, 1, 55, 18, "hh:mm:ss"),
				new ColumnProperties(175, 1, 55, 18, null),
				new ColumnProperties(230, 1, 80, 18, null),
				new ColumnProperties(310, 1, 55, 18, null),
				new ColumnProperties(365, 1, 55, 18, null),
				new ColumnProperties(420, 1, 55, 18, null),
				new ColumnProperties(475, 1, 55, 18, null)
			};
		}
		return columnProperties;
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
				java.util.Date.class,
				java.util.Date.class,
				java.util.Date.class,
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
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
	}
}
