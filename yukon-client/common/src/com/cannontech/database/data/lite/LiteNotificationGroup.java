package com.cannontech.database.data.lite;

import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.db.notification.NotificationDestination;

/*
 */
public class LiteNotificationGroup extends LiteBase
{
	private String notificationGroupName = null;
	
	//contains instances of com.cannontech.database.data.lite.LiteContactNotification
	private java.util.ArrayList notificationDestinations = null;

	/**
	 * LiteNotificationGroup
	 */
	public LiteNotificationGroup( int nID ) 
	{
		this( nID, "" );
	}

	/**
	 * LiteNotificationGroup
	 */
	public LiteNotificationGroup( int nID, String nName ) 
	{
		super();
		setNotificationGroupID(nID);
		notificationGroupName = new String(nName);
		setLiteType(LiteTypes.NOTIFICATION_GROUP);
	}

	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public java.util.ArrayList getNotificationDestinationsList() {
		if( notificationDestinations == null )
			notificationDestinations = new java.util.ArrayList(6);
		return notificationDestinations;
	}
	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public int getNotificationGroupID() {
		return getLiteID();
	}
	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public String getNotificationGroupName() {
		return notificationGroupName;
	}
	/**
	 * retrieve method comment.
	 */
	public void retrieve(String databaseAlias) {
	
	 	com.cannontech.database.SqlStatement stmt =
	 		new com.cannontech.database.SqlStatement(
					"SELECT GroupName FROM notificationGroup " + 
					"WHERE NotificationGroupID = " + 
					Integer.toString(getNotificationGroupID()),
					databaseAlias);
	
	 	try
	 	{
	 		stmt.execute();
			notificationGroupName = ((String) stmt.getRow(0)[0]);
	
			stmt = new com.cannontech.database.SqlStatement(
				"SELECT n.ContactNotifID, n.ContactID, n.NotificationCategoryID, " + 
				"n.DisableFlag, n.Notification " + 
		 		"FROM " + ContactNotification.TABLE_NAME + " n, " +
		 		NotificationDestination.TABLE_NAME + " nd " +
		 		"WHERE nd.notificationgroupID = " + getNotificationGroupID() +
		 		" and n.ContactNotifID = nd.RecipientID " +
		 		" ORDER BY nd.destinationorder",
				databaseAlias);
				
			stmt.execute();
	
			LiteContactNotification lcc = null;
			for(int i=0;i<stmt.getRowCount();i++)
			{
				lcc = new LiteContactNotification(
					((java.math.BigDecimal)stmt.getRow(i)[0]).intValue(),
					((java.math.BigDecimal)stmt.getRow(i)[1]).intValue(),
					((java.math.BigDecimal)stmt.getRow(i)[2]).intValue(),
					stmt.getRow(i)[3].toString(),
					stmt.getRow(i)[4].toString() );


				getNotificationDestinationsList().add(lcc);
			}
	 	}
	 	catch( Exception e )
	 	{
	 		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	 	}
	}
	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public void setNotificationDestinationsList(java.util.List newList) {
		this.notificationDestinations = new java.util.ArrayList(newList);
	}
	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public void setNotificationGroupID(int newValue) 
	{
		setLiteID(newValue);
	}
	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public void setNotificationGroupName(String newValue) {
		this.notificationGroupName = new String(newValue);
	}
	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public String toString() {
		return notificationGroupName;
	}
}
