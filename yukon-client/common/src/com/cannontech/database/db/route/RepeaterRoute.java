package com.cannontech.database.db.route;

import com.cannontech.database.SqlUtils;

/**
 * This type was created in VisualAge.
 */
public class RepeaterRoute extends com.cannontech.database.db.DBPersistent {
	
	private Integer deviceID = null;
	private Integer variableBits = null;
	private Integer routeID = null;
	private Integer repeaterOrder = null;
   
   public static final String TABLE_NAME = "RepeaterRoute";
/**
 * RepeaterRotue constructor comment.
 */
public RepeaterRoute() {
	super();
	initialize( null,  null, null, null );
}


/**
 * RepeaterRotue constructor comment.
 */
public RepeaterRoute(Integer routeID, Integer deviceID, Integer variableBits) {
	super();
	initialize( routeID,  deviceID, variableBits, null );
}
/**
 * RepeaterRotue constructor comment.
 */
public RepeaterRoute(Integer routeID, Integer deviceID, Integer variableBits, Integer repeaterOrder ) {
	super();
	initialize( routeID,  deviceID, variableBits, repeaterOrder );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException {

	Object addValues[] = { getRouteID(),  getDeviceID(), getVariableBits(), getRepeaterOrder() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException {

	String constraintColumns[] = { "RouteID", "DeviceID" };
	Object constraintValues[] = { getRouteID(), getDeviceID() };
	
	delete( TABLE_NAME, constraintColumns, constraintValues );
}


/**
 * This method deletes all the rows in RepeaterRoute associated with the route identified
 * by routeID
 * @param routeID java.lang.Integer
 */
public final static boolean deleteRepeaterRoutes(Integer routeID, java.sql.Connection conn ) throws java.sql.SQLException 
{

	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement(
            "DELETE FROM " + TABLE_NAME + " WHERE RouteID=" + routeID,
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
 * @return java.lang.Integer
 */
public Integer getDeviceID() {
	return deviceID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getRepeaterOrder() {
	return repeaterOrder;
}

/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.route.RepeaterRoute[]
 * @param routeID java.lang.Integer
 */
public static final RepeaterRoute[] getRepeaterRoutes(Integer routeID, java.sql.Connection conn) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	//MAKE SURE THAT THE RESULT IS ORDERED BY RepeaterOrder!	
	String sql = "SELECT DeviceID,VariableBits,RepeaterOrder " + 
				 "FROM " + TABLE_NAME + " WHERE RouteID= ? ORDER BY RepeaterOrder";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection, connection should not be (null).");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, routeID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				tmpList.add( new RepeaterRoute( 
						routeID, 
						new Integer(rset.getInt("DeviceID")), 
						new Integer(rset.getInt("VariableBits")) ) );						
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, pstmt);
	}


	RepeaterRoute retVal[] = new RepeaterRoute[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}

/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static String isRepeaterUsed( Integer repeaterID ) throws java.sql.SQLException 
{
   com.cannontech.database.SqlStatement stmt =
      new com.cannontech.database.SqlStatement(
            "SELECT PAOName FROM " + 
            com.cannontech.database.db.pao.YukonPAObject.TABLE_NAME + " y, " +
            RepeaterRoute.TABLE_NAME + " r" +
            " WHERE r.DeviceID=" + repeaterID +
            " AND r.RouteID=y.PAObjectID",
            com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

   try
   {
      stmt.execute();
      if(stmt.getRowCount() > 0 )
         return stmt.getRow(0)[0].toString();
      else
         return null;
   }
   catch( Exception e )
   {
      return null;
   }

}

/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getRouteID() {
	return routeID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Character
 */
public Integer getVariableBits() {
	return variableBits;
}
/**
 * This method was created in VisualAge.
 * @param name java.lang.String
 * @param deviceID java.lang.Integer
 * @param variableBits java.lang.Character
 */
public void initialize(Integer routeID, Integer deviceID, Integer variableBits, Integer repeaterOrder) {

	setRouteID(routeID);
	setDeviceID( deviceID );
	setVariableBits( variableBits );
	setRepeaterOrder( repeaterOrder );
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "VariableBits", "RepeaterOrder" };

	String constraintColumns[] = { "RouteID", "DeviceID"  };
	Object constraintValues[] = { getRouteID(), getDeviceID() };

	Object results[] = retrieve(selectColumns, TABLE_NAME, constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		if( results[0] != null )
			setVariableBits( (Integer) results[0] );
		setRepeaterOrder( (Integer) results[1] );
	}
		
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setRepeaterOrder(Integer newValue) {
	this.repeaterOrder = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setRouteID(Integer newValue) {
	this.routeID = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Character
 */
public void setVariableBits(Integer newValue) {
	this.variableBits = newValue;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = { "VariableBits", "RepeaterOrder" };
	Object setValues[] = {  getVariableBits(), getRepeaterOrder() };

	String constraintColumns[] = { "RouteID", "DeviceID" };
	Object constraintValues[] = { getRouteID(), getDeviceID() };
	
	update( TABLE_NAME, setColumns, setValues, constraintColumns, constraintValues );
}
}
