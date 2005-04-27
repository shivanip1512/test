package com.cannontech.database.db.web;

/**
 * Creation date: (6/4/2001 2:46:17 PM)
 * @author: Aaron Lauinger
 */
public class EnergyCompanyOperatorLoginList extends com.cannontech.database.db.DBPersistent 
{
	public static final String tableName = "EnergyCompanyOperatorLoginList";

	private Integer energyCompanyID = null;
	private Integer operatorLoginID = null;

	private static final String energyCompanySql =
	"SELECT EnergyCompanyID FROM " + tableName + " WHERE OperatorLoginID=?";

	private static final String operatorLoginSql =
	"SELECT OperatorLoginID FROM " + tableName + " WHERE EnergyCompanyID=?";
/**
 * EnergyCompanyOperatorLoginList constructor comment.
 */
public EnergyCompanyOperatorLoginList() {
	super();
}

public EnergyCompanyOperatorLoginList(Integer ecID, Integer oplID) {
	super();
	
	energyCompanyID = ecID;
	operatorLoginID = oplID;
	
}
/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = 
	{ 
		getEnergyCompanyID(),
		getOperatorLoginID()
	};

	add( tableName, addValues );
}

public Integer getEnergyCompanyID()
{
	return energyCompanyID;
}

public Integer getOperatorLoginID()
{
	return operatorLoginID;
}
/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException 
{
	throw new RuntimeException("Not implemented");
}
/**
 * Creation date: (6/4/2001 3:21:29 PM)
 * @return long[]
 * @param operatorLoginID long
 */
public static long getEnergyCompanyID(long operatorLoginID) {
	return getEnergyCompanyID(operatorLoginID, "yukon" );
}
/**
 * Creation date: (6/4/2001 3:21:29 PM)
 * @return long[]
 * @param operatorLoginID long
 */
public static long getEnergyCompanyID(long operatorLoginID, String dbAlias) {
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);

		pstmt = conn.prepareStatement(energyCompanySql);
		pstmt.setLong(1, operatorLoginID);

		rset = pstmt.executeQuery();

		if( rset.next() )
		{
			return rset.getInt(1);
		}
		else
		{
			return -1;
		}
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
	return -1;
}
/**
 * Creation date: (6/4/2001 3:21:29 PM)
 * @return long[]
 * @param operatorLoginID long
 */
public static long[] getEnergyCompanyIDs(long operatorLoginID, String dbAlias) {
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);

		pstmt = conn.prepareStatement(energyCompanySql);
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
 * Creation date: (6/4/2001 3:21:53 PM)
 * @return long[]
 * @param energyCompanyID long
 */
public static long[] getOperatorLoginIDs(long energyCompanyID) {
	return getOperatorLoginIDs(energyCompanyID, "yukon");
}
/**
 * Creation date: (6/4/2001 3:21:53 PM)
 * @return long[]
 * @param energyCompanyID long
 */
public static long[] getOperatorLoginIDs(long energyCompanyID, String dbAlias) {
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);

		pstmt = conn.prepareStatement(operatorLoginSql);
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
	Object[] setValues = { getEnergyCompanyID() };
	String[] setColumns = { "EnergyCompanyID" };
		
	String[] constraintColumns = { "OperatorLoginID" };
	Object[] constraintValues = { getOperatorLoginID() };
		
	update(tableName, setColumns, setValues, constraintColumns, constraintValues);
}

public void setOperatorLoginID(Integer newID)
{
	operatorLoginID = newID;
}

}