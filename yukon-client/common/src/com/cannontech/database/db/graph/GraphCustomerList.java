package com.cannontech.database.db.graph;

/**
 * This type was created in VisualAge.
 */

public class GraphCustomerList extends com.cannontech.database.db.DBPersistent
{
	private Integer graphDefinitionID = null;
	private Integer customerID = null;
	private Integer customerOrder = new Integer(1);


	public static final String SETTER_COLUMNS[] = 
	{ 
		"CustomerID", "CustomerOrder"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "GraphDefinitionID" };

	public static final String TABLE_NAME = "GraphCustomerList";

/**
 * LMGroupVersacomSerial constructor comment.
 */
public GraphCustomerList() {
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object addValues[] = { getGraphDefinitionID(),
					getCustomerID(), getCustomerOrder() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	Integer values[] = { getGraphDefinitionID() };

	delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
}
/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteCustomerGraphList(Integer customerID, java.sql.Connection conn)
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("DELETE FROM " + TABLE_NAME + " WHERE CustomerID=" + customerID,
												 conn);
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
 * @return com.cannontech.database.db.point.State[]
 * @param stateGroup java.lang.Integer
 */
public static final GraphCustomerList[] getAllGraphCustomerList(Integer customerID, java.sql.Connection conn) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT GraphDefinitionID,CustomerID,CustomerOrder " +
				 "FROM " + TABLE_NAME + " WHERE CustomerID= ?";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Error getting database connection.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, customerID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				GraphCustomerList item = new GraphCustomerList();

				item.setDbConnection(conn);
				item.setGraphDefinitionID( new Integer(rset.getInt("GraphDefinitionID")) );
				item.setCustomerID( new Integer(rset.getInt("CustomerID")) );
				item.setCustomerOrder( new Integer(rset.getInt("CustomerOrder")) );

				tmpList.add( item );
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
			if( rset != null ) rset.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}


	GraphCustomerList retVal[] = new GraphCustomerList[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/2001 2:47:50 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCustomerID() {
	return customerID;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/2001 2:47:50 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCustomerOrder() {
	return customerOrder;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/2001 2:47:50 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getGraphDefinitionID() {
	return graphDefinitionID;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getGraphDefinitionID() };
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{
		setCustomerID( (Integer) results[0] );
		setCustomerOrder( (Integer) results[1] );
	}
	else
		throw new Error(getClass() + " - Incorrect Number of results retrieved");

}
/**
 * Insert the method's description here.
 * Creation date: (6/1/2001 2:47:50 PM)
 * @param newCustomerID java.lang.Integer
 */
public void setCustomerID(java.lang.Integer newCustomerID) {
	customerID = newCustomerID;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/2001 2:47:50 PM)
 * @param newCustomerOrder java.lang.Integer
 */
public void setCustomerOrder(java.lang.Integer newCustomerOrder) {
	customerOrder = newCustomerOrder;
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/2001 2:47:50 PM)
 * @param newGraphDefinitionID java.lang.Integer
 */
public void setGraphDefinitionID(java.lang.Integer newGraphDefinitionID) {
	graphDefinitionID = newGraphDefinitionID;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	Object setValues[] = { getCustomerID(), getCustomerOrder() };

	Object constraintValues[] = { getGraphDefinitionID() };

	update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
}
}
