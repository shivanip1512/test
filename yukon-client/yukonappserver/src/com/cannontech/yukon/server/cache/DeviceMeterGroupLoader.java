package com.cannontech.yukon.server.cache;

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
	String sqlString = "SELECT DEVICEID, METERNUMBER, COLLECTIONGROUP FROM DEVICEMETERGROUP ORDER BY METERNUMBER";

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

			if(meterNumber.compareToIgnoreCase("default") != 0)
			{
				com.cannontech.database.data.lite.LiteDeviceMeterNumber liteDevMetNum = new com.cannontech.database.data.lite.LiteDeviceMeterNumber(deviceID, meterNumber, collGrp);
				devMetNumList.add(liteDevMetNum);
			}
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
