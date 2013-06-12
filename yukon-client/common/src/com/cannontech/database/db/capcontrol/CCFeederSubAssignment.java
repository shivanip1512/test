package com.cannontech.database.db.capcontrol;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.SqlUtils;

/**
 * This type was created in VisualAge.
 */
public class CCFeederSubAssignment extends com.cannontech.database.db.DBPersistent 
{
	private Integer substationBusID = null;
	private Integer feederID = null;
	private Integer displayOrder = null;
	private static JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();

	public static final String SETTER_COLUMNS[] = 
	{ 
		"DisplayOrder"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "SubstationBusID", "FeederID" };

	public static final String TABLE_NAME= "CCFeederSubAssignment";
/**
 */
public CCFeederSubAssignment(Integer feedID, Integer subID, Integer dispOrder) 
{
	super();
	
	setFeederID( feedID );
	setSubstationBusID( subID );
	setDisplayOrder( dispOrder );
}
/**
 * add method comment.
 */
@Override
public void add() throws java.sql.SQLException 
{
	Object[] addValues = { getSubstationBusID(), getFeederID(), getDisplayOrder() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
@Override
public void delete() throws java.sql.SQLException 
{
	Object[] values = { getFeederID(), getSubstationBusID() };
	
	delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
	
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public static boolean deleteCCFeedersFromSubList(Integer subId, Integer feederID, java.sql.Connection conn) 
{
	java.sql.PreparedStatement pstmt = null;
	
	String sql = null;
	
	if( subId != null )
	{
		//must be deleting a SubBus
		sql = "DELETE FROM " + TABLE_NAME +
				" WHERE SubstationBusID = " + subId;
	}
	else if( feederID != null )
	{
		//must be deleting a feeder
		sql = "DELETE FROM " + TABLE_NAME +
				" WHERE FeederID = " + feederID;
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

public static Integer getSubBusIdForFeeder( int paoId ){
    String sql = "SELECT SubstationBusId FROM " + TABLE_NAME +
    " WHERE Feederid = ? ";
    
    Integer substationBusId = -1;
    try {
        substationBusId = jdbcOps.queryForInt(sql, paoId);
    } catch (EmptyResultDataAccessException e) {
        return -1;
    }
    
    return substationBusId;
}

/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public static List<CCFeederSubAssignment> getCCFeedersOnSub(Integer subId, java.sql.Connection conn ) 
{
    List<CCFeederSubAssignment> returnList = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT FeederID, DisplayOrder FROM " + TABLE_NAME +
 				 " WHERE SubstationBusID = ? ORDER BY DisplayOrder";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection can not be null.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, subId.intValue() );
			
			rset = pstmt.executeQuery();
			returnList = new ArrayList<CCFeederSubAssignment>(16); //rset.getFetchSize()
	
			while( rset.next() )
			{				
				returnList.add( 
						new CCFeederSubAssignment(
							new Integer( rset.getInt("FeederID") ),
							subId, 							
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
public java.lang.Integer getFeederID() {
	return feederID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 2:02:03 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getSubstationBusID() {
	return substationBusID;
}
/**
 * retrieve method comment.
 */
@Override
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
 * @param newFeederID java.lang.Integer
 */
public void setFeederID(java.lang.Integer newFeederID) {
	feederID = newFeederID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 2:02:03 PM)
 * @param newSubstationBusID java.lang.Integer
 */
public void setSubstationBusID(java.lang.Integer newSubstationBusID) {
	substationBusID = newSubstationBusID;
}
/**
 * update method comment.
 */
@Override
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
