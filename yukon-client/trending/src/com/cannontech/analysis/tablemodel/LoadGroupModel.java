package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.data.lm.LGAccounting;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;

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
public class LoadGroupModel extends ReportModelBase
{
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

	/** A string for the title of the data */
	private static String title = "LOAD GROUP ACCOUNTING";
		
	/** Array of IDs (of loadGroup paobjectIDs)*/
	private int loadGroups[] = null;
	
	public LoadGroupModel()
	{
		super();
		setReportType(ReportTypes.LG_ACCOUNTING_DATA);
	}	

	/**
	 * Constructor class
	 * @param startTime_ LMControlHistory.startDateTime
	 * @param stopTime_ LMControlHistory.stopDateTime
	 * 
	 * A null loadGroup is specified, which means ALL Load Groups!
	 */
	public LoadGroupModel(long startTime_, long stopTime_)
	{
		this(null, startTime_, stopTime_, ReportTypes.LG_ACCOUNTING_DATA);
	}	
	/**
	 * Constructor class
	 * @param loadGroup_ YukonPaobject.paobjectID (of array of load groups)
	 * @param startTime_ LMControlHistory.startDateTime
	 * @param stopTime_ LMControlHistory.stopDateTiem
	 */
	public LoadGroupModel(int[] paoIDs_,long startTime_, long stopTime_ )
	{		
		this(paoIDs_,startTime_, stopTime_, ReportTypes.LG_ACCOUNTING_DATA);
	}
	
	/**
	 * Constructor class
	 * @param loadGroups_ (Array of)YukonPaobject.paobjectID (of single load group)
	 * @param startTime_ LMControlHistory.startDateTime
	 * @param stopTime_ LMControlHistory.stopDateTime
	 */
	public LoadGroupModel( int[] paoIDs_,long startTime_, long stopTime_, int reportType_)
	{
		super(reportType_, startTime_, stopTime_);
		setPaoIDs(paoIDs_);
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
			getData().add(lgAccounting);
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
				if( getPaoIDs()!= null)	//null load groups means ALL groups!
				{
					sql.append(" AND PAO.paobjectid in (" + getPaoIDs()[0] ); 
					for (int i = 1; i < getPaoIDs().length; i++)
					{
						sql.append(", " + getPaoIDs()[i]+" ");
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
		CTILogger.info(sql.toString());
		
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	
			if( conn == null )
			{
				CTILogger.error(getClass() + ":  Error getting database connection.");
				return;
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartTime()));
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopTime()));
				CTILogger.info("START DATE > " + new java.sql.Timestamp(getStartTime()) + "  -  STOP DATE <= " + new java.sql.Timestamp(getStopTime()));
				
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
		CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
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
					return lga.getPaoName();
				case CONTROL_DATE_COLUMN:
					return lga.getStartDate();
				case CONTROL_START_TIME_COLUMN:
					return lga.getStartDate();
				case CONTROL_STOP_TIME_COLUMN:
					return lga.getStopDate();
				case CONTROL_DURATION_COLUMN:
					return lga.getDuration();
				case CONTROL_TYPE_COLUMN:
					return lga.getControlType();
				case DAILY_CONTROL_COLUMN:
					return lga.getDailyControl();
				case MONTHLY_CONTROL_COLUMN:
					return lga.getMonthlyControl();
				case SEASONAL_CONTROL_COLUMN:
					return lga.getSeasonalControl();
				case ANNUAL_CONTROL_COLUMN:
					return lga.getAnnualControl();
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
	 * @see com.cannontech.analysis.Reportable#getTitleString()
	 */
	public String getTitleString()
	{
		return title;
	}
}
	
