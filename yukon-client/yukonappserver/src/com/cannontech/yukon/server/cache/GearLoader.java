/*
 * Created on May 17, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.yukon.server.cache;

import java.util.List;

import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteGear;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class GearLoader implements Runnable {
	private String databaseAlias = null;
	private List<LiteGear> allGears = null;
/**
 * GearLoader constructor comment.
 */
public GearLoader(List<LiteGear> gears ,String dbAlias) {
	super();
	this.allGears = gears ;
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
	String sqlString = "SELECT GEARID, GEARNAME, CONTROLMETHOD, DEVICEID, GEARNUMBER FROM LMPROGRAMDIRECTGEAR WHERE GEARID >= 0 ORDER BY DEVICEID";

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
			int gearID = rset.getInt(1);
			String gearName = rset.getString(2).trim();
			String gearType = rset.getString(3).trim();
			int ownerID = rset.getInt(4);
			int gearNumber = rset.getInt(5);

			LiteGear gear = new LiteGear( gearID );
				
			gear.setGearName(gearName);
			gear.setGearType(gearType);
			gear.setOwnerID(ownerID);
			gear.setGearNumber(gearNumber);

			allGears.add(gear);
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
			   " Secs for GearLoader (" + allGears.size() + " loaded)" );
               
		//temp code
	}

}
}