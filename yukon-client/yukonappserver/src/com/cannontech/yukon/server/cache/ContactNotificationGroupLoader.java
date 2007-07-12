package com.cannontech.yukon.server.cache;

import java.util.List;

import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.db.notification.NotificationGroup;
import com.cannontech.database.db.point.PointAlarming;

/**
 * Insert the type's description here.
 * Creation date: (3/15/00 3:57:58 PM)
 * @author: 
 */
public class ContactNotificationGroupLoader implements Runnable 
{
	private List<LiteNotificationGroup> allContactNotificationGroups = null;
	//private java.util.ArrayList allUsedContactNotifications = new java.util.ArrayList();
	
	private String databaseAlias = null;

	/**
	 * ContactNotificationLoader constructor comment.
	 */
	public ContactNotificationGroupLoader(List<LiteNotificationGroup> contactGroupArray_, String alias) {
		super();
		this.allContactNotificationGroups = contactGroupArray_;
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
		String sqlString = 
				"SELECT NotificationGroupID, GroupName, DisableFlag " +
				"FROM " + NotificationGroup.TABLE_NAME + " " + 
				"WHERE NotificationGroupID > " + PointAlarming.NONE_NOTIFICATIONID + 
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

				lGrp.setDisabled( rset.getString(3).trim().equalsIgnoreCase("Y") );


				lGrp.setNotifDestinationMap(
					com.cannontech.database.data.notification.NotificationGroup.getAllNotifGroupDestinations(
						new Integer(lGrp.getNotificationGroupID()), conn) );

				lGrp.setContactMap(
					com.cannontech.database.data.notification.NotificationGroup.getAllNotifGroupContacts(
						new Integer(lGrp.getNotificationGroupID()), conn) );

				lGrp.setCustomerMap(
					com.cannontech.database.data.notification.NotificationGroup.getAllNotifGroupCustomers(
						new Integer(lGrp.getNotificationGroupID()), conn) );

				allContactNotificationGroups.add( lGrp );
			}

			
		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			SqlUtils.close(rset, stmt, conn );
	//temp code
	timerStop = new java.util.Date();
	com.cannontech.clientutils.CTILogger.info( 
	    (timerStop.getTime() - timerStart.getTime())*.001 + 
	      " Secs for ContactNotificationGroupLoader (" + allContactNotificationGroups.size() + " loaded)" );
	//temp code
		}
	}
}
