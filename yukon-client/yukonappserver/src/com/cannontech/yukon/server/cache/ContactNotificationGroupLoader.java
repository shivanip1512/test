package com.cannontech.yukon.server.cache;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.db.notification.NotificationDestination;
import com.cannontech.database.db.notification.NotificationGroup;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class ContactNotificationGroupLoader implements Runnable 
{
	private java.util.ArrayList allContactNotificationGroups = null;
	//private java.util.ArrayList allUsedContactNotifications = new java.util.ArrayList();
	
	private String databaseAlias = null;

	/**
	 * ContactNotificationLoader constructor comment.
	 */
	public ContactNotificationGroupLoader(java.util.ArrayList contactGroupArray_, String alias) {
		super();
		this.allContactNotificationGroups = contactGroupArray_;
		this.databaseAlias = alias;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (11/21/00 9:14:40 AM)
	 * @return java.util.List
	 */
/*	public java.util.ArrayList getAllUsedContactNotifications() 
	{
		return allUsedContactNotifications;
	}
*/


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
		String sqlString = 
				"SELECT NOTIFICATIONGROUPID,GROUPNAME FROM " + 
				NotificationGroup.TABLE_NAME + " " + 
				"WHERE NOTIFICATIONGROUPID > 0 " + 
				"ORDER BY NOTIFICATIONGROUPID";
	
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
						new com.cannontech.database.data.lite.LiteNotificationGroup(
							notifGroupID, notifGroupName);
					
				allContactNotificationGroups.add(gr);
			}

/*	
			// WE MIGHT NEED TO ADD THE LOCATIONS IN THE GROUP HERE		
			sqlString = 
				"SELECT n.ContactNotifID, n.ContactID, n.NotificationCategoryID, n.DisableFlag, " + 
				"n.Notification, nd.NotificationID FROM " + 
		 		ContactNotification.TABLE_NAME + " n, " +
		 		NotificationDestination.TABLE_NAME + " nd " +
		 		"WHERE nd.notificationgroupID > " + CtiUtilities.NONE_ID + " " +
		 		"and n.ContactID = nd.RecipientID " +
		 		"ORDER BY nd.destinationorder";
					
			
			rset = stmt.executeQuery(sqlString);
			while (rset.next())
			{
	
				for( int i = 0; i < allContactNotificationGroups.size(); i++ )
				{
					LiteContactNotification ln = new LiteContactNotification(
							rset.getInt(1), 
							rset.getInt(2), 
							rset.getInt(3),
							rset.getString(4),
							rset.getString(5) );
							
					int notifGroupID = rset.getInt(2);
					
					
					// load all the destinations here regardless of the group 
					// they belong to
					allUsedContactNotifications.add( ln ); 
					
					// load the destinations for the specific group
					if( ((LiteNotificationGroup)allContactNotificationGroups.get(i)).
						getNotificationGroupID() == notifGroupID )
					{
						((LiteNotificationGroup)allContactNotificationGroups.get(i)).
							getNotificationDestinationsList().add(ln);
	
						break;
					}
				}
			}
*/
			
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
	      " Secs for ContactNotificationGroupLoader (" + allContactNotificationGroups.size() + " loaded)" );
	//temp code
		}
	}
}
