package com.cannontech.database.db.device.lm;

import com.cannontech.common.util.CtiUtilities;

/**
 * This type was created in VisualAge.
 */

public class LMProgram extends com.cannontech.database.db.DBPersistent 
{
	private Integer deviceID = null;
	private String controlType = null;
	private Integer constraintID = null;


	public static final String SETTER_COLUMNS[] = 
	{ 
		"ControlType", "ConstraintID"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMProgram";

/**
 * LMGroupVersacomSerial constructor comment.
 */
public LMProgram() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getControlType(), getConstraintID() };

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
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:47:47 AM)
 * @return java.lang.String
 */
public java.lang.String getControlType() {
	return controlType;
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
 * Creation date: (3/16/2001 10:51:39 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getConstraintID() {
	return constraintID;
}

/**
 * This method was created in VisualAge.
 * @param
 * @return java.util.Vector of Integers
 *
 * This method returns all the LMProgram ID's that are not assgined
 *  to a Control Area.
 */
public static java.util.Vector getUnassignedPrograms()
{
	java.util.Vector returnVector = null;
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;


	String sql = "SELECT DeviceID FROM " + TABLE_NAME + " where " +
					 " deviceid not in (select lmprogramdeviceid from " + LMControlAreaProgram.TABLE_NAME +
					 ") ORDER BY deviceid";

	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());

		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			
			rset = pstmt.executeQuery();
			returnVector = new java.util.Vector(5); //rset.getFetchSize()
	
			while( rset.next() )
			{				
				returnVector.addElement( 
						new Integer(rset.getInt("DeviceID")) );
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
			if( pstmt != null ) 
				pstmt.close();
			if( conn != null ) 
				conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}


	return returnVector;
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
		setControlType( (String) results[0] );
		setConstraintID( (Integer) results[1] );

	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}

/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 10:47:47 AM)
 * @param newControlType java.lang.String
 */
public void setControlType(java.lang.String newControlType) {
	controlType = newControlType;
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
 * Creation date: (3/16/2001 10:51:39 AM)
 * @param newMaxHoursAnnually java.lang.Integer
 */
public void setConstraintID(java.lang.Integer newID) {
	constraintID = newID;
}

/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getControlType(), getConstraintID() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
	

}
