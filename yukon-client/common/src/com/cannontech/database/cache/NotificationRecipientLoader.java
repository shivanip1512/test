package com.cannontech.database.cache;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
class NotificationRecipientLoader implements Runnable 
{
	private java.util.ArrayList allLocations = null;
	
	private String databaseAlias = null;
/**
 * StateGroupLoader constructor comment.
 */
public NotificationRecipientLoader(java.util.ArrayList allLocationsArray, String alias) {
	super();
	this.allLocations = allLocationsArray;
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
	String sqlString = "SELECT RecipientID, RecipientName,EMAILADDRESS, EmailSendType, PAGERNUMBER " +
		"FROM NotificationRecipient WHERE RecipientID >= 0 ORDER BY EMAILADDRESS";

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
			int locID = rset.getInt(1);
			String locName = rset.getString(2).trim();
			String emailAddress = rset.getString(3).trim();
			int sendType = rset.getInt(4);
			String pagerNumber = rset.getString(5).trim();

			com.cannontech.database.data.lite.LiteNotificationRecipient gl =
					new com.cannontech.database.data.lite.LiteNotificationRecipient(locID, locName, 
						emailAddress, sendType, pagerNumber);
				
			allLocations.add(gl);
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
System.out.print( (timerStop.getTime() - timerStart.getTime())*.001 );
com.cannontech.clientutils.CTILogger.info( " Secs for NotificationGroupRecipientLoader" );
//temp code
	}
}
}
