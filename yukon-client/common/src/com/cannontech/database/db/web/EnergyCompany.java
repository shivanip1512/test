package com.cannontech.database.db.web;

/**
 * Creation date: (6/2/2001 2:21:52 PM)
 * @author: Aaron Lauinger
 */
public class EnergyCompany extends com.cannontech.database.db.DBPersistent 
{
	public static final String tableName = "EnergyCompany";

	private long id = -1;
	private String name = null;
	private long routeID = -1;

	private static final String[] selectColumns =
	{		
		"Name",
		"RouteID"
	};

	private static final String[] constraintColumns =
	{
		"EnergyCompanyID"
	};

	private static final String allSql =
	"SELECT EnergyCompanyID FROM EnergyCompany";
/**
 * EnergyCompany constructor comment.
 */
public EnergyCompany() {
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
 * Creation date: (6/11/2001 3:38:14 PM)
 * @return com.cannontech.database.db.web.EnergyCompany
 * @param dbAlias java.lang.String
 */
public static long[] getAllEnergyCompanies() {
	return getAllEnergyCompanies("yukon");
}
/**
 * Creation date: (6/11/2001 3:38:14 PM)
 * @return com.cannontech.database.db.web.EnergyCompany
 * @param dbAlias java.lang.String
 */
public static long[] getAllEnergyCompanies(String dbAlias) {
	
	java.sql.Connection conn = null;
	java.sql.Statement stmt = null;
	java.sql.ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);
		stmt = conn.createStatement();
		rset = stmt.executeQuery(allSql);

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
			if( stmt != null ) stmt.close();
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
 * Creation date: (6/2/2001 2:24:02 PM)
 * @return long
 */
public long getId() {
	return id;
}
/**
 * Creation date: (6/2/2001 2:24:02 PM)
 * @return java.lang.String
 */
public java.lang.String getName() {
	return name;
}
/**
 * Creation date: (6/2/2001 2:24:02 PM)
 * @return long
 */
public long getRouteID() {
	return routeID;
}
/**
 * This method was created by a SmartGuide.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { new Long(getId()) };
	
	Object results[] = retrieve( selectColumns, tableName, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setName( (String) results[0] );
		setRouteID( ((Integer) results[1]).longValue() );			
	}
	else
	{
		throw new RuntimeException("Incorrect number of columns in result");
	}
}
/**
 * Creation date: (6/2/2001 2:24:02 PM)
 * @param newId long
 */
public void setId(long newId) {
	id = newId;
}
/**
 * Creation date: (6/2/2001 2:24:02 PM)
 * @param newName java.lang.String
 */
public void setName(java.lang.String newName) {
	name = newName;
}
/**
 * Creation date: (6/2/2001 2:24:02 PM)
 * @param newRouteID long
 */
public void setRouteID(long newRouteID) {
	routeID = newRouteID;
}
/**
 * Creation date: (6/3/2001 2:21:37 PM)
 * @return java.lang.String
 */
public String toString() {

	return "( Energy Company ID:  " + getId() + 
		   "  name:  " + getName() +
	 	   "  route ID:  " + getRouteID() +
	 	   " )";
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
