package com.cannontech.database.data.lite;

/**
 * Insert the type's description here.
 * Creation date: (9/5/2001 5:12:39 PM)
 * @author: 
 */
public class LiteDeviceMeterNumber extends LiteBase
{
	String meterNumber = null;
/**
 * LiteDeviceMeterNumber constructor comment.
 */
public LiteDeviceMeterNumber() {
	super();
}
/**
 * LiteDeviceMeterNumber constructor comment.
 */
public LiteDeviceMeterNumber(int devID)
{
	super();
	setDeviceID(devID);
	setLiteType(LiteTypes.DEVICE_METERNUMBER);
}
/**
 * LiteDeviceMeterNumber constructor comment.
 */
public LiteDeviceMeterNumber(int devID, String meterNum)
{
	super();
	setDeviceID(devID);
	meterNumber = new String(meterNum);

	setLiteType(LiteTypes.DEVICE_METERNUMBER);	
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public int getDeviceID() {
	return getLiteID();
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String getMeterNumber() {
	return meterNumber;
}
/**
 * retrieve method comment.
 */
public void retrieve(String databaseAlias) 
{
	
 	String sqlString = "SELECT METERNUMBER FROM DEVICEMETERGROUP WHERE DEVICEID = " + getDeviceID();

	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	try 
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( databaseAlias );
		stmt = conn.createStatement();
		rset = stmt.executeQuery(sqlString);

		while (rset.next())
		{
			setMeterNumber( rset.getString(1) );
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

	}

}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setDeviceID(int newValue) 
{
	setLiteID(newValue);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public void setMeterNumber(String newValue) {
	this.meterNumber = new String(newValue);
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String toString() {
	return meterNumber;
}
}
