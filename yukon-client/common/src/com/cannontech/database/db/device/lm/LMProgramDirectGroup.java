package com.cannontech.database.db.device.lm;

/**
 * This type was created in VisualAge.
 */

public class LMProgramDirectGroup extends com.cannontech.database.db.DBPersistent implements DeviceListItem
{
	private Integer deviceID = null;
	private Integer lmGroupDeviceID = null;
	private Integer groupOrder = null;


	public static final String SETTER_COLUMNS[] = 
	{ 
		"LMGroupDeviceID", "GroupOrder"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "LMProgramDirectGroup";

/**
 * LMGroupVersacomSerial constructor comment.
 */
public LMProgramDirectGroup() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getLmGroupDeviceID(), getGroupOrder() };

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
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.point.State[]
 * @param stateGroup java.lang.Integer
 */
public static final void deleteAllDirectGroups(Integer deviceID, java.sql.Connection conn ) throws java.sql.SQLException
{
	com.cannontech.database.SqlStatement sql = 
		new com.cannontech.database.SqlStatement("DELETE FROM " +TABLE_NAME +
			" WHERE deviceID=" + deviceID, conn );
		
	try
	{
		sql.execute();
	}
	catch( com.cannontech.common.util.CommandExecutionException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

}

/**
 * Deletes a group from all the programs it may belong to
 * @return com.cannontech.database.db.point.State[]
 * @param stateGroup java.lang.Integer
 */
public static final void deleteGroupsFromProgram(Integer groupID, java.sql.Connection conn ) throws java.sql.SQLException
{
	com.cannontech.database.SqlStatement sql = 
		new com.cannontech.database.SqlStatement("DELETE FROM " + TABLE_NAME +
			" WHERE LMGroupDeviceID = " + groupID, conn );

	try
	{
		sql.execute();
	}
	catch( com.cannontech.common.util.CommandExecutionException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

}

/**
 * This method was created in VisualAge.
 * @return LMProgramDirectGear[]
 * @param stateGroup java.lang.Integer
 */
public static final LMProgramDirectGroup[] getAllDirectGroups( Integer deviceID ) throws java.sql.SQLException
{
	return getAllDirectGroups( deviceID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
}
/**
 * This method was created in VisualAge.
 * @return LMProgramDirectGear[]
 * @param stateGroup java.lang.Integer
 */
public static final LMProgramDirectGroup[] getAllDirectGroups(Integer deviceID, String databaseAlias) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	
	//get all the gears that have the passed in DeviceID
	String sql = "select LMGroupDeviceID, GroupOrder from " +
					 TABLE_NAME +
					 " where deviceid=? order by GroupOrder";
	try
	{		
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);

		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, deviceID.intValue() );

			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				LMProgramDirectGroup group = new LMProgramDirectGroup();

				group.setDeviceID( deviceID );
				group.setLmGroupDeviceID( new Integer(rset.getInt("LMGroupDeviceID")) );
				group.setGroupOrder(new Integer(rset.getInt("GroupOrder")) );
				group.setDbConnection(null);

				tmpList.add( group );
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
			if( conn != null ) conn.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}


	LMProgramDirectGroup retVal[] = new LMProgramDirectGroup[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
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
 * Creation date: (3/16/2001 4:26:58 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getGroupOrder() {
	return groupOrder;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 4:25:41 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLmGroupDeviceID() {
	return lmGroupDeviceID;
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
		setLmGroupDeviceID( (Integer) results[0] );
		setGroupOrder( (Integer) results[1] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

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
 * Creation date: (3/16/2001 4:26:58 PM)
 * @param newGroupOrder java.lang.Integer
 */
public void setGroupOrder(java.lang.Integer newGroupOrder) {
	groupOrder = newGroupOrder;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 4:25:41 PM)
 * @param newLmGroupDeviceID java.lang.Integer
 */
public void setLmGroupDeviceID(java.lang.Integer newLmGroupDeviceID) {
	lmGroupDeviceID = newLmGroupDeviceID;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getLmGroupDeviceID(), getGroupOrder() };

	Object constraintValues[] = { getDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
