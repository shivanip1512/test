/*
 * Created on Sep 22, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.tou;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TOUDeviceMapping extends com.cannontech.database.db.DBPersistent {
	
	private Integer deviceID;
	private Integer scheduleID;

	public static final String SETTER_COLUMNS[] = { "scheduleID" };

	public static final String CONSTRAINT_COLUMNS[] = { "deviceID" };

	public static final String TABLE_NAME = "TOUDeviceMapping";

/**
 * TOUDeviceMapping constructor comment.
 */
public TOUDeviceMapping() {
	super();
}

/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getScheduleID() };
	deleteAMapping(getDeviceID(), getDbConnection());
	add( TABLE_NAME, addValues );
}

/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException {
	
	String values[] = { getDeviceID().toString(), getScheduleID().toString() };

	delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
}

/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteAMapping(Integer deviceID, java.sql.Connection conn)
{
	java.sql.PreparedStatement pstmt = null;		
	String sql = "DELETE FROM " + TABLE_NAME + " WHERE deviceID=" + deviceID;

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

/*
public void deleteAMapping(Integer deviceID)
{
	try
	{
		java.sql.Connection conn = null;
	
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
	
		deleteAMapping(deviceID, conn);
			
		conn.close();
	}
	catch( java.sql.SQLException e2 )
	{
		com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
	}	
}*/
/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static final java.util.Vector getAllDevicesIDList(Integer scheduleID, java.sql.Connection conn ) throws java.sql.SQLException
{
	java.util.Vector tmpList = new java.util.Vector();
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT deviceID FROM " + TABLE_NAME + " WHERE scheduleID= ?";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be (null).");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, scheduleID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				tmpList.add( new Integer(rset.getInt("deviceID")) );
			}
					
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
			if( pstmt != null ) pstmt.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}

	return tmpList;
}

/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 1:49:55 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDeviceID() {
	return deviceID;
}

/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 1:49:55 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getScheduleID() {
	return scheduleID;
}

/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "scheduleID" };
	String constraintColumns[] = { "deviceID"};

	Object constraintValues[] = { getDeviceID() };

	Object results[] = retrieve( selectColumns, TABLE_NAME, constraintColumns, constraintValues );
}

/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 1:49:55 PM)
 * @param newdeviceID java.lang.Integer
 */

public void setdeviceID(java.lang.Integer newdeviceID) {
	deviceID = newdeviceID;
}

/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 1:49:55 PM)
 * @param newscheduleID java.lang.Integer
 */
public void setscheduleID(java.lang.Integer newscheduleID) {
	scheduleID = newscheduleID;
}

/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = { "scheduleID" };
	Object setValues[] = { getScheduleID() };

	String constraintColumns[] = { "deviceID" };
	Object constraintValues[] = { getDeviceID()};

	update( TABLE_NAME, setColumns, setValues, constraintColumns, constraintValues );
}

/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static final Integer getTheScheduleID(Integer deviceID, java.sql.Connection conn ) throws java.sql.SQLException
{
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	Integer returnedID = new Integer(0);

	String sql = "SELECT scheduleID FROM " + TABLE_NAME + " WHERE deviceID= ?";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be null.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, deviceID.intValue() );
			
			rset = pstmt.executeQuery();							
			rset.next();
			returnedID = new Integer(rset.getInt(1));
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
			if( pstmt != null ) pstmt.close();
			if( rset != null ) rset.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}

	return returnedID;
}

public final static boolean hasSchedule(Integer deviceID) throws java.sql.SQLException 
{	
	return hasSchedule(deviceID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasSchedule(Integer deviceID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT ScheduleID FROM " + TABLE_NAME + " WHERE DeviceID=" + deviceID,
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