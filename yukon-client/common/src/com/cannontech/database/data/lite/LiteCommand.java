package com.cannontech.database.data.lite;

import com.cannontech.clientutils.CTILogger;

/*
 */
public class LiteCommand extends LiteBase
{
	private String command = null;
	private String label = null;
	private String category = null;
	/**
	 * LiteCommand
	 */
	public LiteCommand( int cmdID) 
	{
		super();
		setLiteID(cmdID);
		setLiteType(LiteTypes.COMMAND);
	}
	
	/**
	 * LiteCommand
	 */
	public LiteCommand( int cmdID, String cmd, String label, String category) 
	{
		this( cmdID );
		setCommand(cmd);
		setLabel(label);
		setCategory(category);
	}
	
	/**
	 * retrieve method comment.
	 */
	public void retrieve(String databaseAlias) 
	{
	   //nearly the same as the DeviceTypeLoader's run() method
	   String sqlString = "SELECT C.COMMANDID, C.COMMAND, C.LABEL, C.CATEGORY " +
	            " FROM COMMAND C WHERE C.COMMANDID = "+ getCommandID();

		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( databaseAlias );
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);
	
			while( rset.next() )
			{
				setCommandID(rset.getInt(1));
				setCommand(rset.getString(2).trim());
				setLabel(rset.getString(3).trim());
				setCategory(rset.getString(4).trim());
			}
		}
		catch( Exception  e )
		{
			CTILogger.error( e.getMessage(), e);
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
				CTILogger.error( e.getMessage(), e );
			}
		}
	}
	
	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public String toString()
	{
		return getLabel() + " : " + getCommand();
	}

	/**
	 * @return
	 */
	public String getCommand()
	{
		return command;
	}

	/**
	 * @return
	 */
	public int getCommandID()
	{
		return getLiteID();
	}

	/**
	 * @return
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * @param string
	 */
	public void setCommand(String string)
	{
		command = string;
	}

	/**
	 * @param i
	 */
	public void setCommandID(int i)
	{
		setLiteID(i);
	}
	/**
	 * @param string
	 */
	public void setLabel(String string)
	{
		label = string;
	}
	/**
	 * @return
	 */
	public String getCategory()
	{
		return category;
	}

	/**
	 * @param string
	 */
	public void setCategory(String string)
	{
		category = string;
	}

}
