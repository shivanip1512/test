package com.cannontech.analysis.tablemodel;

import java.util.HashMap;

import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.data.activity.ActivityLog;

/**
 * Created on Dec 15, 2003
 * StatisticReportDatabase TableModel object
 * Abstract class for all Statistical report tableModels to extend.
 * Extending classes must implement:
 *   addDataRow(ResultSet)	- add a "row" object to the data Vector
 *   buildSQLStatement()	- Returns the sql query statment
 * 
 * Contains the start and stop times for query information.
 * Contains the paoClass - YukonPaobject.paoClass
 * 				category - YukonPaobject.category
 * 				statType - DynamicPaoStatistics.statisticType
 * @author snebben
 */
public class ActivityModel extends ReportModelBase
{
	/** Class fields */
	private Integer energyCompanyID = null;
	
	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public ActivityModel(long startTime_, long stopTime_)
	{
		super(startTime_, stopTime_);//default type
		setReportType(ReportTypes.ENERGY_COMPANY_ACTIVITY_LOG_DATA);		
	}

	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public ActivityModel()
	{
		super();//default type
		setReportType(ReportTypes.ENERGY_COMPANY_ACTIVITY_LOG_DATA);		
	}

	/**
	 * Constructor class
	 * @param statType_ DynamicPaoStatistics.StatisticType
	 */
	public ActivityModel(int energyCompanyID_)
	{
		this();//default type
		setEnergyCompanyID(energyCompanyID);
	}
	
	/* (non-Javadoc)
	 * @see com.cannontech.analysis.data.ReportModelBase#collectData()
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
				//pstmt.setTimestamp(2, new java.sql.Timestamp( getStopTime()));
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
			com.cannontech.clientutils.CTILogger.error(" DB : Standard SQL Builder did not work, trying with a non SQL-92 query");
			//try using a nonw SQL-92 method, will be slower
			//  Oracle 8.1.X and less will use this
			runNonSQL92Statement();
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
		com.cannontech.clientutils.CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}
		 
	/**
	 * Build the SQL statement to retrieve <StatisticDeviceDataBase> data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer("SELECT AL.ENERGYCOMPANYID, AL.CUSTOMERID, AL.ACCOUNTID, CA.ACCOUNTNUMBER, ACTION, " + 
		" COUNT(ACTIVITYLOGID) AS ACTIONCOUNT " + 
		" FROM ACTIVITYLOG AL LEFT OUTER JOIN CUSTOMERACCOUNT CA " +
		" ON CA.ACCOUNTID = AL.ACCOUNTID " + 
		" WHERE AL.TIMESTAMP >= ? ");
		if( getEnergyCompanyID() != null )
			sql.append(" AND AL.ENERGYCOMPANYID = " + getEnergyCompanyID());
		
		sql.append(" GROUP BY AL.ENERGYCOMPANYID, AL.CUSTOMERID, AL.ACCOUNTID, CA.ACCOUNTNUMBER, ACTION " +
					" ORDER BY AL.ENERGYCOMPANYID, AL.CUSTOMERID, CA.ACCOUNTNUMBER, ACTION");
		return sql;
		
	}
	/**
	 * Build the SQL statement to retrieve <StatisticDeviceDataBase> data.
	 * @return StringBuffer  an sqlstatement
	 */
	public void runNonSQL92Statement()
	{
		
		int rowCount = 0;
			
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
				StringBuffer sql = new StringBuffer("SELECT DISTINCT ACCOUNTID, ACCOUNTNUMBER FROM CUSTOMERACCOUNT");
				pstmt = conn.prepareStatement(sql.toString());
				rset = pstmt.executeQuery();
				HashMap acctNumHash = new HashMap();
				while( rset.next())
				{
					Integer acctID = new Integer(rset.getInt(1));
					String acctNum = rset.getString(2);
					acctNumHash.put(acctID, acctNum);
				}
					
				sql = new StringBuffer("SELECT ENERGYCOMPANYID, CUSTOMERID, ACCOUNTID, ACTION, " + 
					" COUNT(ACTIVITYLOGID) AS ACTIONCOUNT " +
					" FROM ACTIVITYLOG " +
					" WHERE TIMESTAMP >= ? ");
				if( getEnergyCompanyID() != null)
					sql.append(" AND ENERGYCOMPANYID = " + getEnergyCompanyID());

				sql.append(" GROUP BY ENERGYCOMPANYID, CUSTOMERID, ACCOUNTID, ACTION " +
							" ORDER BY ENERGYCOMPANYID, CUSTOMERID, ACCOUNTID, ACTION");
				
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( getStartTime() ));
				//pstmt.setTimestamp(2, new java.sql.Timestamp( getStopTime()));
				com.cannontech.clientutils.CTILogger.info("START DATE > " + new java.sql.Timestamp(getStartTime()) + "  -  STOP DATE <= " + new java.sql.Timestamp(getStopTime()));
				/*java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
				tempCal.add(java.util.Calendar.DATE, -90);
				stmt.setTimestamp(1, new java.sql.Timestamp(tempCal.getTime().getTime()));
				System.out.println( "DATE > "+ tempCal.getTime());*/
				rset = pstmt.executeQuery();
				while( rset.next())
				{
					Integer ecID = new Integer(rset.getInt(1));
					Integer custID = new Integer(rset.getInt(2));
					Integer acctID = new Integer(rset.getInt(3));
					String action = rset.getString(4);
					Integer count = new Integer(rset.getInt(5));
					//ENERGYCOMPANYID, CUSTOMERID, ACCOUNTID, ACTION, COUNT(ACTIVITYLOGID) AS ACTIONCOUNT 

					String acctNum = (String)acctNumHash.get(acctID);

					ActivityLog al = new ActivityLog(ecID, custID, acctNum, count, action);
					getData().add(al); 
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
		return;
	}
	
	/**
	 * Add <innerClass> objects to data, retrieved from rset.
	 * @param ResultSet rset
	 */
	public void addDataRow(java.sql.ResultSet rset)
	{
		try
		{
			Integer ecID = new Integer(rset.getInt(1));
			Integer custID = new Integer(rset.getInt(2));
			Integer acctID = new Integer(rset.getInt(3));
			String acctNum = rset.getString(4);
			String action = rset.getString(5);
			Integer count = new Integer(rset.getInt(6));
			//AL.ENERGYCOMPANYID, AL.CUSTOMERID, AL.ACCOUNTID, CA.ACCOUNTNUMBER, ACTION, COUNT(ACTIVITYLOGID) AS ACTIONCOUNT
	
			ActivityLog al = new ActivityLog(ecID, custID, acctNum, count, action);
			getData().add(al);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	public Integer getEnergyCompanyID()
	{
		return energyCompanyID;
	}

	/**
	 * @param i
	 */
	public void setEnergyCompanyID(Integer ecID)
	{
		energyCompanyID = ecID;
	}

}
