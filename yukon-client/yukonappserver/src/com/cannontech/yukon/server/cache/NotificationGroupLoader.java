package com.cannontech.yukon.server.cache;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
class NotificationGroupLoader implements Runnable 
{
	private java.util.ArrayList allNotificationGroups = null;
	private java.util.ArrayList allUsedNotificationRecipients = new java.util.ArrayList();
	
	private String databaseAlias = null;
/**
 * StateGroupLoader constructor comment.
 */
public NotificationGroupLoader(java.util.ArrayList notificationGroupArray, String alias) {
	super();
	this.allNotificationGroups = notificationGroupArray;
	this.databaseAlias = alias;
}
/**
 * Insert the method's description here.
 * Creation date: (11/21/00 9:14:40 AM)
 * @return java.util.List
 */
public java.util.ArrayList getAllUsedNotificationRecipients() 
{
	return allUsedNotificationRecipients;
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
	String sqlString = "SELECT NOTIFICATIONGROUPID,GROUPNAME FROM NOTIFICATIONGROUP " + 
			"WHERE NOTIFICATIONGROUPID > 0 ORDER BY NOTIFICATIONGROUPID";

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
			int notifGroupID = rset.getInt(1);
			String notifGroupName = rset.getString(2).trim();

			com.cannontech.database.data.lite.LiteNotificationGroup gr =
					new com.cannontech.database.data.lite.LiteNotificationGroup(notifGroupID, notifGroupName);
				
			allNotificationGroups.add(gr);
		}

		// WE MIGHT NEED TO ADD THE LOCATIONS IN THE GROUP HERE		
		sqlString = "SELECT n.notificationgroupid, r.recipientid, r.recipientname, r.emailaddress, r.emailsendtype, r.pagernumber " +
					"FROM NotificationRecipient r, NotificationDestination n WHERE n.notificationgroupID > 0 " +
					"AND n.recipientid=r.recipientid ORDER BY n.destinationorder";
		
		rset = stmt.executeQuery(sqlString);
		while (rset.next())
		{
			int notifGroupID = rset.getInt(1);
			int locationID = rset.getInt(2);
			String locationName = rset.getString(3).trim();
			String emailAddress = rset.getString(4).trim();
			int sendType = rset.getInt(5);
			String pagerNumber = rset.getString(6).trim();

			for(int i=0;i<allNotificationGroups.size();i++)
			{
				// load all the destinations here regardless of the group they belong to
				allUsedNotificationRecipients.add( new com.cannontech.database.data.lite.LiteNotificationRecipient( 
					locationID, locationName, emailAddress, sendType, pagerNumber ) );
				
				// load the destinations for the specific group
				if( ((com.cannontech.database.data.lite.LiteNotificationGroup)allNotificationGroups.get(i)).
					getNotificationGroupID() == notifGroupID )
				{
					((com.cannontech.database.data.lite.LiteNotificationGroup)allNotificationGroups.get(i)).
					getNotificationDestinationsList().add(
						new com.cannontech.database.data.lite.LiteNotificationRecipient( locationID, locationName, emailAddress, sendType, pagerNumber ) );
					break;
				}
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
      " Secs for NotificationGroupLoader (" + allNotificationGroups.size() + " loaded)" );
//temp code
	}
}
}
