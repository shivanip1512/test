package com.cannontech.database.db.web;

/**
 * Creation date: (6/2/2001 4:02:50 PM)
 * @author: Aaron Lauinger
 */
public class LMMACSScheduleOperatorList extends com.cannontech.database.db.DBPersistent 
{
	public static final String tableName = "LMMACSScheduleOperatorList";
	
	private long scheduleID = -1;
	private long operatorLoginID = -1;

	private static final String operatorSql =
	"SELECT OperatorLoginID FROM " + tableName + " WHERE ScheduleID=?";

	private static final String scheduleSql =
	"SELECT ScheduleID FROM " + tableName + " WHERE OperatorLoginID=?";
/**
 * LMMACSScheduleOperatorList constructor comment.
 */
public LMMACSScheduleOperatorList() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	throw new RuntimeException("Not implemented");
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException 
{
	throw new RuntimeException("Not implemented");
}
/**
 * Creation date: (6/2/2001 4:05:50 PM)
 * @return long[]
 * @param scheduleID long
 */
public static long[] getOperatorLoginIDs(long scheduleID) {
	return getOperatorLoginIDs(scheduleID, "yukon");
}
/**
 * Creation date: (6/2/2001 4:05:50 PM)
 * @return long[]
 * @param scheduleID long
 */
public static long[] getOperatorLoginIDs(long scheduleID, String dbAlias) {
	
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);

		pstmt = conn.prepareStatement(operatorSql);
		pstmt.setLong(1, scheduleID);

		rset = pstmt.executeQuery();

		java.util.ArrayList idList = new java.util.ArrayList();	
		while( rset.next() )
		{
			idList.add( new Long(rset.getLong(1)) );
		}

		long[] retIDs = new long[idList.size()];
		for( int i = 0; i < idList.size(); i++ )
		{
			retIDs[i] = ((Long) idList.get(i)).longValue();
		}
		
		return retIDs;			
	}
	catch(java.sql.SQLException e)
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
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
		}
	}

	// An exception must have occured
	return new long[0];
}
/**
 * Creation date: (6/2/2001 4:05:28 PM)
 * @return long[]
 * @param operatorLoginID long
 */
public static long[] getScheduleIDs(long operatorLoginID) {
	return getScheduleIDs(operatorLoginID, "yukon");
}
/**
 * Creation date: (6/2/2001 4:05:28 PM)
 * @return long[]
 * @param operatorLoginID long
 */
public static long[] getScheduleIDs(long operatorLoginID, String dbAlias) {
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);

		pstmt = conn.prepareStatement(scheduleSql);
		pstmt.setLong(1, operatorLoginID);

		rset = pstmt.executeQuery();

		java.util.ArrayList idList = new java.util.ArrayList();	
		while( rset.next() )
		{
			idList.add( new Long(rset.getLong(1)) );
		}

		long[] retIDs = new long[idList.size()];
		for( int i = 0; i < idList.size(); i++ )
		{
			retIDs[i] = ((Long) idList.get(i)).longValue();
		}
		
		return retIDs;			
	}
	catch(java.sql.SQLException e)
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
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
		}
	}

	// An exception must have occured
	return new long[0];
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException 
{
	throw new RuntimeException("Not implemented");
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException 
{
	throw new RuntimeException("Not implemented");
}
}
