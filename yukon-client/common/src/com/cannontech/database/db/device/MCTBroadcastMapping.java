package com.cannontech.database.db.device;

import com.cannontech.database.SqlUtils;

/**
 * Insert the type's description here.
 * Creation date: (6/18/2002 1:29:24 PM)
 * @author: 
 */
public class MCTBroadcastMapping extends com.cannontech.database.db.DBPersistent {
	private Integer mctBroadcastID;
	private Integer mctID;
	private Integer ordering;

	public static final String SETTER_COLUMNS[] = { "Ordering" };

	public static final String CONSTRAINT_COLUMNS[] = { "MctBroadcastID", "MctID" };

	public static final String TABLE_NAME = "MCTBroadcastMapping";

/**
 * MCTBroadcastMapping constructor comment.
 */
public MCTBroadcastMapping() {
	super();
}


/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException {
	
	Object addValues[] = { getMctBroadcastID(), getMctID(), getOrdering() };
	add( TABLE_NAME, addValues );
	
}


/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException {
	
	String values[] = { getMctBroadcastID().toString(), getMctID().toString() };

	delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
}


/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteAllBroadCastMappings(Integer mctBroadCastID, java.sql.Connection conn)
{
	java.sql.PreparedStatement pstmt = null;		
	String sql = "DELETE FROM " + TABLE_NAME + " WHERE MctBroadCastID=" + mctBroadCastID;

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be (null)");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.executeUpdate();
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
			if( pstmt != null ) 
				pstmt.close();
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}

	return true;
}


/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static final com.cannontech.common.util.NativeIntVector getAllMCTsIDList(Integer MCTBroadcastID, java.sql.Connection conn ) throws java.sql.SQLException
{
	com.cannontech.common.util.NativeIntVector tmpList = new com.cannontech.common.util.NativeIntVector(30);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT MctID " +
				 "FROM " + TABLE_NAME + " WHERE MCTBroadcastID= ?" + " ORDER BY Ordering";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be (null).");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, MCTBroadcastID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				tmpList.add( rset.getInt("MctID") );
			}
					
		}		
	}
	catch( java.sql.SQLException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		SqlUtils.close(rset, pstmt );
	}

	return tmpList;
}

/**
 * Insert the method's description here.
 * Creation date: (6/18/2002 1:49:55 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMctBroadcastID() {
	return mctBroadcastID;
}


/**
 * Insert the method's description here.
 * Creation date: (6/18/2002 1:49:55 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMctID() {
	return mctID;
}


/**
 * Insert the method's description here.
 * Creation date: (6/18/2002 1:49:55 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getOrdering() {
	return ordering;
}


/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "Ordering" };
	String constraintColumns[] = { "MCTBroadcastID", "mctID" };

	Object constraintValues[] = { getMctBroadcastID(), getMctID() };

	Object results[] = retrieve( selectColumns, "MCTBroadcastMapping", constraintColumns, constraintValues );

	if( results.length == selectColumns.length )
	{
		setOrdering( (Integer) results[0] );
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/18/2002 1:49:55 PM)
 * @param newMctBroadcastID java.lang.Integer
 */
public void setMctBroadcastID(java.lang.Integer newMctBroadcastID) {
	mctBroadcastID = newMctBroadcastID;
}


/**
 * Insert the method's description here.
 * Creation date: (6/18/2002 1:49:55 PM)
 * @param newMctID java.lang.Integer
 */
public void setMctID(java.lang.Integer newMctID) {
	mctID = newMctID;
}


/**
 * Insert the method's description here.
 * Creation date: (6/18/2002 1:49:55 PM)
 * @param newOrdering java.lang.Integer
 */
public void setOrdering(java.lang.Integer newOrdering) {
	ordering = newOrdering;
}


/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = { "Ordering" };
	Object setValues[] = { getOrdering() };

	String constraintColumns[] = { "MctBroadcastID", "mctID" };
	Object constraintValues[] = { getMctBroadcastID(), getMctID() };

	update( "MCTBroadcastMapping", setColumns, setValues, constraintColumns, constraintValues );
}

/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static final MCTBroadcastMapping[] getAllMCTsList(Integer MCTBroadcastID, java.sql.Connection conn ) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT MctID, Ordering " + "FROM " + TABLE_NAME + " WHERE MctBroadcastID= ?";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be null.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, MCTBroadcastID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				MCTBroadcastMapping item = new MCTBroadcastMapping();

				item.setDbConnection(conn);
				item.setMctID( new Integer(rset.getInt("mctID")) );
				item.setOrdering( new Integer(rset.getInt("ordering")) );
				item.setMctBroadcastID( MCTBroadcastID );

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


	MCTBroadcastMapping retVal[] = new MCTBroadcastMapping[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
}