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
public class TOURateOffset extends com.cannontech.database.db.DBPersistent 
{
	private Integer touScheduleID;
	private String switchRate;
	private Integer switchOffset;
	
	public static final String SETTER_COLUMNS[] = 
	{ 
		"TOUSCHEDULEID", "SWITCHRATE", "SWITCHOFFSET"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "TOUScheduleID" };

	public static final String TABLE_NAME = "TOURateOffset";

/**
 * TOURateOffset constructor comment.
 */
public TOURateOffset() {
	super();
}
/**
 * TOURateOffset constructor comment.
 */
public TOURateOffset(String rate, Integer offset) 
{
	super();
	switchRate = rate;
	switchOffset = offset;
}

public TOURateOffset(Integer scheduleID, String rate, Integer offset) 
{
	super();
	touScheduleID = scheduleID;
	switchRate = rate;
	switchOffset = offset;
}

/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:34:05 AM)
 */
public void add() throws java.sql.SQLException
{
	Object addValues[] = 
	{ 
		getTOUScheduleID(), getSwitchRate(),
		getSwitchOffset()
	};

	add( TABLE_NAME, addValues );
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:34:22 AM)
 */
public void delete() 
{
	throw new com.cannontech.common.util.MethodNotImplementedException();
}
/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteAllRateOffsets(Integer touScheduleID, java.sql.Connection conn)
{
	com.cannontech.database.SqlStatement stmt = null;

	if( conn == null )
		throw new IllegalArgumentException("Database connection should not be (null)");

	try
	{
		java.sql.Statement stat = conn.createStatement();

		stat.execute("DELETE FROM " + TABLE_NAME + " WHERE touScheduleID=" + touScheduleID);

		if (stat != null)
			stat.close();
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}

	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 12:52:15 PM)
 * @param touScheduleID java.lang.Integer
 * @param dbAlias java.lang.String
 */
public static final java.util.Vector getAllRateOffsets(Integer scheduleID, java.sql.Connection conn)
{
	java.util.Vector returnVector = new java.util.Vector(5);
	Integer touScheduleID = null;
	String 	switchRate = null;
	Integer switchOffset = null;
	
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT " + SETTER_COLUMNS[1] +"," 
		+ SETTER_COLUMNS[2] + 
		" FROM " + TABLE_NAME +
		" WHERE " + CONSTRAINT_COLUMNS[0] + 
		"=? ORDER BY " + SETTER_COLUMNS[1];

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
				touScheduleID = scheduleID;
				switchRate = rset.getString(SETTER_COLUMNS[1]);
				switchOffset = new Integer( rset.getInt(SETTER_COLUMNS[2]) );
								
				returnVector.addElement( new TOURateOffset(
						touScheduleID, 
						switchRate, 
						switchOffset) );				
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


	return returnVector;
}

/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 5:40:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSwitchOffset() {
	return switchOffset;
}

/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 5:40:28 PM)
 * @return java.lang.String
 */
public java.lang.String getSwitchRate() {
	return switchRate;
}

/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 5:40:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getTOUScheduleID() {
	return touScheduleID;
}

/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:33:53 AM)
 */
public void retrieve() 
{
	throw new com.cannontech.common.util.MethodNotImplementedException();
}

/**
 * Insert the method's description here.
 * @param newSwitchOffset java.lang.Integer
 */
public void setSwitchOffset(java.lang.Integer newSwitchOffset) {
	switchOffset = newSwitchOffset;
}

/**
 * Insert the method's description here.
 * @param newSwitchRate java.lang.String
 */
public void setSwitchRate(java.lang.String newSwitchRate) {
	switchRate = newSwitchRate;
}

/**
 * Insert the method's description here.
 * @param newTOUScheduleID java.lang.Integer
 */
public void setTOUScheduleID(java.lang.Integer newTOUScheduleID) {
	touScheduleID = newTOUScheduleID;
}

/**
 * Insert the method's description here.
 * Creation date: (9/22/2004 10:34:35 AM)
 */
public void update() 
{
	throw new com.cannontech.common.util.MethodNotImplementedException();
}
}
