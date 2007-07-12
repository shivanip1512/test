package com.cannontech.database.db.pao;

import com.cannontech.database.SqlUtils;

/**
 * This type was created in VisualAge.
 */
public class PAOowner extends com.cannontech.database.db.DBPersistent 
{	
	private Integer ownerID = new Integer(com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID);
	private Integer childID = new Integer(com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID);
	
	public final static String SETTER_COLUMNS[] = 
	{ 
		"ChildID"
	};

	public final static String CONSTRAINT_COLUMNS[] = { "OwnerID" };

	public final static String TABLE_NAME = "PAOowner";
/**
 * PointDispatch constructor comment.
 */
public PAOowner() 
{
	super();
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	if( getChildID().intValue() == com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID 
		 || getOwnerID().intValue() == com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID )
		return;  //do not add this entry

	Object addValues[] = { getOwnerID(), getChildID() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	delete( TABLE_NAME, CONSTRAINT_COLUMNS[0], getOwnerID() );
}
/**
 * delete method comment.
 */
public static final void deleteAllPAOowners( Integer paoID, java.sql.Connection conn)
{
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "Delete from " + TABLE_NAME +
				    " WHERE OwnerID= ?";
	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be (null).");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, paoID.intValue() );
			
			pstmt.executeUpdate();					
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

}
/**
 * This method was created in VisualAge.
 * @param ownerID java.lang.Integer
 */
public static final PAOowner[] getAllPAOownerChildren(Integer ownerID, java.sql.Connection conn ) throws java.sql.SQLException
{
	java.util.ArrayList tmpList = new java.util.ArrayList(30);
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;

	String sql = "SELECT ChildID " +
				 "FROM " + TABLE_NAME + " WHERE OwnerID= ?";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection should not be (null).");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, ownerID.intValue() );
			
			rset = pstmt.executeQuery();							
	
			while( rset.next() )
			{
				PAOowner item = new PAOowner();

				item.setDbConnection(conn);
				item.setOwnerID( ownerID );
				item.setChildID( new Integer(rset.getInt("ChildID")) );

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
		SqlUtils.close(rset, pstmt);
	}


	PAOowner retVal[] = new PAOowner[ tmpList.size() ];
	tmpList.toArray( retVal );
	
	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2001 11:16:10 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getChildID() {
	return childID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2001 11:16:10 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getOwnerID() {
	return ownerID;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	Object constraintValues[] = { getOwnerID() };

	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length == SETTER_COLUMNS.length )
	{		
		setChildID( (Integer) results[0] );
	}
	//do not throw an exception since we are not a 1-1 relationship
	
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2001 11:16:10 AM)
 * @param newChildID java.lang.Integer
 */
public void setChildID(java.lang.Integer newChildID) {
	childID = newChildID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/16/2001 11:16:10 AM)
 * @param newOwnerID java.lang.Integer
 */
public void setOwnerID(java.lang.Integer newOwnerID) {
	ownerID = newOwnerID;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	if( getChildID().intValue() == com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID 
		 || getOwnerID().intValue() == com.cannontech.database.db.device.Device.SYSTEM_DEVICE_ID )
		return;  //do not add this entry


	Object setValues[] = { getChildID() };					
	Object constraintValues[] = { getOwnerID() };
	Object addValues[] = { getOwnerID(), getChildID() };
	
	Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

	if( results.length > 0 )
	{
		if( results[0] != null )
			update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
		else
			add( TABLE_NAME, addValues );
	}
	else
		add( TABLE_NAME, addValues );
		
}
}
