package com.cannontech.yukon.server.cache;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 10:53:23 AM)
 * @author: 
 */
public class HolidayScheduleLoader implements Runnable {
	private String databaseAlias = null;
	private java.util.ArrayList allHolidaySchedules = null;
/**
 * HolidayScheduleLoader constructor comment.
 */
public HolidayScheduleLoader(java.util.ArrayList holidaySchedules ,String dbAlias) {
	super();
	this.allHolidaySchedules = holidaySchedules ;
	this.databaseAlias = dbAlias;
	
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/2001 10:54:17 AM)
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
	String sqlString = "SELECT HOLIDAYSCHEDULEID,HOLIDAYSCHEDULENAME FROM HOLIDAYSCHEDULE WHERE HOLIDAYSCHEDULEID > 0";

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
			int holidayScheduleID = rset.getInt(1);
			String holidayScheduleName = rset.getString(2).trim();

			com.cannontech.database.data.lite.LiteHolidaySchedule hs =
				new com.cannontech.database.data.lite.LiteHolidaySchedule( holidayScheduleID );
				
			hs.setHolidayScheduleName(holidayScheduleName);

			allHolidaySchedules.add(hs);
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
               " Secs for HolidayScheduleLoader (" + allHolidaySchedules.size() + " loaded)" );
               
		//temp code
	}

}
}
