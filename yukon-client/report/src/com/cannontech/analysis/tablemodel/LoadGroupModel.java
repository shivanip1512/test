package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;

import com.cannontech.analysis.ColumnProperties;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.db.device.lm.LMProgramDirectGroup;
import com.cannontech.database.db.pao.LMControlHistory;
import com.cannontech.database.db.user.UserPaoOwner;

/**
 * Created on Dec 15, 2003
 * LoadGroupReportData TableModel object
 * Innerclass object for row data is LGAccounting:
 * 	String paoName				- PaoFuncs.getYukonPaoName(LMControlHistory.paobjectID)
 * 	java.util.Date startDate	- LMControlHistory.startDateTime
 *  java.util.Date stopDate		- LMControlHistory.stopDateTime
 *  String duration				- LMControlHistory.controlDuration (in seconds)
 *  String activeRestore		- LMControlHistory.activeRestore
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
	protected final int NUMBER_COLUMNS = 11;
	
	/** Enum values for column representation */
	public final static int PAO_NAME_COLUMN = 0;
	public final static int CONTROL_DATE_COLUMN = 1;
	public final static int CONTROL_START_TIME_COLUMN = 2;
	public final static int CONTROL_STOP_TIME_COLUMN = 3;
	public final static int CONTROL_DURATION_COLUMN = 4;
	public final static int ACTIVE_RESTORE_COLUMN = 5;	
	public final static int CONTROL_TYPE_COLUMN = 6;
	public final static int DAILY_CONTROL_COLUMN = 7;
	public final static int MONTHLY_CONTROL_COLUMN = 8;
	public final static int SEASONAL_CONTROL_COLUMN = 9;
	public final static int ANNUAL_CONTROL_COLUMN = 10;

	/** String values for column representation */
	public final static String PAO_NAME_STRING = "Load Group";
	public final static String CONTROL_DATE_STRING = "Date";
	public final static String CONTROL_START_STRING = "Control Start";
	public final static String CONTROL_STOP_STRING = "Control Stop";
	public final static String CONTROL_DURATION_STRING = "Control Duration";
	public final static String ACTIVE_RESTORE_STRING = "Active Restore";
	public final static String CONTROL_TYPE_STRING= "Control Type";
	public final static String DAILY_CONTROL_STRING= "Daily Control";
	public final static String MONTHLY_CONTROL_STRING= "Monthly Control";
	public final static String SEASONAL_CONTROL_STRING= "Seasonal Control";
	public final static String ANNUAL_CONTROL_STRING= "Annual Control";

	/** A string for the title of the data */
	private static String title = "LOAD GROUP ACCOUNTING";
		
	/** Array of IDs (of loadGroup paobjectIDs)*/
	private int loadGroups[] = null;

	/** A flag to show all ActiveRestore types R, T, M, O, N, or C
	 * When true - all types
	 * When false - only R, T, M, O types
	 */
	private boolean showAllActiveRestore = false;
	
	
	public static Comparator lmControlHistoryPAONameComparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			String thisVal = PAOFuncs.getYukonPAOName(((LMControlHistory)o1).getPaObjectID().intValue());
			String anotherVal = PAOFuncs.getYukonPAOName(((LMControlHistory)o2).getPaObjectID().intValue());
			return ( thisVal.compareToIgnoreCase(anotherVal));
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};
	
	public LoadGroupModel()
	{
		super();
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
		this(null, startTime_, stopTime_);
	}	
	
	/**
	 * Constructor class
	 * @param loadGroups_ (Array of)YukonPaobject.paobjectID (of single load group)
	 * @param startTime_ LMControlHistory.startDateTime
	 * @param stopTime_ LMControlHistory.stopDateTime
	 */
	public LoadGroupModel( int[] paoIDs_,long startTime_, long stopTime_)
	{
		super(startTime_, stopTime_);
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
			Integer ctrlHistID = new Integer(rset.getInt(1));
			Integer paoID = new Integer(rset.getInt(2));
			java.sql.Timestamp startTS = rset.getTimestamp(3);
			java.sql.Timestamp stopTS = rset.getTimestamp(4);
			Integer controlDuration = new Integer(rset.getInt(5));
			String controlType = rset.getString(6);
			Integer dailyTime = new Integer(rset.getInt(7));
			Integer monthyTime = new Integer(rset.getInt(8));
			Integer seasonalTime = new Integer(rset.getInt(9));
			Integer annualTime = new Integer(rset.getInt(10));
			String activeRestore = rset.getString(11);
			
			LMControlHistory lmControlHist = new LMControlHistory(ctrlHistID);
			lmControlHist.setPaObjectID(paoID);
			lmControlHist.setStartDateTime(new java.util.Date(startTS.getTime()));
			lmControlHist.setStopDateTime(new java.util.Date(stopTS.getTime()));
			lmControlHist.setControlDuration(controlDuration);
			lmControlHist.setControlType(controlType);
			lmControlHist.setCurrentDailyTime(dailyTime);
			lmControlHist.setCurrentMonthlyTime(monthyTime);
			lmControlHist.setCurrentSeasonalTime(seasonalTime);
			lmControlHist.setCurrentAnnualTime(annualTime);
			lmControlHist.setActiveRestore(activeRestore); 
			getData().add(lmControlHist);
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
		StringBuffer sql = new StringBuffer("SELECT LMCH.LMCTRLHISTID, LMCH.PAOBJECTID, LMCH.STARTDATETIME, LMCH.STOPDATETIME, "+
				" LMCH.CONTROLDURATION, LMCH.CONTROLTYPE, "+
				" LMCH.CURRENTDAILYTIME, LMCH.CURRENTMONTHLYTIME, "+
				" LMCH.CURRENTSEASONALTIME, LMCH.CURRENTANNUALTIME, LMCH.ACTIVERESTORE "+
				" FROM LMCONTROLHISTORY LMCH " + 
				" WHERE (LMCH.StartDateTime > ?) AND (LMCH.StopDateTime <= ?) ");

//				DAVID - 7/29/04 I think the only records you need are the ones with a T, M, O or R.  The N or C (which are the other options I think) aren't necessary				
				if(!isShowAllActiveRestore())
					sql.append(" AND LMCH.ActiveRestore IN ('R', 'T', 'O', 'M') ");

				if( getECIDs() != null)
				{
					sql.append("AND LMCH.PAOBJECTID IN " +
					"(SELECT DISTINCT DG.LMGROUPDEVICEID " +
					" FROM " + UserPaoOwner.TABLE_NAME + " us, " +
					LMProgramDirectGroup.TABLE_NAME + " DG " +
					" WHERE us.PaoID = DG.DEVICEID " +
					" AND us.userID IN (SELECT DISTINCT ECLL.OPERATORLOGINID " +
					" FROM ENERGYCOMPANYOPERATORLOGINLIST ECLL " +
					" WHERE ECLL.ENERGYCOMPANYID IN ( " + getECIDs()[0]);
					for (int i = 1; i < getECIDs().length; i++)
						sql.append(", " + getECIDs()[i]+ " ");
							 
					sql.append(")))");
				}
				if( getPaoIDs()!= null)	//null load groups means ALL groups!
				{
					sql.append(" AND LMCH.PAOBJECTID in (" + getPaoIDs()[0] ); 
					for (int i = 1; i < getPaoIDs().length; i++)
					{
						sql.append(", " + getPaoIDs()[i]+" ");
					}
					sql.append(") ");
				}
				sql.append(" ORDER BY LMCH.PAOBJECTID, LMCH.StartDateTime, LMCH.StopDateTime");
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
				CTILogger.info("START DATE > " + new Timestamp(getStartTime()) + "  -  STOP DATE <= " + new Timestamp(getStopTime()));
				
				rset = pstmt.executeQuery();
				while( rset.next())
				{
					addDataRow(rset);
				}
				if(!getData().isEmpty())
					Collections.sort(getData(), lmControlHistoryPAONameComparator);
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


	/**
	 * Convert seconds of time into hh:mm:ss string.
	 * @param int seconds
	 * @return String in format hh:mm:ss
	 */
	private String convertSecondsToTimeString(int seconds)
	{
		DecimalFormat format = new DecimalFormat("00");
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
		if( o instanceof LMControlHistory)
		{
			LMControlHistory lmch= ((LMControlHistory)o);
			switch( columnIndex)
			{
				case PAO_NAME_COLUMN:
					return PAOFuncs.getYukonPAOName(lmch.getPaObjectID().intValue());
				case CONTROL_DATE_COLUMN:
					return lmch.getStartDateTime();
				case CONTROL_START_TIME_COLUMN:
					return lmch.getStartDateTime();
				case CONTROL_STOP_TIME_COLUMN:
					return lmch.getStopDateTime();
				case CONTROL_DURATION_COLUMN:
					return lmch.getControlDuration();
				case ACTIVE_RESTORE_COLUMN:
					return lmch.getActiveRestore();
				case CONTROL_TYPE_COLUMN:
					return lmch.getControlType();
				case DAILY_CONTROL_COLUMN:
					return convertSecondsToTimeString(lmch.getCurrentDailyTime().intValue());
				case MONTHLY_CONTROL_COLUMN:
					return convertSecondsToTimeString(lmch.getCurrentMonthlyTime().intValue());
				case SEASONAL_CONTROL_COLUMN:
					return convertSecondsToTimeString(lmch.getCurrentSeasonalTime().intValue());
				case ANNUAL_CONTROL_COLUMN:
					return convertSecondsToTimeString(lmch.getCurrentAnnualTime().intValue());
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
				ACTIVE_RESTORE_STRING,
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
				new ColumnProperties(0, 1, 130, null),
				new ColumnProperties(0, 1, 60, "MM/dd/yyyy"),
				new ColumnProperties(60, 1, 50, "HH:mm:ss"),
				new ColumnProperties(110, 1, 50, "HH:mm:ss"),
				new ColumnProperties(160, 1, 50, null),
				new ColumnProperties(210, 1, 50, null),
				new ColumnProperties(260, 1, 190, null),
				new ColumnProperties(450, 1, 50, null),
				new ColumnProperties(500, 1, 50, null),
				new ColumnProperties(0, 1, 100, null),
				new ColumnProperties(0, 1, 100, null)
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
	/**
	 * @return
	 */
	public boolean isShowAllActiveRestore()
	{
		return showAllActiveRestore;
	}

	/**
	 * When true - all types
	 * When false - only R, T, M, O types
	 */
	public void setShowAllActiveRestore(boolean showAll)
	{
		showAllActiveRestore = showAll;
	}
}
	
