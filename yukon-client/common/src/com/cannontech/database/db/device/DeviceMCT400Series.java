/*
 * Created on Nov 19, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.device;

import com.cannontech.database.db.DBPersistent;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeviceMCT400Series extends DBPersistent {
	
	private Integer deviceID;
	private Integer disconnectAddress;
	private Integer touScheduleID;
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"DEVICEID", "DISCONNECTADDRESS", "TOUSCHEDULEID"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "DeviceMCT400Series";

/**
 * DeviceMCT400Series constructor comment.
 */
public DeviceMCT400Series() {
	super();
}
/**
 * DeviceMCT400Series constructor comment.
 */
public DeviceMCT400Series(Integer disconn, Integer touID) 
{
	super();
	disconnectAddress = disconn;
	touScheduleID = touID;
}

public DeviceMCT400Series(Integer scheduleID, Integer disconn, Integer touID) 
{
	super();
	deviceID = scheduleID;
	disconnectAddress = disconn;
	touScheduleID = touID;
}

/**
 * Insert the method's description here.
 * Creation date: (11/19/2004 10:34:05 AM)
 */
public void add() throws java.sql.SQLException
{
	Object addValues[] = 
	{ 
		getDeviceID(), getDisconnectAddress(),
		getTOUScheduleID()
	};

	add( TABLE_NAME, addValues );
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2004 10:34:22 AM)
 */
public void delete() throws java.sql.SQLException
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getDeviceID());
}
/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
/*public static boolean deleteDisconnectAddress(Integer deviceID, java.sql.Connection conn)
{
	com.cannontech.database.SqlStatement stmt = null;

	if( conn == null )
		throw new IllegalArgumentException("Database connection should not be (null)");

	try
	{
		java.sql.Statement stat = conn.createStatement();

		stat.execute("DELETE FROM " + TABLE_NAME + " WHERE deviceID=" + deviceID);

		if (stat != null)
			stat.close();
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}

	return true;
}*/

/**
 * Insert the method's description here.
 * Creation date: (11/19/2004 10:40:28 AM)
 * @return java.lang.Integer
 */
public Integer getTOUScheduleID() {
	return touScheduleID;
}

/**
 * Insert the method's description here.
 * Creation date: (11/19/2004 10:40:28 AM)
 * @return java.lang.String
 */
public Integer getDisconnectAddress() {
	return disconnectAddress;
}

/**
 * Insert the method's description here.
 * Creation date: (11/19/2004 10:40:28 AM)
 * @return java.lang.Integer
 */
public Integer getDeviceID() {
	return deviceID;
}

/**
 * Insert the method's description here.
 * Creation date: (11/19/2004 10:40:28 AM)
 */
public void retrieve() 
{
	Integer constraintValues[] = { getDeviceID() };	
	
	try
	{
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
	
		if( results.length == SETTER_COLUMNS.length )
		{
			setDisconnectAddress( (Integer) results[1] );
			setTOUScheduleID( (Integer) results[2] );
		}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}

/**
 * Insert the method's description here.
 * @param newTOUScheduleID java.lang.Integer
 */
public void setTOUScheduleID(Integer newTOUScheduleID) {
	touScheduleID = newTOUScheduleID;
}

/**
 * Insert the method's description here.
 * @param newSwitchRate java.lang.String
 */
public void setDisconnectAddress(Integer newDisconn) {
	disconnectAddress = newDisconn;
}

/**
 * Insert the method's description here.
 * @param newDeviceID java.lang.Integer
 */
public void setDeviceID(Integer newDeviceID) {
	deviceID = newDeviceID;
}

/**
 * Insert the method's description here.
 * Creation date: (11/19/2004 10:40:28 AM)
 */
public void update() 
{
	Object setValues[] =
	{ 
		getDeviceID(), getDisconnectAddress(),
		getTOUScheduleID()
	};
	
	Object constraintValues[] = { getDeviceID() };
	
	try
	{
		update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
}

public final static boolean hasDisconnectAddress(Integer mctID) throws java.sql.SQLException 
{	
	return hasDisconnectAddress(mctID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasDisconnectAddress(Integer mctID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT DisconnectAddress FROM " + TABLE_NAME + " WHERE DeviceID=" + mctID,
													databaseAlias );

	try
	{
		stmt.execute();
		return (stmt.getRowCount() > 0 );
	}
	catch( Exception e )
	{
		return false;
	}
}

public final static boolean hasTOUSchedule(Integer mctID) throws java.sql.SQLException 
{	
	return hasTOUSchedule(mctID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasTOUSchedule(Integer mctID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT TOUScheduleID FROM " + TABLE_NAME + " WHERE DeviceID=" + mctID,
													databaseAlias );

	try
	{
		stmt.execute();
		return (stmt.getRowCount() > 0 );
	}
	catch( Exception e )
	{
		return false;
	}
}
}

	