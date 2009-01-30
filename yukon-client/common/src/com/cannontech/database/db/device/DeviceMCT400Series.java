/*
 * Created on Nov 19, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.device;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.db.DBPersistent;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeviceMCT400Series extends DBPersistent {
	
	private Integer deviceID;
	private Integer disconnectAddress = new Integer(-1);
	private Integer touScheduleID = new Integer(0);
	
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

public final static boolean hasExistingDisconnectAddress(Integer mctID) 
{	
	return hasExistingDisconnect(mctID, CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasExistingDisconnect(Integer mctID, String databaseAlias) 
{
	SqlStatement stmt = 
        new SqlStatement("SELECT DisconnectAddress FROM " + TABLE_NAME + " WHERE DeviceID=" + mctID,
                         databaseAlias );

	try {
		stmt.execute();
		return (stmt.getRowCount() > 0 );
        
	} catch( Exception e ) {
        CTILogger.error( e );
		return false;
	}
}

public final static boolean hasExistingTOUSchedule(Integer mctID) 
{	
	return hasExistingTOUSchedule(mctID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasExistingTOUSchedule(Integer mctID, String databaseAlias) 
{
	SqlStatement stmt =
        new SqlStatement("SELECT TOUScheduleID FROM " + TABLE_NAME + " WHERE DeviceID=" + mctID,
                         databaseAlias );

	try {
		stmt.execute();
		return (stmt.getRowCount() > 0 );
        
	} catch( Exception e ) {
        CTILogger.error( e );
		return false;
	}
}

/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteAnAddress(Integer mctID, java.sql.Connection conn)
{
	java.sql.PreparedStatement pstmt = null;		
	String sql = "DELETE FROM " + TABLE_NAME + " WHERE DeviceID=" + mctID;

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be (null)");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.executeUpdate();
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
			if( pstmt != null ) 
				pstmt.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}

	return true;
}

public void deleteAnAddress(Integer mctID)
{
	try
	{
		java.sql.Connection conn = null;
	
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
	
		deleteAnAddress(mctID, conn);
		
		conn.close();
	}
	catch( java.sql.SQLException e2 )
	{
		com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
	}	
}
}

	