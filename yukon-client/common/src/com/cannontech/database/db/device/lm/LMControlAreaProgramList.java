package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */

public class LMControlAreaProgramList extends com.cannontech.database.db.DBPersistent implements DeviceListItem
{
	private Integer deviceID = null;
	private Integer lmProgramDeviceID = new Integer(0);
	private Integer userOrder = new Integer(0);
	private Integer stopOrder = new Integer(0);
	private Integer defaultPriority = new Integer(0);


	public static final String SETTER_COLUMNS[] = 
	{ 
		"UserOrder", "StopOrder", "DefaultPriority"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID", "LMProgramDeviceID" };

	public static final String TABLE_NAME = "LMControlAreaProgram";

/**
 * LMGroupVersacomSerial constructor comment.
 */
public LMControlAreaProgramList() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getLmProgramDeviceID(), 
					getUserOrder(), getStopOrder(), getDefaultPriority() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	String values[] = { getDeviceID().toString(), getLmProgramDeviceID().toString() };

	delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
}
/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteAllControlAreaProgramList(Integer ctrlAreaDeviceID, java.sql.Connection conn)
{
	java.sql.PreparedStatement pstmt = null;		
	String sql = "DELETE FROM " + TABLE_NAME + " WHERE deviceID=" + ctrlAreaDeviceID;

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
		e.printStackTrace();
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
			e2.printStackTrace();//something is up
		}	
	}

	return true;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.State[]
 * @param stateGroup java.lang.Integer
 */
public static final LMControlAreaProgramList[] getAllControlAreaList(Integer ctrlAreaDeviceID, java.sql.Connection conn ) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT DEVICEID,LMPROGRAMDEVICEID,USERORDER," +
					 "STOPORDER,DEFAULTPRIORITY " +
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
			pstmt.setInt( 1, ctrlAreaDeviceID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				LMControlAreaProgramList item = new LMControlAreaProgramList();

				item.setDbConnection(conn);
				item.setDeviceID( new Integer(rset.getInt("DeviceID")) );
				item.setLmProgramDeviceID( new Integer(rset.getInt("LMProgramDeviceID")) );
				item.setUserOrder( new Integer(rset.getInt("UserOrder")) );
				item.setStopOrder( new Integer(rset.getInt("StopOrder")) );
				item.setDefaultPriority( new Integer(rset.getInt("DefaultPriority")) );

				tmpList.add( item );
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
	}
	finally
	{
		try
		{
			if( pstmt != null ) pstmt.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			e2.printStackTrace();//something is up
		}	
	}


	LMControlAreaProgramList retVal[] = new LMControlAreaProgramList[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 11:59:51 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDefaultPriority() {
	return defaultPriority;
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
 * Creation date: (3/16/2001 11:59:51 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLmProgramDeviceID() {
	return lmProgramDeviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 11:59:51 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getStopOrder() {
	return stopOrder;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 11:59:51 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getUserOrder() {
	return userOrder;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getDeviceID(), getLmProgramDeviceID() };	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setUserOrder( (Integer) results[0] );
		setStopOrder( (Integer) results[1] );
		setDefaultPriority( (Integer) results[2] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 11:59:51 AM)
 * @param newDefaultPriority java.lang.Integer
 */
public void setDefaultPriority(java.lang.Integer newDefaultPriority) {
	defaultPriority = newDefaultPriority;
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
 * Creation date: (3/16/2001 11:59:51 AM)
 * @param newLmProgramDeviceID java.lang.Integer
 */
public void setLmProgramDeviceID(java.lang.Integer newLmProgramDeviceID) {
	lmProgramDeviceID = newLmProgramDeviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 11:59:51 AM)
 * @param newStopOrder java.lang.Integer
 */
public void setStopOrder(java.lang.Integer newStopOrder) {
	stopOrder = newStopOrder;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 11:59:51 AM)
 * @param newUserOrder java.lang.Integer
 */
public void setUserOrder(java.lang.Integer newUserOrder) {
	userOrder = newUserOrder;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getUserOrder(), 
								  getStopOrder(), getDefaultPriority() };

	Object constraintValues[] = { getDeviceID(), getLmProgramDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
