package com.cannontech.yukon.server.cache;

import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.db.command.DeviceTypeCommand;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class DeviceTypeCommandLoader implements Runnable 
{
	private java.util.ArrayList allDeviceTypeCommands = null;
	private String databaseAlias = null;

/**
 * DeviceTypeCommandLoader constructor comment.
 */
public DeviceTypeCommandLoader(java.util.ArrayList devTypeCommandsArray_, String alias) 
{
	super();
	this.allDeviceTypeCommands = devTypeCommandsArray_;
	this.databaseAlias = alias;
}

/**
 * run method comment.
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

	String sqlString = "SELECT DEVICECOMMANDID, COMMANDID, DEVICETYPE, DISPLAYORDER, VISIBLEFLAG " +
						" FROM " + DeviceTypeCommand.TABLE_NAME + 
						" ORDER BY DEVICETYPE, DISPLAYORDER ";

	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( this.databaseAlias );
		stmt = conn.createStatement();
		rset = stmt.executeQuery(sqlString);

		while (rset.next())
		{
			int devCmdId = rset.getInt(1);
			int cmdId = rset.getInt(2);
			String devType = rset.getString(3);
			if( devType == null)
				System.out.println("HERRE ");
			int order = rset.getInt(4);
			char visible = rset.getString(5).charAt(0);
			LiteDeviceTypeCommand ldtc = new LiteDeviceTypeCommand(devCmdId, cmdId, devType, order, visible);
			allDeviceTypeCommands.add(ldtc);
		}
		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		try
		{
			if( stmt != null )
				stmt.close();
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
//temp code
timerStop = new java.util.Date();
com.cannontech.clientutils.CTILogger.info( 
    (timerStop.getTime() - timerStart.getTime())*.001 + 
      " Secs for DeviceTypeCommands (" + allDeviceTypeCommands.size() + " loaded)" );
//temp code
	}
}

}