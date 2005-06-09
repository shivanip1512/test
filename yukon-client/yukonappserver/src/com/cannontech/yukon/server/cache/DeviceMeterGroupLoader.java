package com.cannontech.yukon.server.cache;

import com.cannontech.database.data.lite.LiteDeviceMeterNumber;


/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class DeviceMeterGroupLoader implements Runnable
{
	private java.util.ArrayList devMetNumList = null;
	private String databaseAlias = null;
/**
 * DeviceLoader constructor comment.
 */
public DeviceMeterGroupLoader(java.util.ArrayList deviceList, String alias) {
	super();
	this.devMetNumList = deviceList;
	this.databaseAlias = alias;
}
/**
 * run method comment.
 */
public void run()
{
	long timer = System.currentTimeMillis();
	String sqlString = "SELECT DEVICEID, METERNUMBER, COLLECTIONGROUP, TESTCOLLECTIONGROUP, BILLINGGROUP FROM DEVICEMETERGROUP ORDER BY METERNUMBER";

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
			int deviceID = rset.getInt(1);
			String meterNumber = rset.getString(2).trim();
			String collGrp = rset.getString(3).trim();
			String testCollGrp = rset.getString(4).trim();
			String billGrp = rset.getString(5).trim();
			
			//20050602 - SN - removed this check, we want all of them in the cache
//			if(meterNumber.compareToIgnoreCase("default") != 0)
//			{
				LiteDeviceMeterNumber liteDevMetNum = new LiteDeviceMeterNumber(deviceID, meterNumber, collGrp, testCollGrp, billGrp);
				devMetNumList.add(liteDevMetNum);
//			}
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

		com.cannontech.clientutils.CTILogger.info( 
            ((System.currentTimeMillis() - timer)*.001) + 
               " Secs for DeviceMeterGroupLoader (" + devMetNumList.size() + " loaded)" );
	}
}
}
