/*
 * Created on Sep 22, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.yukon.server.cache;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TOUScheduleLoader implements Runnable {
	
	private String databaseAlias = null;
	private java.util.ArrayList allTOUSchedules = null;
/**
 * TOUScheduleLoader constructor comment.
 */
public TOUScheduleLoader(java.util.ArrayList touSchedules ,String dbAlias) {
	super();
	this.allTOUSchedules = touSchedules ;
	this.databaseAlias = dbAlias;
	
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 3:54:17 PM)
 */
public void run()
{

	//temp code
	java.util.Date timerStart = null;
	java.util.Date timerStop = null;
	//temp code

	//temp code
	timerStart = new java.util.Date();
	//temp code
	String sqlString = "SELECT TOUSCHEDULEID,TOUSCHEDULENAME FROM TOUSCHEDULE WHERE TOUSCHEDULEID >= 0";

	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(this.databaseAlias);
		stmt = conn.createStatement();
		rset = stmt.executeQuery(sqlString);

		while (rset.next())
		{
			int scheduleID = rset.getInt(1);
			String scheduleName = rset.getString(2).trim();

			com.cannontech.database.data.lite.LiteTOUSchedule tou =
				new com.cannontech.database.data.lite.LiteTOUSchedule( scheduleID );
				
			tou.setScheduleName(scheduleName);
			if(scheduleID != 0)
				allTOUSchedules.add(tou);
		}

	}
	catch (java.sql.SQLException e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		catch (java.sql.SQLException e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		//temp code
		timerStop = new java.util.Date();
		com.cannontech.clientutils.CTILogger.info(
			(timerStop.getTime() - timerStart.getTime())*.001 + 
			   " Secs for TOUScheduleLoader (" + allTOUSchedules.size() + " loaded)" );
               
		//temp code
	}

}
}