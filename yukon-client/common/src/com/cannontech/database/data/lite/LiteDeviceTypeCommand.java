package com.cannontech.database.data.lite;

import com.cannontech.clientutils.CTILogger;

/*
 */
public class LiteDeviceTypeCommand extends LiteBase
{
	private int commandID = 0;
	private String deviceType = null;
	private int displayOrder = 0;
	private char visibleFlag = 'Y';
		
	/**
	 * LiteDeviceTypeCommand
	 */
	public LiteDeviceTypeCommand( int devCmdID) 
	{
		super();
		setLiteID(devCmdID);
		setLiteType(LiteTypes.DEVICE_TYPE_COMMAND);
	}
	
	/**
	 * LiteDeviceTypeCommand
	 */
	public LiteDeviceTypeCommand( int devCmdID, int cmdID, String devType, int order, char visible ) 
	{
		this( devCmdID );
		setCommandID(cmdID);
		setDeviceType(devType);
		setDisplayOrder(order);
		setVisibleFlag(visible);
	}
	
	/**
	 * retrieve method comment.
	 */
	public void retrieve(String databaseAlias) 
	{
	   //nearly the same as the DeviceTypeLoader's run() method
	   String sqlString = "SELECT COMMANDID, DISPLAYORDER, VISIBLEFLAG, DEVICETYPE " +
	            " FROM DEVICETYPECOMMAND WHERE DEVICECOMMANDID = " + getDeviceCommandID();
	
	
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
				setDisplayOrder(rset.getInt(2));
				setVisibleFlag(rset.getString(3).charAt(0));
				setDeviceType(rset.getString(4));
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
		return getCommandID() +" " + getDeviceType();
	}


	/**
	 * @return
	 */
	public int getCommandID()
	{
		return commandID;
	}

	/**
	 * @return
	 */
	public String getDeviceType()
	{
		return deviceType;
	}

	/**
	 * @return
	 */
	public int getDisplayOrder()
	{
		return displayOrder;
	}

	/**
	 * @return
	 */
	public char getVisibleFlag()
	{
		return visibleFlag;
	}

	/**
	 * @param i
	 */
	public void setCommandID(int i)
	{
		commandID = i;
	}

	/**
	 * @param string
	 */
	public void setDeviceType(String string)
	{
		deviceType = string;
	}

	/**
	 * @param i
	 */
	public void setDisplayOrder(int i)
	{
		displayOrder = i;
	}

	/**
	 * @param c
	 */
	public void setVisibleFlag(char c)
	{
		visibleFlag = c;
	}

	/**
	 * @return
	 */
	public int getDeviceCommandID()
	{
		return getLiteID();
	}

	/**
	 * @param i
	 */
	public void setDeviceCommandID(int i)
	{
		setLiteID(i);
	}
	public boolean isVisible()
	{
		if( getVisibleFlag() == 'Y' )
			return true;
		return false;
	}

}
