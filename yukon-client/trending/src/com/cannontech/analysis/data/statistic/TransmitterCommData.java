package com.cannontech.analysis.data.statistic;

import com.cannontech.analysis.ColumnProperties;


/**
 * Created on Dec 15, 2003
 * StatisticTransmitterCommData TableModel object
 * Innerclass object for row data is TransmmitterCommStat:
 *  String transName		- YukonPaobject.paoName
 *  Integer totalAttempts	- DynamicPaoStatistics.attempts
 *  Integer portFailures	- DynamicPaoStatistics.commErrors
 *  Double portPercent		- (DynamicPaoStatistics.attempts - DPS.commErrors) / DPS.attempts
 *  Double succCommPerc		- DynamicPaoStatistics.completions / DPS.requests
 * @author snebben
 */
public class TransmitterCommData extends StatisticData
{
	/** Number of columns */
	protected final int NUMBER_COLUMNS = 5;
	
	/** Enum values for column representation */
	private final int TRANSMITTER_NAME_COLUMN = 0;
	private final int TOTAL_ATTEMPTS_COLUMN = 1;
	private final int PORT_FAILURES_COLUMN = 2;
	private final int PORT_PERCENT_COLUMN = 3;
	private final int SUCC_COMM_PERC_COLUMN = 4;

	/** String values for column representation */
	public final static String TRANSMITTER_NAME_STRING = "Transmitter Name";
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
	private static String title = "TRANSMITTER COMMUNICATION STATISTICS";

	/**
	 * @param paoName_
	 * @param attempts_
	 * @param commErrors_
	 * @param systemErrors_
	 * @param protocolErrs_
	 * @param completions_
	 * @param requests_
	 */
	public TransmitterCommData(String paoName_, Integer attempts_, Integer commErrors_, Integer systemErrors_, Integer protocolErrs_, Integer completions_, Integer requests_)
	{
		super(paoName_, attempts_, commErrors_, systemErrors_, protocolErrs_, completions_, requests_);
	}

	/**
	 * Default Constructor
	 */
	public TransmitterCommData()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getAttribute(int, java.lang.Object)
	 */
	public Object getAttribute(int columnIndex, Object o)
	{
		if( o instanceof TransmitterCommData)
		{
			TransmitterCommData tcs = (TransmitterCommData)o;
			switch( columnIndex)
			{
				case TRANSMITTER_NAME_COLUMN:
					return tcs.getPAOName();
				
				case TOTAL_ATTEMPTS_COLUMN:
					return tcs.getAttempts();
	
				case PORT_FAILURES_COLUMN:
					return tcs.getCommErrors();
	
				case PORT_PERCENT_COLUMN:
					return tcs.getPortPercent();
					
				case SUCC_COMM_PERC_COLUMN:
					return tcs.getSuccessPercent();
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
				TRANSMITTER_NAME_STRING,
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
		if( columnProperties == null)
		{
			columnProperties = new ColumnProperties[]{
			   //posX, posY, width, height, numberFormatString
			   new ColumnProperties(0, 1, 210, 20, null),
			   new ColumnProperties(210, 1, 60, 20, "#,##0"),
			   new ColumnProperties(270, 1, 60, 20, "#,##0"),
			   new ColumnProperties(330, 1, 60, 20, "##0.00%"),
			   new ColumnProperties(390, 1, 110, 20, "##0.00%")
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

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
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
	/**
	 * @param string
	 */
/*	public static void setTitleString(String string)
	{
		title = string;
	}
*/
}
