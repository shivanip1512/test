package com.cannontech.database.cache;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
class CustomerContactLoader implements Runnable {
	private java.util.ArrayList allCustomerContacts = null;
	private String databaseAlias = null;
/**
 * StateGroupLoader constructor comment.
 */
public CustomerContactLoader(java.util.ArrayList customerContactArray, String alias) {
	super();
	this.allCustomerContacts = customerContactArray;
	this.databaseAlias = alias;
}
/**
 * run method comment.
 */
public void run() {
//temp code
java.util.Date timerStart = null;
java.util.Date timerStop = null;
//temp code

//temp code
timerStart = new java.util.Date();
//temp code

	//get all the customer contacts that are assigned to a customer
	String sqlString = "SELECT cus.contactID, cus.ContFirstName, cus.ContLastName, cont.deviceID, cus.Loginid " + 
	 		"FROM CustomerContact cus, CICustContact cont " + 
	 		"WHERE cus.contactID=cont.contactID order by cus.contactID";

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
			int contactID = rset.getInt(1);
			String contFirstName = rset.getString(2).trim();
			String contLastName = rset.getString(3).trim();
			int deviceID = rset.getInt(4);
			int loginID = rset.getInt(5);
			
			com.cannontech.database.data.lite.LiteCustomerContact lc =
				new com.cannontech.database.data.lite.LiteCustomerContact(contactID, contFirstName, contLastName, deviceID, loginID);

			allCustomerContacts.add(lc);
		}
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();
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
			e.printStackTrace();
		}
//temp code
timerStop = new java.util.Date();
com.cannontech.clientutils.CTILogger.info( 
    (timerStop.getTime() - timerStart.getTime())*.001 + " Secs for CustomerContactLoader" );
//temp code
	}
}
}
