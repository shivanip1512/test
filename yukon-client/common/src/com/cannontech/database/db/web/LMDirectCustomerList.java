package com.cannontech.database.db.web;

import com.cannontech.database.SqlUtils;
import com.cannontech.database.db.DBPersistent;
/**
 * Creation date: (7/24/2001 12:19:25 AM)
 * @author: 
 */
public class LMDirectCustomerList extends DBPersistent {

	public static final String tableName = "LMDirectCustomerList";
	
	private static String programCustomerSql =
	"SELECT ProgramID FROM " + tableName + " WHERE CustomerID = ?";
/**
 * LMDirectCustomerList constructor comment.
 */
public LMDirectCustomerList() {
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
 * Creation date: (7/24/2001 12:20:53 AM)
 * @return long[]
 * @param customerID long
 */
public static long[] getProgramIDs(long customerID) {
	return getProgramIDs(customerID, "yukon");
}
/**
 * Creation date: (7/24/2001 12:20:53 AM)
 * @return long[]
 * @param customerID long
 */
public static long[] getProgramIDs(long customerID, String dbAlias) {
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);

		pstmt = conn.prepareStatement(programCustomerSql);
		pstmt.setLong(1, customerID);

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
