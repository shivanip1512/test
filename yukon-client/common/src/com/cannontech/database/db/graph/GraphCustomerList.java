package com.cannontech.database.db.graph;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;

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
 * @param customerID
 * @param conn
 * @return
 */
public static synchronized boolean deleteGraphCustomerList(Integer customerID, java.sql.Connection conn )
{
	try
	{
		if( conn == null )
			throw new IllegalStateException("Database connection should not be null.");
    			
		java.sql.Statement stat = conn.createStatement();
			
		stat.execute( "DELETE FROM " +
			GraphCustomerList.TABLE_NAME +
			" WHERE CustomerID=" + customerID );
				
		if( stat != null )
			stat.close();
	}
	catch(Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}
	return true;
} 
/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public synchronized static boolean deleteGraphCustomerList(Integer customerID)
{
	boolean results = false;
	java.sql.Connection c = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	results = GraphCustomerList.deleteGraphCustomerList(customerID, c);
	try{
		c.close();
	}
	catch (SQLException e){
		e.printStackTrace();
	}	
	return results;		
}
	
/**
 * @param customerID
 * @param conn
 * @return
 * @throws java.sql.SQLException
 */
public synchronized static final GraphCustomerList[] getGraphCustomerList(Integer customerID, java.sql.Connection conn) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = 
			"SELECT GraphDefinitionID,CustomerID,CustomerOrder " +
			"FROM " + GraphCustomerList.TABLE_NAME + 
			" WHERE CustomerID= ?";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be null.");
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
 * @param customerID
 * @return
 * @throws java.sql.SQLException
 */
public synchronized static GraphCustomerList[] getGraphCustomerList(Integer customerID) throws java.sql.SQLException
{
	GraphCustomerList retVal[] = null;
	java.sql.Connection c = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
	retVal = GraphCustomerList.getGraphCustomerList(customerID, c);
	try{
		c.close();
	}
	catch (SQLException e){
		e.printStackTrace();
	}	
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
