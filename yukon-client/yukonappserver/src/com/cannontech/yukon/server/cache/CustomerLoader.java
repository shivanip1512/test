package com.cannontech.yukon.server.cache;

import java.util.Map;
import java.util.Vector;

import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.db.customer.Customer;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class CustomerLoader implements Runnable 
{
	//	Map<Integer(custID), LiteCustomer>
	private Map allCustsMap = null;
	private java.util.ArrayList allCustomers = null;
	private String databaseAlias = null;

	/**
	 * CustomerLoader constructor comment.
	 */
	public CustomerLoader(java.util.ArrayList custArray, Map custMap, String alias) 
	{
		super();
		this.allCustomers = custArray;
		this.databaseAlias = alias;
		this.allCustsMap = custMap;
	}

	/**
	 * run method comment.
	 */
	public void run() 
	{
	//temp code
	java.util.Date timerStart = null;
	java.util.Date timerStop = null;
	//temp code
	
	//temp code
	timerStart = new java.util.Date();
	//temp code
	
		//get all the customer contacts that are assigned to a customer
		String sqlString = 
         "select CustomerID, PrimaryContactID, TimeZone, CustomerTypeID " +
         "from " + Customer.TABLE_NAME;
	
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection( this.databaseAlias );
			stmt = conn.createStatement();
			rset = stmt.executeQuery(sqlString);
	
			while (rset.next())
			{
				int cstID = rset.getInt(1);
				int contactID = rset.getInt(2);
				String timeZone = rset.getString(3).trim();
				int custTypeID = rset.getInt(4);				
				
				LiteCustomer lc = new LiteCustomer( cstID );
				lc.setPrimaryContactID(contactID);
				lc.setTimeZone(timeZone);
				lc.setCustomerTypeID(custTypeID);
				allCustomers.add(lc);
			}
			
			
			sqlString = 
				"SELECT ca.CustomerID, ca.ContactID " + 
				"FROM CustomerAdditionalContact ca, " + 
				Customer.TABLE_NAME + " c " + 
				"WHERE ca.CustomerID=c.CustomerID " +
				"ORDER BY ca.CustomerID";
			
			Vector vectVals = new Vector(30);
			rset = stmt.executeQuery(sqlString);

			while( rset.next() )
			{
				//maps CustomerID,ContactID
				int[] vals = { rset.getInt(1), rset.getInt(2) };
				vectVals.add( vals );
			}
			
			//assign our Contacts to their owner Customer
			for( int i = 0; i < allCustomers.size(); i++ )
			{
				LiteCustomer lc = (LiteCustomer)allCustomers.get(i);
				boolean found = false; //tries to make this fast
				
				for( int j = 0; j < vectVals.size(); j++ )
				{
					int[] map = (int[])vectVals.get(j);
					if( map[0] == lc.getCustomerID() )
					{
						lc.getAdditionalContacts().add( ContactFuncs.getContact(map[1]) );
						
						found = true;
					}
					else if( found )  //speed it up!
						break;
				}
				//Put all customers in the map
				allCustsMap.put( new Integer(lc.getCustomerID()), lc);
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
				if( stmt != null )
					stmt.close();
				if( conn != null )
					conn.close();
			}
			catch( java.sql.SQLException e )
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
	//temp code
	timerStop = new java.util.Date();
	com.cannontech.clientutils.CTILogger.info( 
	    (timerStop.getTime() - timerStart.getTime())*.001 + 
	      " Secs for CICustomerLoader with Contacts (" + allCustomers.size() + " loaded)" );
	//temp code
		}
	}
}
