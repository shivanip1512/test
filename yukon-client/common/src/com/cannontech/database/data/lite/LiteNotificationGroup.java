package com.cannontech.database.data.lite;

import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.database.db.notification.NotificationDestination;
import com.cannontech.database.db.notification.NotificationGroup;

/*
 */
public class LiteNotificationGroup extends LiteBase
{
	private String notificationGroupName = null;
	private String emailSubject = null;
	private String emailFrom = null;
	private String emailBody = null;
	private boolean disabled = false;
	
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
					"SELECT GroupName, EmailFromAddress, EmailMessage, " +
					"EmailSubject, DisableFlag " +
					"FROM " + NotificationGroup.TABLE_NAME + " " + 
					"WHERE NotificationGroupID = " + 
					Integer.toString(getNotificationGroupID()),
					databaseAlias);
	
	 	try
	 	{
	 		stmt.execute();
			setNotificationGroupName( (String)stmt.getRow(0)[0] );
			setEmailFrom( (String)stmt.getRow(0)[1] );
			setEmailBody( (String)stmt.getRow(0)[2] );
			setEmailSubject( (String)stmt.getRow(0)[3] );
			setDisabled( stmt.getRow(0)[4].toString().trim().equalsIgnoreCase("Y") );



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


				getNotificationDestinations().add(lcc);
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
	/**
	 * @return
	 */
	public boolean isDisabled()
	{
		return disabled;
	}

	/**
	 * @return
	 */
	public String getEmailFrom()
	{
		return emailFrom;
	}

	/**
	 * @return
	 */
	public String getEmailSubject()
	{
		return emailSubject;
	}

	/**
	 * @return
	 */
	public java.util.ArrayList getNotificationDestinations()
	{
		if( notificationDestinations == null )
			notificationDestinations = new java.util.ArrayList(8);
		return notificationDestinations;
	}

	/**
	 * @param b
	 */
	public void setDisabled(boolean b)
	{
		disabled = b;
	}

	/**
	 * @param string
	 */
	public void setEmailFrom(String string)
	{
		emailFrom = string;
	}

	/**
	 * @param string
	 */
	public void setEmailSubject(String string)
	{
		emailSubject = string;
	}

	/**
	 * @param list
	 */
	public void setNotificationDestinations(java.util.ArrayList list)
	{
		notificationDestinations = list;
	}

	/**
	 * @return
	 */
	public String getEmailBody()
	{
		return emailBody;
	}

	/**
	 * @param string
	 */
	public void setEmailBody(String string)
	{
		emailBody = string;
	}

}
