/*
 * Created on Dec 17, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.config;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

public class MCTConfigMapping extends com.cannontech.database.db.DBPersistent {
	private Integer mctID;
	private Integer configID;

	public static final String SETTER_COLUMNS[] = { "configID" };

	public static final String CONSTRAINT_COLUMNS[] = { "mctID" };

	public static final String TABLE_NAME = "MCTConfigMapping";

/**
 * MCTConfigMapping constructor comment.
 */
public MCTConfigMapping() {
	super();
}


/**
 * This method was created by a SmartGuide.
 * @exception java.sql.SQLException The exception description.
 */
public void add() throws java.sql.SQLException {
	
		
	Object addValues[] = { getMctID(), getConfigID() };
	deleteAMapping(getMctID(), getDbConnection());
	add( TABLE_NAME, addValues );
	
}


/**
 * This method was created by a SmartGuide.
 */
public void delete() throws java.sql.SQLException {
	
	String values[] = { getMctID().toString(), getConfigID().toString() };

	delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
}


/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static boolean deleteAMapping(Integer mctID, java.sql.Connection conn)
{
	java.sql.PreparedStatement pstmt = null;		
	String sql = "DELETE FROM " + TABLE_NAME + " WHERE mctID=" + mctID;

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

public void deleteAMapping(Integer mctID)
{
	try
	{
		java.sql.Connection conn = null;
	
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
	
		deleteAMapping(mctID, conn);
			
		conn.close();
	}
	catch( java.sql.SQLException e2 )
	{
		com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
	}	
}
/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static final java.util.Vector getAllMCTsIDList(Integer configID, java.sql.Connection conn ) throws java.sql.SQLException
{
	java.util.Vector tmpList = new java.util.Vector();
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT mctID FROM " + TABLE_NAME + " WHERE configID= ?";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be (null).");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, configID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				tmpList.add( new Integer(rset.getInt("mctID")) );
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
		} 
		catch( java.sql.SQLException e2 )
		{
			com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		}	
	}

	return tmpList;
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
public java.lang.Integer getConfigID() {
	return configID;
}

/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException {

	String selectColumns[] = { "configID" };
	String constraintColumns[] = { "mctID"};

	Object constraintValues[] = { getMctID() };

	Object results[] = retrieve( selectColumns, "MCTConfigMapping", constraintColumns, constraintValues );

	
}


/**
 * Insert the method's description here.
 * Creation date: (6/18/2002 1:49:55 PM)
 * @param newmctID java.lang.Integer
 */
public void setmctID(java.lang.Integer newmctID) {
	mctID = newmctID;
}


/**
 * Insert the method's description here.
 * Creation date: (6/18/2002 1:49:55 PM)
 * @param newconfigID java.lang.Integer
 */
public void setconfigID(java.lang.Integer newconfigID) {
	configID = newconfigID;
}


/**
 * update method comment.
 */
public void update() throws java.sql.SQLException {

	String setColumns[] = { "configID" };
	Object setValues[] = { getConfigID() };

	String constraintColumns[] = { "mctID" };
	Object constraintValues[] = { getMctID()};

	update( "MCTConfigMapping", setColumns, setValues, constraintColumns, constraintValues );
}

/**
 * This method was created by Cannon Technologies Inc.
 * @return boolean
 * @param deviceID java.lang.Integer
 */
public static final Integer getTheConfigID(Integer mctID, java.sql.Connection conn ) throws java.sql.SQLException
{
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	Integer returnedID = new Integer(0);

	String sql = "SELECT configID FROM " + TABLE_NAME + " WHERE mctID= ?";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be null.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, mctID.intValue() );
			
			rset = pstmt.executeQuery();							
			rset.next();
			returnedID = new Integer(rset.getInt(1));
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

	return returnedID;
}

public final static boolean hasConfig(Integer mctID) throws java.sql.SQLException 
{	
	return hasConfig(mctID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean hasConfig(Integer mctID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT ConfigID FROM " + TABLE_NAME + " WHERE MctID=" + mctID,
													databaseAlias );

	try
	{
		stmt.execute();
		return (stmt.getRowCount() > 0 );
	}
	catch( Exception e )
	{
		return false;
	}
}
}