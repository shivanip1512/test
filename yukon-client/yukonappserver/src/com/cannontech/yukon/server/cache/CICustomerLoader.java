package com.cannontech.yukon.server.cache;

import java.util.Vector;

import com.cannontech.database.cache.functions.CustomerContactFuncs;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.db.customer.CICustomerBase;
import com.cannontech.database.db.customer.Customer;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class CICustomerLoader implements Runnable 
{
	private java.util.ArrayList allCICustomers = null;
	private String databaseAlias = null;

	/**
	 * CICustomerLoader constructor comment.
	 */
	public CICustomerLoader(java.util.ArrayList ciCustArray, String alias) 
	{
		super();
		this.allCICustomers = ciCustArray;
		this.databaseAlias = alias;
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
         "select ci.CustomerID, ci.MainAddressID, ci.CompanyName, " +
         "c.PrimaryContactID, c.TimeZone, ci.CustomerDemandLevel, " +
         "ci.CurtailAmount " +
         "from " + CICustomerBase.TABLE_NAME + " ci, " +
         Customer.TABLE_NAME + " c where ci.CustomerID = c.CustomerID " +
         " order by ci.CompanyName";
	
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
				int addressID = rset.getInt(2);
				String name = rset.getString(3).trim();
				int contactID = rset.getInt(4);
				String timeZone = rset.getString(5).trim();				
				double dmdLevel = rset.getDouble(4);
				double curtAmount = rset.getDouble(4);
				
				LiteCICustomer lc =
					new LiteCICustomer( cstID, name );
				
				lc.setMainAddressID( addressID );
				lc.setPrimaryContactID( contactID );
				lc.setTimeZone( timeZone );
				lc.setDemandLevel( dmdLevel );
				lc.setCurtailAmount( curtAmount );
				
	
				allCICustomers.add(lc);
			}
			
			
			sqlString = 
				"SELECT ca.CustomerID, ca.ContactID " + 
				"FROM CustomerAdditionalContact ca, " + 
				Customer.TABLE_NAME + " c, " + 
				CICustomerBase.TABLE_NAME + " ci " +
				"WHERE c.CustomerID=ci.CustomerID " +
				"AND ca.CustomerID=ci.CustomerID " +
				"ORDER BY ca.CustomerID";
			
			Vector vectVals = new Vector(30);
			rset = stmt.executeQuery(sqlString);

			while( rset.next() )
			{
				//maps CustomerID,ContactID
				int[] vals = { rset.getInt(1), rset.getInt(2) };
				vectVals.add( vals );
			}
			
			//assign our Contacts to their owner CICustomer
			for( int i = 0; i < allCICustomers.size(); i++ )
			{
				LiteCICustomer lc = (LiteCICustomer)allCICustomers.get(i);
				boolean found = false; //tries to make this fast
				
				for( int j = 0; j < vectVals.size(); j++ )
				{
					int[] map = (int[])vectVals.get(j);
					if( map[0] == lc.getCustomerID() )
					{
						lc.getAdditionalContacts().add(
							CustomerContactFuncs.getCustomerContact(map[1]) );
						
						found = true;
					}
					else if( found )  //speed it up!
						break;
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
	      " Secs for CICustomerLoader with Contacts (" + allCICustomers.size() + " loaded)" );
	//temp code
		}
	}
}
