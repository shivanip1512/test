/*
 * Created on Apr 14, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.activity;

import java.util.Date;
import java.util.Vector;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.activity.ActivityLog;
import com.cannontech.util.ServletUtil;

/**
 * @author snebben
 *
 * Class used to return a small collection of summary data.
 * Makes data easily accesible in web pages.
 * Contains an action, the number of times the action occured and the maximum timestamp of occurance. 
 */
public class ActivityLogSummary
{
	public class LogSummary {
		public String action = "";
		public int count = 0;
		public Date maxTimeStamp= null;
	}
	/* contains LogSummary (action, count, timestamp) values.*/
	private Vector logSummaryVector = null;
	private long startTS = ServletUtil.getToday().getTime();
	private long stopTS = ServletUtil.getTomorrow().getTime();
	private int userID = -1;
	private int energyCompanyID = -1;

	/**
	 * Constructor user default start/stop times of today/tomorrow.
	 */		
	public ActivityLogSummary(int userID_, int energyCompanyID_)
	{
		super();
		userID = userID_;
		energyCompanyID = energyCompanyID_;
	}
	public ActivityLogSummary(int userID_, int energyCompanyID_, long startTime_, long stopTime_)
	{
		super();
		userID = userID_;
		energyCompanyID = energyCompanyID_;
		startTS = startTime_;
		stopTS = stopTime_;
	}
	
	public void retrieve()
	{
		java.util.ArrayList tmpList = new java.util.ArrayList(30);
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		//get all the unused customers for Curtailment	
		String sql = "SELECT ACTION, COUNT(*), MAX(TIMESTAMP) FROM " + 
						ActivityLog.TABLE_NAME +              
						" WHERE USERID = " + userID +
						" AND ENERGYCOMPANYID  = " + energyCompanyID + 
						" AND TIMESTAMP > ?  AND TIMESTAMP <= ?" + 
						" GROUP BY ACTION";

		try
		{		
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());

			if( conn == null )
			{
				throw new IllegalStateException("Error getting database connection.");
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setTimestamp(1, new java.sql.Timestamp( startTS ));
				pstmt.setTimestamp(2, new java.sql.Timestamp( stopTS ));

				rset = pstmt.executeQuery();

				while( rset.next() )
				{
					LogSummary summary = new LogSummary();
					summary.action = rset.getString(1);
					summary.count = rset.getInt(2);
					java.sql.Timestamp ts = rset.getTimestamp(3);
					summary.maxTimeStamp = new Date(ts.getTime());
					
					getLogSummaryVector().add(summary);
				}
			}		
		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if( pstmt != null ) pstmt.close();
				if( conn != null ) conn.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
			}	
		}
	}	
	/**
	 * @return
	 */
	public Vector getLogSummaryVector()
	{
		if( logSummaryVector == null)
			logSummaryVector = new Vector();
		return logSummaryVector;
	}
}
