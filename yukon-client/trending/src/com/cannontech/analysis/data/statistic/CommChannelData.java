package com.cannontech.analysis.data.statistic;

import com.cannontech.analysis.ColumnProperties;


/**
 * Created on Dec 15, 2003
 * StatisticCommChannelData TableModel object
 * Innerclass object for row data is CommStat:
 *  String portName			- YukonPaobject.paoName
 *  Integer totalAttempts	- DynamicPaoStatistics.attempts
 *  Integer portFailuers	- DynamicPaoStatistics.commErrors
 *  Double portPercent		- (DynamicPaoStatistics.attempts - DPS.commErrors) / DPS.attempts
 *  Double succCommPerc		- DynamicPaoStatistics.completions / DPS.requests
 * @author snebben
 */
public class CommChannelData extends StatisticData
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 5;
		
	/** Enum values for column representation */
	private final int PORT_NAME_COLUMN = 0;
	private final int TOTAL_ATTEMPTS_COLUMN = 1;
	private final int PORT_FAILURES_COLUMN = 2;
	private final int PORT_PERCENT_COLUMN = 3;
	private final int SUCC_COMM_PERC_COLUMN = 4;

	/** String values for column representation */
	public final static String PORT_NAME_STRING = "Port Name";
	public final static String TOTAL_ATTEMPTS_STRING = "Total Attempts";
	public final static String PORT_FAILURES_STRING = "Port Failures";
	public final static String PORT_PERCENT_STRING = "Port Percent";
	public final static String SUCC_COMM_PERC_STRING= "Successful Communication%";
	
	/** Array of Strings representing the column names */
	private static String[] columnNames = null;
	/** Array of ColumnProperties representing the column width/height/pos/etc */
	private static ColumnProperties[] columnProperties = null;
	/** Array of Classes representing the data in each column */
	private static Class[] columnTypes = null;
	/** A string for the title of the data */
	private static String title = "COMMUNICATION CHANNEL STATISTICS";
	/**
	 * @param paoName_
	 * @param attempts_
	 * @param commErrors_
	 * @param systemErrors_
	 * @param protocolErrs_
	 * @param completions_
	 * @param requests_
	 */
	public CommChannelData(String paoName_, Integer attempts_, Integer commErrors_, Integer systemErrors_, Integer protocolErrs_, Integer completions_, Integer requests_)
	{
		super(paoName_, attempts_, commErrors_, systemErrors_, protocolErrs_, completions_, requests_);
	}

	/**
	 * Default Constructor
	 */
	public CommChannelData()
	{
		super();
	}
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof CommChannelData )
		{
			CommChannelData ccs = (CommChannelData)o;
			switch( columnIndex)
			{
				case PORT_NAME_COLUMN:
					return ccs.getPAOName();
				
				case TOTAL_ATTEMPTS_COLUMN:
					return ccs.getAttempts();
	
				case PORT_FAILURES_COLUMN:
					return ccs.getCommErrors();
	
				case PORT_PERCENT_COLUMN:
					return ccs.getPortPercent();
					
				case SUCC_COMM_PERC_COLUMN:
					return ccs.getSuccessPercent();
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
			   PORT_NAME_STRING,
			   TOTAL_ATTEMPTS_STRING,
			   PORT_FAILURES_STRING,
			   PORT_PERCENT_STRING,
			   SUCC_COMM_PERC_STRING
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
				new ColumnProperties(0, 1, 175, 20, null),
				new ColumnProperties(175, 1, 75, 20, "#,##0"),
				new ColumnProperties(240, 1, 50, 20, "#,##0"),
				new ColumnProperties(305, 1, 50, 20, "##0.00%"),
				new ColumnProperties(370, 1, 110, 20, "##0.00%")
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
				Integer.class,
				Integer.class,
				Double.class,
				Double.class
			};
		}
		return columnTypes;
	}

	/**
	 * @param strings
	 */
/*	public static void setColumnNames(String[] strings)
	{
		columnNames = strings;
	}
*/
	/**
	 * @param properties
	 */
/*	public static void setColumnProperties(ColumnProperties[] properties)
	{
		columnProperties = properties;
	}
*/
	/**
	 * @param classes
	 */
/*	public static void setColumnTypes(Class[] classes)
	{
		columnTypes = classes;
	}
*/
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
	}

	/**
	 * @param string
	 */
/*	public static void setTitleString(String string)
	{
		title = string;
	}
*/
}
