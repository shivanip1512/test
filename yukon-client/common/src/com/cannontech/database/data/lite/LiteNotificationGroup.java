package com.cannontech.database.data.lite;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
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

	//contains ints of ContactIDs
	private int[] contactIDs = new int[0];

	//contains ints of CustomerIDs
	private int[] customerIDs = new int[0];


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
	public void retrieve(String databaseAlias)
	{
	 	String notifSQL = 
			"SELECT GroupName, EmailFromAddress, EmailMessage, " +
			"EmailSubject, DisableFlag " +
			"FROM " + NotificationGroup.TABLE_NAME + " " + 
			"WHERE NotificationGroupID = " + 
			Integer.toString(getNotificationGroupID());

		String contNotifSQL =
			"SELECT n.ContactNotifID, n.ContactID, n.NotificationCategoryID, " + 
			"n.DisableFlag, n.Notification " + 
			"FROM " + ContactNotification.TABLE_NAME + " n, " +
			NotificationDestination.TABLE_NAME + " nd " +
			"WHERE nd.notificationgroupID = " + getNotificationGroupID() +
			" and n.ContactNotifID = nd.RecipientID " +
			" ORDER BY nd.destinationorder";

		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
	
		try
		{
			conn = PoolManager.getInstance().getConnection( databaseAlias );
			stmt = conn.createStatement();
			rset = stmt.executeQuery(notifSQL);

			while( rset.next() )
			{
				setNotificationGroupName( rset.getString(1).trim() );
				setEmailFrom( rset.getString(2).trim() );
				setEmailBody( rset.getString(3).trim() );
				setEmailSubject( rset.getString(4).trim() );
				setDisabled( rset.getString(5).trim().charAt(0) == CtiUtilities.trueChar.charValue() );
			}
			
			rset = stmt.executeQuery(contNotifSQL);
			LiteContactNotification lcc = null;
			while( rset.next() )
			{
				lcc = new LiteContactNotification(
					rset.getInt(1),
					rset.getInt(2),
					rset.getInt(3),
					rset.getString(4).trim(),
					rset.getString(5).trim() );


				getNotificationDestinations().add(lcc);
			}

			setContactIDs(
				com.cannontech.database.data.notification.NotificationGroup.getAllNotifGroupContactIDs(
					new Integer(getNotificationGroupID()),
					conn) );

			setCustomerIDs(
				com.cannontech.database.data.notification.NotificationGroup.getAllNotifGroupCustomerIDs(
					new Integer(getNotificationGroupID()),
					conn) );
		}
		catch( java.sql.SQLException e ) {
			try { ///close all the stuff here
				if( stmt != null ) stmt.close();
				if( conn != null ) conn.close();
            
				stmt = null;
				conn = null;
			}
			catch( java.sql.SQLException ex ) {
				CTILogger.error( ex.getMessage(), ex);
			}
		}
		finally {
			try {
				if( stmt != null ) stmt.close();
				if( conn != null ) conn.close();
			}
			catch( java.sql.SQLException e ) {
				CTILogger.error( e.getMessage(), e );
			}

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

	/**
	 * @return
	 */
	public int[] getContactIDs()
	{
		return contactIDs;
	}

	/**
	 * @return
	 */
	public int[] getCustomerIDs()
	{
		return customerIDs;
	}

	/**
	 * @param is
	 */
	public void setContactIDs(int[] is)
	{
		contactIDs = is;
	}

	/**
	 * @param is
	 */
	public void setCustomerIDs(int[] is)
	{
		customerIDs = is;
	}

}
