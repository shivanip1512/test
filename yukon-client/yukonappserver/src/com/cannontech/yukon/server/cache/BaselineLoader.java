package com.cannontech.yukon.server.cache;

import com.cannontech.database.SqlUtils;

/**
 * Insert the type's description here.
 * Creation date: (8/24/2001 10:53:23 AM)
 * @author: 
 */
public class BaselineLoader implements Runnable {
	private String databaseAlias = null;
	private java.util.ArrayList allBaselines = null;
/**
 * HolidayScheduleLoader constructor comment.
 */
public BaselineLoader(java.util.ArrayList baselines ,String dbAlias) {
	super();
	this.allBaselines = baselines ;
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
	String sqlString = "SELECT BASELINEID, BASELINENAME FROM BASELINE WHERE BASELINEID >= 0";

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
			int baselineID = rset.getInt(1);
			String baselineName = rset.getString(2).trim();

			com.cannontech.database.data.lite.LiteBaseline basil =
				new com.cannontech.database.data.lite.LiteBaseline( baselineID );
				
			basil.setBaselineName(baselineName);

			allBaselines.add(basil);
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
               " Secs for BaselineLoader (" + allBaselines.size() + " loaded)" );
               
		//temp code
	}

}
}
