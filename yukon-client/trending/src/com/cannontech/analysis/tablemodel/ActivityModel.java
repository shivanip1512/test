package com.cannontech.analysis.tablemodel;



import java.util.Date;

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
	private int energyCompanyID = -1;
	private int userID = -1;
	private int customerID = -1;
	
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
		com.cannontech.clientutils.CTILogger.info("Report Records Collected from Database: " + getData().size());
		return;
	}
		 
	/**
	 * Build the SQL statement to retrieve <StatisticDeviceDataBase> data.
	 * @return StringBuffer  an sqlstatement
	 */
	public StringBuffer buildSQLStatement()
	{
		StringBuffer sql = new StringBuffer("SELECT AL.ENERGYCOMPANYID, AL.CUSTOMERID, " + 
		" AL.USERID, AL.ACCOUNTID, AL.PAOID, AL.TIMESTAMP, AL.ACTION, AL.DESCRIPTION " +
		" FROM ACTIVITYLOG AL " +
		" WHERE AL.TIMESTAMP >= ? ");
		if( getEnergyCompanyID() > -1 )
			sql.append(" AND AL.ENERGYCOMPANYID = " + getEnergyCompanyID());
		
		if( getCustomerID() > -1)
			sql.append(" AND AL.CUSTOMERID = " + getCustomerID());
			
		if( getUserID() > -1 )
			sql.append(" AND AL.USERID = " + getUserID());
		
		sql.append(" ORDER BY AL.ENERGYCOMPANYID, AL.CUSTOMERID, AL.USERID, AL.ACCOUNTID, AL.TIMESTAMP");
		return sql;
		
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
			Integer userID = new Integer(rset.getInt(3));
			Integer acctID = new Integer(rset.getInt(4));
			Integer paoID = new Integer(rset.getInt(5));
			java.sql.Timestamp time  = rset.getTimestamp(6);
			String action = rset.getString(7);
			String desc = rset.getString(8);
			
			ActivityLog actData = (ActivityLog)ReportTypes.create(getReportType());
			
			actData.setEnergyCompanyID(ecID);
			actData.setCustomerID(custID);
			actData.setUserID(userID);
			actData.setAccountID(acctID);
			actData.setPaoID(paoID);
			actData.setTimestamp(new Date(time.getTime()));
			actData.setAction(action);
			actData.setDescription(desc);

			getData().add(actData);
		}
		catch(java.sql.SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	public int getCustomerID()
	{
		return customerID;
	}

	/**
	 * @return
	 */
	public int getUserID()
	{
		return userID;
	}

	/**
	 * @param i
	 */
	public void setCustomerID(int i)
	{
		customerID = i;
	}

	/**
	 * @param i
	 */
	public void setUserID(int i)
	{
		userID = i;
	}

	/**
	 * @return
	 */
	public int getEnergyCompanyID()
	{
		return energyCompanyID;
	}

	/**
	 * @param i
	 */
	public void setEnergyCompanyID(int i)
	{
		energyCompanyID = i;
	}

}
