package com.cannontech.analysis.data.loadgroup;

import java.sql.ResultSet;

import com.cannontech.analysis.data.ColumnProperties;
import com.cannontech.analysis.data.ReportDataBase;

/**
 * Created on Dec 15, 2003
 * LoadGroupReportData TableModel object
 * Innerclass object for row data is LGAccounting:
 * 	String paoName				- YukonPaobject.paoName
 * 	java.util.Date startDate	- LMControlHistory.startDateTime
 *  java.util.Date stopDate		- LMControlHistory.stopDateTime
 *  String duration				- LMControlHistory.controlDuration (in seconds)
 *  String controlType			- LMControlHistory.controlType
 *  String dailyControl			- LMControlHistory.currentDailyControl (in seconds)
 *  String monthlyControl		- LMControlHistory.currentMonthlyControl (in seconds)
 *  String seasonalControl		- LMControlHistory.currentSeasonalControl (in seconds)
 *  String annualControl		- LMControlHistory.currentAnnualControl (in seconds)
 * @author snebben
 */
public class LoadGroupReportData extends ReportDataBase
{
	/** Class fields */
	/** Start time for query in millis */
	private long startTime = Long.MIN_VALUE;
	/** Stop time for query in millis */
	private long stopTime = Long.MIN_VALUE;
	
	/** Vector of Strings (of loadGroup paoNames)*/
	private java.util.Vector loadGroups = null;

	/** Number of columns */
	protected final int NUMBER_COLUMNS = 10;
	
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

	/** Inner class container of table model data*/
	private class LGAccounting
	{
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
	}
	/**
	 * Constructor class
	 * @param startTime_ LMControlHistory.startDateTime
	 * @param stopTime_ LMControlHistory.stopDateTiem
	 * 
	 * A null loadGroup is specified, which means ALL Load Groups!
	 */
	public LoadGroupReportData(long startTime_, long stopTime_)
	{
		super();
		setStartTime(startTime_);
		setStopTime(stopTime_);		
	}	
	/**
	 * Constructor class
	 * @param loadGroup_ YukonPaobject.paoName (of single load group)
	 * @param startTime_ LMControlHistory.startDateTime
	 * @param stopTime_ LMControlHistory.stopDateTiem
	 */
	public LoadGroupReportData(String loadGroup_, long startTime_, long stopTime_)
	{
		super();
		setLoadGroups(loadGroup_);
		setStartTime(startTime_);
		setStopTime(stopTime_);		
	}
	/**
	 * Constructor class
	 * @param loadGroups_ (Vector of)YukonPaobject.paoName (of single load group)
	 * @param startTime_ LMControlHistory.startDateTime
	 * @param stopTime_ LMControlHistory.stopDateTiem
	 */
	public LoadGroupReportData(java.util.Vector loadGroups_, long startTime_, long stopTime_)
	{
		super();
		setLoadGroups(loadGroups_);
		setStartTime(startTime_);
		setStopTime(stopTime_);		
	}
	
