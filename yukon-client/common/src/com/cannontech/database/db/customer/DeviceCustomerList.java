/*
 * Created on Jun 23, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.db.customer;

import java.sql.SQLException;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
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

	public static final String CONSTRAINT_COLUMNS[] = { "DeviceID"};

	public static final String TABLE_NAME = "DeviceCustomerList";

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException
	{
		Object addValues[] = { getCustomerID(), getDeviceID() };

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
	 * @param customerID
	 * @param conn
	 * @return
	 */
	public static synchronized boolean deleteDeviceCustomerList(Integer customerID, java.sql.Connection conn )
	{
		try
		{
			if( conn == null )
				throw new IllegalStateException("Database connection should not be null.");
	
			java.sql.Statement stat = conn.createStatement();
			
			stat.execute( "DELETE FROM " + 
					DeviceCustomerList.TABLE_NAME + 
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
	 * @param customerID
	 * @return
	 */
	public static synchronized boolean deleteDeviceCustomerList(Integer customerID)
	{
		boolean results = false;
		java.sql.Connection c = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
		results = DeviceCustomerList.deleteDeviceCustomerList(customerID, c);
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
	public synchronized static final DeviceCustomerList[] getDeviceCustomerList(Integer customerID, java.sql.Connection conn) throws java.sql.SQLException
	{
		java.util.ArrayList tmpList = new java.util.ArrayList(30);
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;

		String sql = 
				"SELECT DeviceID,CustomerID " +
				"FROM " + DeviceCustomerList.TABLE_NAME + 
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
				if( rset != null ) rset.close();
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
	/**
	 * @param customerID
	 * @return
	 * @throws java.sql.SQLException
	 */
	public synchronized static final DeviceCustomerList[] getDeviceCustomerList(Integer customerID) throws java.sql.SQLException
	{
		DeviceCustomerList retVal[] = null; 
		java.sql.Connection c = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
		retVal = DeviceCustomerList.getDeviceCustomerList(customerID, c);
		try{
			c.close();
		}
		catch (SQLException e){
			e.printStackTrace();
		}	
		return retVal;
	}
}
