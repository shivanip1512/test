/*
 * Created on May 4, 2004
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
public class SeasonLoader implements Runnable {
	private String databaseAlias = null;
	private java.util.ArrayList allSeasons = null;
/**
 * SeasonLoader constructor comment.
 */
public SeasonLoader(java.util.ArrayList seasons ,String dbAlias) {
	super();
	this.allSeasons = seasons ;
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
	String sqlString = "SELECT SCHEDULEID,SCHEDULENAME FROM SEASONSCHEDULE WHERE SCHEDULEID >= 0";

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
			int seasonID = rset.getInt(1);
			String seasonName = rset.getString(2).trim();

			com.cannontech.database.data.lite.LiteSeason ss =
				new com.cannontech.database.data.lite.LiteSeason( seasonID );
				
			ss.setSeasonName(seasonName);

			allSeasons.add(ss);
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
			   " Secs for SeasonLoader (" + allSeasons.size() + " loaded)" );
               
		//temp code
	}

}
}