	/**
	 * Add LGAccounting objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(ResultSet rset)
	{
		try
		{
			String paoName = rset.getString(1);
			java.sql.Timestamp startTS = rset.getTimestamp(2);
			java.sql.Timestamp stopTS = rset.getTimestamp(3);
			Integer controlDuration = new Integer(rset.getInt(4));
			String controlType = rset.getString(5);
			Integer dailyTime = new Integer(rset.getInt(6));
			Integer monthyTime = new Integer(rset.getInt(7));
			Integer seasonalTime = new Integer(rset.getInt(8));
			Integer annualTime = new Integer(rset.getInt(9));
			
			LGAccounting lgAccounting= new LGAccounting(paoName,
														new java.util.Date(startTS.getTime()),
														new java.util.Date(stopTS.getTime()),
														controlDuration, controlType,
														dailyTime, monthyTime, seasonalTime, annualTime); 
			data.add(lgAccounting);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Build the SQL statement to retrieve LoadGroup Accounting data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer("SELECT PAO.PAOName, LMCH.StartDateTime, LMCH.StopDateTime, "+
				" LMCH.ControlDuration, LMCH.ControlType, "+
				" LMCH.CurrentDailyTime, LMCH.CurrentMonthlyTime, "+
				" LMCH.CurrentSeasonalTime, LMCH.CurrentAnnualTime "+
				" FROM YukonPAObject PAO, LMControlHistory LMCH "+
				" WHERE PAO.PAObjectID = LMCH.PAObjectID ");
				if( getLoadGroups()!= null)	//null load groups means ALL groups!
				{
					sql.append(" AND PAO.paoname in ('" + getLoadGroups().get(0) + "'"); 
					for (int i = 1; i < getLoadGroups().size(); i++)
					{
						sql.append(", '" + getLoadGroups().get(i)+"' ");
					}
					sql.append(") ");
				}
				sql.append(" AND (LMCH.ActiveRestore = 'R' " + 
				" OR LMCH.ActiveRestore = 'T' OR LMCH.ActiveRestore='C' " +
				" OR LMCH.ActiveRestore='O' OR LMCH.ActiveRestore='M') " +
				" AND (LMCH.StartDateTime > ?) AND (LMCH.StopDateTime <= ?)" +
				" ORDER BY PAO.PAOName, LMCH.StartDateTime, LMCH.StopDateTime");
		return sql;
	}	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.LoadGroupReportData#collectData()
	 */
	public void collectData()
	{
		int rowCount = 0;
			
		StringBuffer sql = buildSQLStatement();
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
	
			if( conn == null )
			{
				com.cannontech.clientutils.CTILogger.error(getClass() + ":  Error getting database connection.");
				return;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartTime() ));
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopTime()));
				com.cannontech.clientutils.CTILogger.info("START DATE > " + new java.sql.Timestamp(getStartTime()) + "  -  STOP DATE <= " + new java.sql.Timestamp(getStopTime()));
				/*java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
				tempCal.add(java.util.Calendar.DATE, -90);
				stmt.setTimestamp(1, new java.sql.Timestamp(tempCal.getTime().getTime()));
				System.out.println( "DATE > "+ tempCal.getTime());*/
				rset = pstmt.executeQuery();
				while( rset.next())
				{
					addDataRow(rset);
				}
			}
		}
				
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if( pstmt != null )
					pstmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				e.printStackTrace();
			}
		}
		com.cannontech.clientutils.CTILogger.info("Report Records Collected from Database: " + data.size());
		return;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getColumnNames()
	 */
	public String[] getColumnNames()
	{
		if( columnNames == null)
		{
			columnNames = new String[NUMBER_COLUMNS];
			columnNames[0] = PAO_NAME_STRING;
			columnNames[1] = CONTROL_DATE_STRING;
			columnNames[2] = CONTROL_START_STRING;
			columnNames[3] = CONTROL_STOP_STRING;
			columnNames[4] = CONTROL_DURATION_STRING;
			columnNames[5] = CONTROL_TYPE_STRING;
			columnNames[6] = DAILY_CONTROL_STRING;
			columnNames[7] = MONTHLY_CONTROL_STRING;
			columnNames[8] = SEASONAL_CONTROL_STRING;
			columnNames[9] = ANNUAL_CONTROL_STRING;
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
			columnTypes[0] = String.class;
			columnTypes[1] = java.util.Date.class;
			columnTypes[2] = java.util.Date.class;
			columnTypes[3] = java.util.Date.class;
			columnTypes[4] = String.class;
			columnTypes[5] = String.class;
			columnTypes[6] = String.class;
			columnTypes[7] = String.class;
			columnTypes[8] = String.class;
			columnTypes[9] = String.class;
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
			columnProperties = new ColumnProperties[10];
			//posX, posY, width, height, numberFormatString
			columnProperties[0] = new ColumnProperties(0, 1, 55, 18, null);
			columnProperties[1] = new ColumnProperties(0, 1, 65, 18, "MM/dd/yyyy");
			columnProperties[2] = new ColumnProperties(65, 1, 55, 18, "hh:mm:ss");
			columnProperties[3] = new ColumnProperties(120, 1, 55, 18, "hh:mm:ss");
			columnProperties[4] = new ColumnProperties(175, 1, 55, 18, null);
			columnProperties[5] = new ColumnProperties(230, 1, 80, 18, null);
			columnProperties[6] = new ColumnProperties(310, 1, 55, 18, null);
			columnProperties[7] = new ColumnProperties(365, 1, 55, 18, null);
			columnProperties[8] = new ColumnProperties(420, 1, 55, 18, null);
			columnProperties[9] = new ColumnProperties(475, 1, 55, 18, null);
		}
		return columnProperties;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportDataBase#getAttribute(int, java.lang.Object)
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
	 * @see com.cannontech.analysis.data.ReportDataBase#getTitleString()
	 */
	public String getTitleString()
	{
		return "LOAD GROUP ACCOUNTING";
	}

	/**
	 * Returns the startTime in millis
	 * @return long startTime
	 */
	public long getStartTime()
	{
		return startTime;
	}
	/**
	 * Returns the stopTime in millis
	 * @return long stopTime
	 */
	public long getStopTime()
	{
		return stopTime;
	}
	/**
	 * Set the startTime in millis
	 * @param long time
	 */
	public void setStartTime(long time)
	{
		startTime = time;
	}
	/**
	 * Set the stopTime in millis
	 * @param long time
	 */
	public void setStopTime(long time)
	{
		stopTime = time;
	}
	/**
	 * Return Vector of strings of loadGroups
	 * @return
	 */
	public java.util.Vector getLoadGroups()
	{
		return loadGroups;
	}

	/**
	 * Sets vector of string values (of loadGroup Paonames)
	 * @param Vector laodGroups_
	 */
	public void setLoadGroups(java.util.Vector loadGroups_)
	{
		loadGroups = loadGroups_;
	}
	/**
	 * @param String loadGroup_
	 */
	public void setLoadGroups(String loadGroup_)
	{
		loadGroups = new java.util.Vector(1);
		loadGroups.add(loadGroup_);
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
}
