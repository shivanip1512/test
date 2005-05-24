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
				"SELECT NotificationGroupID, GroupName, EmailFromAddress, EmailMessage, " +
				"EmailSubject, DisableFlag " +
				"FROM " + NotificationGroup.TABLE_NAME + " " + 
				"WHERE NotificationGroupID > 0 " + 
				"ORDER BY GroupName";
	
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
				LiteNotificationGroup lGrp =
						new LiteNotificationGroup( rset.getInt(1), rset.getString(2).trim() );

				lGrp.setEmailFrom( rset.getString(3).trim() );
				lGrp.setEmailBody( rset.getString(4).trim() );
				lGrp.setEmailSubject( rset.getString(5).trim() );
				lGrp.setDisabled( rset.getString(6).trim().equalsIgnoreCase("Y") );

				lGrp.setContactIDs(
					com.cannontech.database.data.notification.NotificationGroup.getAllNotifGroupContactIDs(
						new Integer(lGrp.getNotificationGroupID()),
						conn) );

				lGrp.setCustomerIDs(
					com.cannontech.database.data.notification.NotificationGroup.getAllNotifGroupCustomerIDs(
						new Integer(lGrp.getNotificationGroupID()),
						conn) );

				allContactNotificationGroups.add( lGrp );
			}

	
			// Add all the contacts
			sqlString = 
				"SELECT n.ContactNotifID, n.ContactID, n.NotificationCategoryID, n.DisableFlag, " + 
				"n.Notification, nd.NotificationgroupID FROM " + 
		 		ContactNotification.TABLE_NAME + " n, " +
		 		NotificationDestination.TABLE_NAME + " nd " +
		 		"WHERE nd.notificationgroupID > " + CtiUtilities.NONE_ZERO_ID + " " +
		 		"and n.ContactNotifID = nd.RecipientID " +
		 		"ORDER BY nd.destinationorder";

			
			rset = stmt.executeQuery(sqlString);
			while( rset.next() )
			{
	
				for( int i = 0; i < allContactNotificationGroups.size(); i++ )
				{
					LiteNotificationGroup lGrp = 
						(LiteNotificationGroup)allContactNotificationGroups.get(i);
					
					LiteContactNotification ln = new LiteContactNotification(
							rset.getInt(1), 
							rset.getInt(2), 
							rset.getInt(3),
							rset.getString(4),
							rset.getString(5) );
							
					// load the destinations for the specific group					
					if( lGrp.getNotificationGroupID() == rset.getInt(6) )
					{
						lGrp.getNotificationDestinations().add(ln);
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
	      " Secs for ContactNotificationGroupLoader (" + allContactNotificationGroups.size() + " loaded)" );
	//temp code
		}
	}
}
