package com.cannontech.database.db.capcontrol;

import java.util.ArrayList;

import com.cannontech.database.SqlUtils;

/**
 * This type was created in VisualAge.
 */
public class CCSubstationSubBusList extends com.cannontech.database.db.DBPersistent 
{
	private Integer substationID = null;
	private Integer substationBusID = null;
	private Integer displayOrder = null;


	public static final String SETTER_COLUMNS[] = 
	{ 
		"DisplayOrder"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "SubstationID", "SubstationBusID" };

	public static final String TABLE_NAME= "CCSubstationSubBusList";
/**
 */
public CCSubstationSubBusList(Integer substationID, Integer subBusID, Integer dispOrder) 
{
	super();
	
	setSubstationID( substationID );
	setSubstationBusID( subBusID );
	setDisplayOrder( dispOrder );
}
/**
 * add method comment.
 */
public void add() throws java.sql.SQLException 
{
	Object[] addValues = { getSubstationID(), getSubstationBusID(), getDisplayOrder() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
public void delete() throws java.sql.SQLException 
{
	Object[] values = { getSubstationID(), getSubstationBusID() };
	
	delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
	
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public static boolean deleteCCSubBusFromSubstationList(Integer substationID, Integer subBusID, java.sql.Connection conn) 
{
	java.sql.PreparedStatement pstmt = null;
	
	String sql = null;
	
	if( substationID != null )
	{
		//must be deleting a substation
		sql = "DELETE FROM " + TABLE_NAME +
				" WHERE SubstationID = " + substationID;
	}
	else if( subBusID != null )
	{
		//must be deleting a subBus
		sql = "DELETE FROM " + TABLE_NAME +
				" WHERE substationBusID = " + subBusID;
	}
	

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection can not be null.");
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
		return false;
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
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public static ArrayList getCCSubBusesOnSubstation(Integer substationId, java.sql.Connection conn ) 
{
	ArrayList returnList = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT substationBusID, DisplayOrder FROM " + TABLE_NAME +
 				 " WHERE SubstationID = ? ORDER BY DisplayOrder";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection can not be null.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, substationId.intValue() );
			
			rset = pstmt.executeQuery();
			returnList = new ArrayList(16); //rset.getFetchSize()
	
			while( rset.next() )
			{				
				returnList.add( 
						new CCSubstationSubBusList(
							substationId,
							new Integer( rset.getInt("substationBusID") ),
							new Integer( rset.getInt("DisplayOrder")) ) );
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


	return returnList;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 2:02:03 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDisplayOrder() {
	return displayOrder;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 11:47:10 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSubstationBusID() {
	return substationBusID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 2:02:03 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSubstationID() {
	return substationID;
}
/**
 * retrieve method comment.
 */
public void retrieve() throws java.sql.SQLException 
{
	/* WE DO NOT RETRIEVE ANY VALUES WITH THIS OBJECT */

}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 2:02:03 PM)
 * @param newDisplayOrder java.lang.Integer
 */
public void setDisplayOrder(java.lang.Integer newDisplayOrder) {
	displayOrder = newDisplayOrder;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 11:47:10 AM)
 * @param newSubBusID java.lang.Integer
 */
public void setSubstationBusID(java.lang.Integer newSubBusID) {
	substationBusID = newSubBusID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 2:02:03 PM)
 * @param newSubstationBusID java.lang.Integer
 */
public void setSubstationID(java.lang.Integer newSubstationID) {
	substationID = newSubstationID;
}
/**
 * update method comment.
 */
public void update() throws java.sql.SQLException 
{
	/* WE DO NOT UPDATE ANY VALUES WITH THIS OBJECT */

	
/*	String setColumns[] = { "DeviceID", "ControlOrder" };
	Object setValues[]= { getDeviceID(), getControlOrder() };

	String constraintColumns[] = { "CapStrategyID" };
	Object constraintValues[] = { getCapStrategyID() };

	update( this.tableName, setColumns, setValues, constraintColumns, constraintValues );
*/
}
}
