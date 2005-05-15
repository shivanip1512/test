package com.cannontech.database.db.device.lm;

import java.util.Vector;

/**
 * This type was created in VisualAge.
 */

public class LMControlAreaProgram extends com.cannontech.database.db.NestedDBPersistent implements DeviceListItem
{
	private Integer deviceID = null;
	private Integer lmProgramDeviceID = new Integer(0);
	private Integer startPriority = new Integer(0);
	private Integer stopPriority = new Integer(0);
	
	/**
	 * @return Returns the startPriority.
	 */
	public Integer getStartPriority() {
		return startPriority;
	}
	/**
	 * @param startPriority The startPriority to set.
	 */
	public void setStartPriority(Integer startPriority) {
		this.startPriority = startPriority;
	}
	/**
	 * @return Returns the stopPriority.
	 */
	public Integer getStopPriority() {
		return stopPriority;
	}
	/**
	 * @param stopPriority The stopPriority to set.
	 */
	public void setStopPriority(Integer stopPriority) {
		this.stopPriority = stopPriority;
	}
	public static final String SETTER_COLUMNS[] = 
	{ 
		"StartPriority", "StopPriority"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID", "LMProgramDeviceID" };

	public static final String TABLE_NAME = "LMControlAreaProgram";

/**
 * LMGroupVersacomSerial constructor comment.
 */
public LMControlAreaProgram() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getDeviceID(), getLmProgramDeviceID(),
							getStartPriority(), getStopPriority() };

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
public static final LMControlAreaProgram[] getAllControlAreaList(Integer ctrlAreaDeviceID, java.sql.Connection conn ) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT DEVICEID,LMPROGRAMDEVICEID,StartPriority,StopPriority " +
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
				LMControlAreaProgram item = new LMControlAreaProgram();

				item.setDbConnection(conn);
				item.setDeviceID( new Integer(rset.getInt("DeviceID")) );
				item.setLmProgramDeviceID( new Integer(rset.getInt("LMProgramDeviceID")) );
				item.setStartPriority( new Integer(rset.getInt("StartPriority")));
				item.setStopPriority( new Integer(rset.getInt("StopPriority")));

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
		try
		{
			if( pstmt != null ) pstmt.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}


	LMControlAreaProgram retVal[] = new LMControlAreaProgram[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}

public static final Vector getAllProgramsInControlAreas()
{
	String sql = "SELECT LMPROGRAMDEVICEID FROM " + TABLE_NAME;
    	
	com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
			sql, com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
    			
	try {
		stmt.execute();
		Vector containedPrograms = new Vector( stmt.getRowCount()) ;
    		
		for (int i = 0; i < stmt.getRowCount(); i++) 
		{
			Object[] row = stmt.getRow(i);
			containedPrograms.addElement(new Integer(((java.math.BigDecimal) row[0]).intValue()));
		}
    		
		return containedPrograms;
	}
	catch (Exception e) {
		e.printStackTrace();
	}
    	
	return null;
}

public static final java.util.Vector getAllProgramsForAnArea( Integer ctrlAreaDeviceID, java.sql.Connection conn)
{
	java.util.Vector progList = new java.util.Vector();
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT DEVICEID,LMPROGRAMDEVICEID,StartPriority,StopPriority " +
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
				LMControlAreaProgram item = new LMControlAreaProgram();

				item.setDbConnection(conn);
				item.setDeviceID( new Integer(rset.getInt("DeviceID")) );
				item.setLmProgramDeviceID( new Integer(rset.getInt("LMProgramDeviceID")) );
				item.setStartPriority( new Integer(rset.getInt("StartPriority")));
				item.setStopPriority( new Integer(rset.getInt("StopPriority")));

				progList.add( item );
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

	return progList;
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
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getDeviceID(), getLmProgramDeviceID() };	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setStartPriority( (Integer) results[0] );
		setStopPriority( (Integer) results[1] );
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
 * Creation date: (3/16/2001 11:59:51 AM)
 * @param newLmProgramDeviceID java.lang.Integer
 */
public void setLmProgramDeviceID(java.lang.Integer newLmProgramDeviceID) {
	lmProgramDeviceID = newLmProgramDeviceID;
}


/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getStartPriority(), getStopPriority() }; 
			
	Object constraintValues[] = { getDeviceID(), getLmProgramDeviceID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
