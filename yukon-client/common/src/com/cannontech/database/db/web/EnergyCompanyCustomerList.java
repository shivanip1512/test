package com.cannontech.database.db.web;

/**
 * Creation date: (6/2/2001 4:23:32 PM)
 * @author: Aaron Lauinger
 */
public class EnergyCompanyCustomerList extends com.cannontech.database.db.DBPersistent 
{
	public static final String tableName = "EnergyCompanyCustomerList";
	
	private long energyCompanyID = -1;
	private long customerID = -1;

	private static final String energyCompanySql =
	"SELECT EnergyCompanyID FROM " + tableName + " WHERE CustomerID=?";

	private static final String customerSql =
	"SELECT CustomerID FROM " + tableName + " WHERE EnergyCompanyID=?";
/**
 * EnergyCompanyCustomerList constructor comment.
 */
public EnergyCompanyCustomerList() {
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
 * Creation date: (6/2/2001 4:27:37 PM)
 * @return long[]
 * @param energyCompanyID long
 */
public static long[] getCustomerIDs(long energyCompanyID) {
	return getCustomerIDs(energyCompanyID, "yukon");
}
/**
 * Creation date: (6/2/2001 4:27:37 PM)
 * @return long[]
 * @param energyCompanyID long
 */
public static long[] getCustomerIDs(long energyCompanyID, String dbAlias) {
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);

		pstmt = conn.prepareStatement(customerSql);
		pstmt.setLong(1, energyCompanyID);

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
			if( rset != null ) rset.close();
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
 * Creation date: (6/2/2001 4:27:06 PM)
 * @return long[]
 * @param customerID long
 */
public static long[] getEnergyCompanyIDs(long customerID) {
	return getEnergyCompanyIDs(customerID, "yukon");
}
/**
 * Creation date: (6/2/2001 4:27:06 PM)
 * @return long[]
 * @param customerID long
 */
public static long[] getEnergyCompanyIDs(long customerID, String dbAlias) {
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);

		pstmt = conn.prepareStatement(energyCompanySql);
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
		try
		{
			if( rset != null ) rset.close();
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
