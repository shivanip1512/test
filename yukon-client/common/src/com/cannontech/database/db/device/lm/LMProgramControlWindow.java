package com.cannontech.database.db.device.lm;

import com.cannontech.database.SqlUtils;

/**
 * This type was created in VisualAge.
 */

public class LMProgramControlWindow extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private Integer windowNumber = null;
	private Integer availableStartTime = null;
	private Integer availableStopTime = null;


	public static final String SETTER_COLUMNS[] = 
	{ 
		"WindowNumber", "AvailableStartTime", "AvailableStopTime"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMProgramControlWindow";

/**
 * LMGroupVersacomSerial constructor comment.
 */
public LMProgramControlWindow() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getWindowNumber(), 
					getAvailableStartTime(), getAvailableStopTime() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, "DeviceID", getDeviceID() );
}
/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteAllProgramControlWindows(Integer programDeviceID, java.sql.Connection conn)
{
	java.sql.PreparedStatement pstmt = null;		
	String sql = "DELETE FROM " + TABLE_NAME + " WHERE deviceID=" + programDeviceID;

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
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.State[]
 * @param stateGroup java.lang.Integer
 */
public static final LMProgramControlWindow[] getAllLMProgramControlWindows(Integer deviceID, java.sql.Connection conn ) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(2);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT WindowNumber,AvailableStartTime,AvailableStopTime " +
					 "FROM " + TABLE_NAME + " WHERE DEVICEID= ?";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be (null).");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, deviceID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				LMProgramControlWindow item = new LMProgramControlWindow();

				item.setDbConnection(conn);
				item.setDeviceID( deviceID );
				item.setWindowNumber( new Integer(rset.getInt("WindowNumber")) );
				item.setAvailableStartTime( new Integer(rset.getInt("AvailableStartTime")) );
				item.setAvailableStopTime( new Integer(rset.getInt("AvailableStopTime")) );

				tmpList.add( item );
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, pstmt);
	}


	LMProgramControlWindow retVal[] = new LMProgramControlWindow[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 11:55:30 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAvailableStartTime() {
	return availableStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 11:55:30 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getAvailableStopTime() {
	return availableStopTime;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getDeviceID() {
	return deviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 11:55:30 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getWindowNumber() {
	return windowNumber;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getDeviceID() };	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setWindowNumber( (Integer) results[0] );
		setAvailableStartTime( (Integer) results[1] );
		setAvailableStopTime( (Integer) results[2] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 11:55:30 AM)
 * @param newAvailableStartTime java.lang.Integer
 */
public void setAvailableStartTime(java.lang.Integer newAvailableStartTime) {
	availableStartTime = newAvailableStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 11:55:30 AM)
 * @param newAvailableStopTime java.lang.Integer
 */
public void setAvailableStopTime(java.lang.Integer newAvailableStopTime) {
	availableStopTime = newAvailableStopTime;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 11:55:30 AM)
 * @param newWindowNumber java.lang.Integer
 */
public void setWindowNumber(java.lang.Integer newWindowNumber) {
	windowNumber = newWindowNumber;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getWindowNumber(), 
					getAvailableStartTime(), getAvailableStopTime() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
