/*
 * Created on Dec 18, 2003
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

public class ConfigLoader implements Runnable {
	private String databaseAlias = null;
	private java.util.ArrayList allConfigs = null;
/**
 * HolidayScheduleLoader constructor comment.
 */
public ConfigLoader(java.util.ArrayList configs ,String dbAlias) {
	super();
	this.allConfigs = configs ;
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
	String sqlString = "SELECT CONFIGID, CONFIGNAME FROM MCTCONFIG WHERE CONFIGID >= 0";

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
			int configID = rset.getInt(1);
			String configName = rset.getString(2).trim();

			com.cannontech.database.data.lite.LiteConfig basil =
				new com.cannontech.database.data.lite.LiteConfig( configID );
				
			basil.setConfigName(configName);

			allConfigs.add(basil);
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
			   " Secs for ConfigLoader (" + allConfigs.size() + " loaded)" );
               
		//temp code
	}

}
}
