/*
 * Created on Dec 17, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.yukon.server.cache;

import java.util.List;

import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteTOUDay;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TOUDayLoader implements Runnable {
	
	private String databaseAlias = null;
	private List<LiteTOUDay> allTOUDays = null;
/**
 * TOUScheduleLoader constructor comment.
 */
public TOUDayLoader(List<LiteTOUDay> touDays ,String dbAlias) {
	super();
	this.allTOUDays = touDays ;
	this.databaseAlias = dbAlias;
	
}
/**
 * Insert the method's description here.
 * Creation date: (12/17/2004 3:54:17 PM)
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
	String sqlString = "SELECT d.TOUDayID, d.TOUDayName, dm.TOUDayOffset, dm.TOUScheduleID FROM TOUDay d, TOUDayMapping dm " +
			" WHERE DM.toudayid = d.toudayid ORDER BY dm.TOUScheduleID";

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
			int dayID = rset.getInt(1);
			String dayName = rset.getString(2).trim();
			int dayOffset = rset.getInt(3);
			int scheduleID = rset.getInt(4);

			LiteTOUDay day = new LiteTOUDay( dayID, dayName, dayOffset, scheduleID );
				
			if(scheduleID != 0)
				allTOUDays.add(day);
		}

	}
	catch (java.sql.SQLException e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, stmt, conn );
		//temp code
		timerStop = new java.util.Date();
		com.cannontech.clientutils.CTILogger.info(
			(timerStop.getTime() - timerStart.getTime())*.001 + 
			   " Secs for TOUDayLoader (" + allTOUDays.size() + " loaded)" );
               
		//temp code
	}

}
}