package com.cannontech.yukon.server.cache;

import java.util.ArrayList;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;


/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class DeviceMeterGroupLoader implements Runnable
{
	private ArrayList<LiteDeviceMeterNumber> devMetNumList = null;
	private String databaseAlias = null;
/**
 * DeviceLoader constructor comment.
 */
public DeviceMeterGroupLoader(ArrayList<LiteDeviceMeterNumber> deviceList, String alias) {
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
	String sqlString = "SELECT DeviceId, MeterNumber, Type FROM DeviceMeterGroup dmg " + 
	                    " JOIN YukonPaobject pao ON dmg.DeviceId = pao.PaobjectId " +
	                    " ORDER BY MeterNumber";

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
			String paoTypeStr = rset.getString(3);
			PaoType paoType = PaoType.getForDbString(paoTypeStr);
		    
			LiteDeviceMeterNumber liteDevMetNum = new LiteDeviceMeterNumber(deviceID, meterNumber, paoType);
			devMetNumList.add(liteDevMetNum);
		}
	}
	catch (java.sql.SQLException e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, stmt, conn );

		com.cannontech.clientutils.CTILogger.info( 
            ((System.currentTimeMillis() - timer)*.001) + 
               " Secs for DeviceMeterGroupLoader (" + devMetNumList.size() + " loaded)" );
	}
}
}
