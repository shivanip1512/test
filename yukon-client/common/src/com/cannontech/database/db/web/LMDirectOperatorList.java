package com.cannontech.database.db.web;

import com.cannontech.database.db.*;
/**
 * Creation date: (4/9/2002 8:56:08 AM)
 * @author: 
 */
public class LMDirectOperatorList extends DBPersistent {
	public static final String tableName = "LMDirectOperatorList";
	
	private static String programOperatorSql =
	"SELECT ProgramID FROM " + tableName + " WHERE OperatorLoginID = ?";
/**
 * LMDirectOperatorList constructor comment.
 */
public LMDirectOperatorList() {
	super();
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException {
	throw new RuntimeException("Not implemented");
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException {
	throw new RuntimeException("Not implemented");
}
/**
 * Creation date: (7/24/2001 12:20:53 AM)
 * @return long[]
 * @param customerID long
 */
public static long[] getProgramIDs(long operatorLoginID) {
	return getProgramIDs(operatorLoginID, "yukon");
}
/**
 * Creation date: (7/24/2001 12:20:53 AM)
 * @return long[]
 * @param customerID long
 */
public static long[] getProgramIDs(long operatorLoginID, String dbAlias) {
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);

		pstmt = conn.prepareStatement(programOperatorSql);
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
		e.printStackTrace();
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
			e2.printStackTrace();
		}
	}

	// An exception must have occured
	return new long[0];
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException {
	throw new RuntimeException("Not implemented");	
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void update() throws java.sql.SQLException {
	throw new RuntimeException("Not implemented");
}
}
