package com.cannontech.database.db.route;

/**
 * This type was created in VisualAge.
 */
import java.sql.SQLException;

public class MacroRoute extends com.cannontech.database.db.DBPersistent 
{	
	private Integer RouteID = null;
	private Integer singleRouteID = null;
	private Integer routeOrder = null;

	public static final String SETTER_COLUMNS[] = 
	{
		"SingleRouteID"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "RouteID", "RouteOrder" };
	
	public static final String TABLE_NAME = "MacroRoute";

/**
 * This method was created in VisualAge.
 */
public MacroRoute() {
	super();

	initialize( null,  null, null );
}
/**
 * This method was created in VisualAge.
 */
public MacroRoute(Integer routeID, Integer routeOrder) {
	super();

	initialize( routeID, routeOrder, null );
}
/**
 * This method was created in VisualAge.
 */
public MacroRoute(Integer routeID,  Integer routeOrder, Integer singleRouteID) {
	super();

	initialize( routeID,  routeOrder, singleRouteID );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {
	
	Object addValues[] = { getRouteID(), getSingleRouteID(), getRouteOrder() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	Object constraintValues[] = { getRouteID(), getRouteOrder() };

	delete( TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );
}


/**
 * This method was deletes any rows from the MacroRoute table where RouteID = macroRouteID
 * @param macroRouteID java.lang.Integer
 */
public final static boolean deleteAllMacroRoutes(Integer macroRouteID, java.sql.Connection conn) throws SQLException {

	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement(
            "DELETE FROM MacroRoute WHERE RouteID=" + macroRouteID,
				conn );
	try
	{
		stmt.execute();
	}
	catch(Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}

	return true;
}

public final static boolean deleteFromMacro(Integer singleID, java.sql.Connection conn) throws SQLException {

	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement(
			"DELETE FROM MacroRoute WHERE SingleRouteID=" + singleID,
				conn );
	try
	{
		stmt.execute();
	}
	catch(Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}

	return true;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.route.MacroRoute[]
 * @param routeID java.lang.Integer
 */
public static MacroRoute[] getMacroRoutes(Integer routeID) throws java.sql.SQLException{

	return getMacroRoutes(routeID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.route.MacroRoute[]
 * @param routeID java.lang.Integer
 */
public static MacroRoute[] getMacroRoutes(Integer routeID, String databaseAlias) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.Connection conn = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT RouteOrder,SingleRouteID FROM MacroRoute " + 
				 "WHERE RouteID= ? ORDER BY RouteOrder";

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
			pstmt.setInt( 1, routeID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				tmpList.add( new MacroRoute( 
						routeID,
						new Integer(rset.getInt("RouteOrder")),
						new Integer(rset.getInt("SingleRouteID"))
						 ) );				
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


	MacroRoute retVal[] = new MacroRoute[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getRouteID() {
	return RouteID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getRouteOrder() {
	return routeOrder;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getSingleRouteID() {
	return singleRouteID;
}
/**
 * This method was created in VisualAge.
 * @param name java.lang.String
 * @param routeName java.lang.String
 * @param routeOrder java.lang.Integer
 */
public void initialize( Integer routeID, Integer routeOrder, Integer singleRouteID )
{
	setRouteID( routeID );
	setSingleRouteID( singleRouteID) ;
	setRouteOrder( routeOrder );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	String selectColumns[] = { "SingleRouteID" };

	String constraintColumns[] = { "RouteID", "RouteOrder"};
	Object constraintValues[] = { getRouteID(), getRouteOrder() };

	Object results[] = retrieve(selectColumns, "MacroRoute", constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setSingleRouteID( (Integer) results[0] );
	}
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setRouteID(Integer newValue) {
	this.RouteID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setRouteOrder(Integer newValue) {
	this.routeOrder = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setSingleRouteID(Integer newValue) {
	this.singleRouteID = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getSingleRouteID() };
	Object constraintValues[] = { getRouteID(), getRouteOrder() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
