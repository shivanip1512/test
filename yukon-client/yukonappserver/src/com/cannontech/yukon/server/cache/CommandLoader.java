package com.cannontech.yukon.server.cache;

import java.util.Map;

import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.db.command.Command;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class CommandLoader implements Runnable 
{
	private java.util.ArrayList allCommands = null;
	//<Integer(commandID), LiteCommand>
	private Map allCommandsMap = null;
	private String databaseAlias = null;

/**
 * DeviceTypeCommandLoader constructor comment.
 */
public CommandLoader(java.util.ArrayList commandsArray_, Map commandsMap_, String alias) 
{
	super();
	this.allCommands = commandsArray_;
	this.allCommandsMap = commandsMap_;
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

	String sqlString = "SELECT COMMANDID, COMMAND, LABEL, CATEGORY " +
						" FROM " + Command.TABLE_NAME;
						
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
			int cmdId = rset.getInt(1);
			String command = rset.getString(2);
			String label = rset.getString(3);
			String category = rset.getString(4);
			LiteCommand lc = new LiteCommand(cmdId, command, label, category);
			allCommands.add(lc);
			allCommandsMap.put( new Integer(cmdId), lc );
			
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
      " Secs for Commands (" + allCommands.size() + " loaded)" );
//temp code
	}
}

}