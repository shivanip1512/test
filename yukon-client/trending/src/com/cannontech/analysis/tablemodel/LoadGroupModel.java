package com.cannontech.analysis.tablemodel;

import java.sql.ResultSet;

import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.data.lm.LGAccounting;

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
		
	/** Array of IDs (of loadGroup paobjectIDs)*/
	private int loadGroups[] = null;

	/** Number of columns */
	protected final int NUMBER_COLUMNS = 10;
	
	
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
		super();
		setStartTime(startTime_);
		setStopTime(stopTime_);
		setReportType(reportType_);
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
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartTime()));
				pstmt.setTimestamp(2, new java.sql.Timestamp( getStopTime()));
				com.cannontech.clientutils.CTILogger.info("START DATE > " + new java.sql.Timestamp(getStartTime()) + "  -  STOP DATE <= " + new java.sql.Timestamp(getStopTime()));
				
				com.cannontech.clientutils.CTILogger.info(sql.toString());
				
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

	
}
	
