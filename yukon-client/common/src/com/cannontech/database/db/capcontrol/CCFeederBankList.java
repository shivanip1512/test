package com.cannontech.database.db.capcontrol;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.SqlUtils;

/**
 * This type was created in VisualAge.
 */
public class CCFeederBankList extends com.cannontech.database.db.DBPersistent 
{
	private Integer feederID = null;
	private Integer deviceID = null;
	private Float controlOrder = null;
    private Float closeOrder = null;
    private Float tripOrder = null;
    private static JdbcOperations jdbcOps = JdbcTemplateHelper.getYukonTemplate();

	public static final String SETTER_COLUMNS[] = 
	{ 
		"ControlOrder", "TripOrder", "ControlOrder"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "FeederID", "DeviceID" };

	public static final String TABLE_NAME= "CCFeederBankList";
/**
 */
public CCFeederBankList(Integer feedID, Integer devID, Float order, Float clsOrd, Float trpOrd) 
{
	super();
	
	setFeederID( feedID );
	setDeviceID( devID );
	setControlOrder( order );
    setCloseOrder(clsOrd);
    setTripOrder(trpOrd);
}
/**
 * add method comment.
 */
@Override
public void add() throws java.sql.SQLException 
{
	Object[] addValues = { getFeederID(), getDeviceID(), getControlOrder(), getCloseOrder(), getTripOrder() };

	add( TABLE_NAME, addValues );
}
/**
 * delete method comment.
 */
@Override
public void delete() throws java.sql.SQLException 
{
	Object[] values = { getFeederID(), getDeviceID() };
	
	delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
	
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public static boolean deleteCapBanksFromFeederList(Integer feederId, Integer capBankID, java.sql.Connection conn) 
{
	java.sql.PreparedStatement pstmt = null;
	
	String sql = null;

	if( feederId != null )
	{
		sql = "DELETE FROM " + TABLE_NAME +
			 	" WHERE FeederID = " + feederId;
	}
	else if( capBankID != null )
	{
		sql = "DELETE FROM " + TABLE_NAME +
			 	" WHERE DeviceID = " + capBankID;
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

public static Integer getFeederIdForCapBank( int paoId ){
    String sql = "SELECT FeederId FROM " + TABLE_NAME +
                 " WHERE DeviceId = ? ";
    Integer feederId = -1;
    try {
        feederId = jdbcOps.queryForObject(sql, Integer.class, paoId);
    } catch (EmptyResultDataAccessException e) {
        return -1;
    }
    return feederId;
}

/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public static List<CCFeederBankList> getCapBanksOnFeederList(Integer feederId, java.sql.Connection conn ) 
{
    List<CCFeederBankList> returnList = null;
	java.sql.PreparedStatement pstmt = null;
	java.sql.ResultSet rset = null;
	
	String sql = "SELECT DeviceID, ControlOrder, CloseOrder, TripOrder FROM " + TABLE_NAME +
 				 " WHERE FeederID = ? ORDER BY ControlOrder";

	try
	{		
		if( conn == null )
		{
			throw new IllegalStateException("Database connection can not be null.");
		}
		else
		{
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt( 1, feederId.intValue() );
			
			rset = pstmt.executeQuery();
			returnList = new java.util.ArrayList<CCFeederBankList>(8); //rset.getFetchSize()
	
			while( rset.next() )
			{				
				returnList.add( 
						new CCFeederBankList(
							feederId, 
							new Integer( rset.getInt("DeviceID") ),
							new Float( rset.getFloat("ControlOrder")) ,
                            new Float (rset.getFloat("CloseOrder")),
                            new Float (rset.getFloat("TripOrder")))
                        );
                            
                        
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
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Float getControlOrder() {
	return controlOrder;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Integer
 */
public Integer getDeviceID() {
	return deviceID;
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
 * retrieve method comment.
 */
@Override
public void retrieve() throws java.sql.SQLException 
{
	/* WE DO NOT RETRIEVE ANY VALUES WITH THIS OBJECT */

}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setControlOrder(Float newValue) {
	this.controlOrder = newValue;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.lang.Integer
 */
public void setDeviceID(Integer newValue) {
	this.deviceID = newValue;
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
public Float getCloseOrder() {
    return closeOrder;
}
public void setCloseOrder(Float closeOrder) {
    this.closeOrder = closeOrder;
}
public Float getTripOrder() {
    return tripOrder;
}
public void setTripOrder(Float tripOrder) {
    this.tripOrder = tripOrder;
}
}
