/*
 * Created on Jun 23, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.customer;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeviceCustomerList extends DBPersistent
{
	private Integer deviceID = null;
	private Integer customerID = null;

	public static final String SETTER_COLUMNS[] = 
	{ 
		"CustomerID"
	};

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID" };

	public static final String TABLE_NAME = "DeviceCustomerList";

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException
	{
		Object addValues[] = { getDeviceID(), getCustomerID() };

		add( TABLE_NAME, addValues );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException
	{
		Integer values[] = { getDeviceID() };
		delete( TABLE_NAME, CONSTRAINT_COLUMNS, values );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException
	{
		Object constraintValues[] = { getDeviceID() };
		Object results[] = retrieve( SETTER_COLUMNS, TABLE_NAME, CONSTRAINT_COLUMNS, constraintValues );

		if( results.length == SETTER_COLUMNS.length )
		{
			setCustomerID( (Integer) results[0] );
		}
		else
			throw new Error(getClass() + " - Incorrect Number of results retrieved");
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException
	{
		Object setValues[] = { getCustomerID() };

			Object constraintValues[] = { getDeviceID() };

			update( TABLE_NAME, SETTER_COLUMNS, setValues, CONSTRAINT_COLUMNS, constraintValues );
	}

	/**
	 * @return
	 */
	public Integer getCustomerID()
	{
		return customerID;
	}

	/**
	 * @return
	 */
	public Integer getDeviceID()
	{
		return deviceID;
	}

	/**
	 * @param integer
	 */
	public void setCustomerID(Integer integer)
	{
		customerID = integer;
	}

	/**
	 * @param integer
	 */
	public void setDeviceID(Integer integer)
	{
		deviceID = integer;
	}
	
  /**
   * This method was created by Cannon Technologies Inc.
   * @return boolean
   * @param deviceID java.lang.Integer
   */
  public static boolean deleteDeviceCustomerList(Integer customerID )
  {
	  return deleteDeviceCustomerList( customerID, "yukon");
  }
  /**
   * This method was created by Cannon Technologies Inc.
   * @return boolean
   * @param deviceID java.lang.Integer
   */
  public static boolean deleteDeviceCustomerList(Integer customerID, String databaseAlias )
  {
  	  java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
	  return deleteDeviceCustomerList( customerID, conn);
  } 
  /**
   * This method was created by Cannon Technologies Inc.
   * @return boolean
   * @param deviceID java.lang.Integer
   */
  public static boolean deleteDeviceCustomerList(Integer customerID, java.sql.Connection conn)
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
   * @return DeviceCustomerList[]
   * @param stateGroup java.lang.Integer
   */
  public static final DeviceCustomerList[] getAllDeviceCustomerList(Integer customerID) throws java.sql.SQLException 
  {
	  return getAllDeviceCustomerList(customerID, "yukon");												
  }
  
  /**
	 * This method was created in VisualAge.
	 * @return DeviceCustomerList[]
	 * @param stateGroup java.lang.Integer
	 */
  public static final DeviceCustomerList[] getAllDeviceCustomerList(Integer customerID, String databaseAlias) throws java.sql.SQLException 
  {
  	java.sql.Connection conn = null;
  	conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
  	return getAllDeviceCustomerList(customerID, conn);												
  }  
  /**
   * This method was created in VisualAge.
   * @return DeviceCustomerList[]
   * @param stateGroup java.lang.Integer
   */
  public static final DeviceCustomerList[] getAllDeviceCustomerList(Integer customerID, java.sql.Connection conn) throws java.sql.SQLException
  {
	  java.util.ArrayList tmpList = new java.util.ArrayList(30);
	  java.sql.PreparedStatement pstmt = null;
	  java.sql.ResultSet rset = null;

	  String sql = "SELECT DeviceID, CustomerID " +
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
				  DeviceCustomerList item = new DeviceCustomerList();

				  item.setDbConnection(conn);
				  item.setDeviceID( new Integer(rset.getInt("DeviceID")) );
				  item.setCustomerID( new Integer(rset.getInt("CustomerID")) );
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
			  if( conn != null ) conn.close();
		  } 
		  catch( java.sql.SQLException e2 )
		  {
			  com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
		  }	
	  }


	  DeviceCustomerList retVal[] = new DeviceCustomerList[ tmpList.size() ];
	  tmpList.toArray( retVal );
	
	  return retVal;
  }
}
