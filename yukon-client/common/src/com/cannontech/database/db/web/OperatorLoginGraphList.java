package com.cannontech.database.db.web;

import com.cannontech.database.SqlUtils;

/**
 * Creation date: (6/2/2001 4:29:56 PM)
 * @author: Aaron Lauinger
 */
public class OperatorLoginGraphList extends com.cannontech.database.db.DBPersistent {

	public static final String tableName = "OperatorLoginGraphList";
	
	private long operatorLoginID = -1;
	private long graphDefinitionID = -1;

	private static final String operatorSql =
	"SELECT OperatorLoginID FROM " + tableName + " WHERE GraphDefinitionID=?";

	private static final String graphDefinitionSql =
	"SELECT GraphDefinitionID FROM " + tableName + " WHERE OperatorLoginID=?";
/**
 * OperatorLoginGraphList constructor comment.
 */
public OperatorLoginGraphList() {
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
 * Creation date: (6/2/2001 4:40:29 PM)
 * @return long[]
 * @param operatorLoginID long
 */
public static long[] getGraphDefinitionIDs(long operatorLoginID) {
	return getGraphDefinitionIDs(operatorLoginID, "yukon");
}
/**
 * Creation date: (6/2/2001 4:40:29 PM)
 * @return long[]
 * @param operatorLoginID long
 */
public static long[] getGraphDefinitionIDs(long operatorLoginID, String dbAlias) {
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);

		pstmt = conn.prepareStatement(graphDefinitionSql);
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
		SqlUtils.close(rset, pstmt, conn );
	}

	// An exception must have occured
	return new long[0];
}
/**
 * Creation date: (6/2/2001 4:40:12 PM)
 * @return long[]
 * @param graphDefinitionID long
 */
public static long[] getOperatorLoginIDs(long graphDefinitionID) {
	return getOperatorLoginIDs(graphDefinitionID, "yukon" );
}
/**
 * Creation date: (6/2/2001 4:40:12 PM)
 * @return long[]
 * @param graphDefinitionID long
 */
public static long[] getOperatorLoginIDs(long graphDefinitionID, String dbAlias) {
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);

		pstmt = conn.prepareStatement(operatorSql);
		pstmt.setLong(1, graphDefinitionID);

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
