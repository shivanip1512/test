package com.cannontech.database.data.lite;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.notification.ContactNotifGroupMap;
import com.cannontech.database.data.notification.CustomerNotifGroupMap;
import com.cannontech.database.data.notification.NotifDestinationMap;
import com.cannontech.database.db.notification.NotificationGroup;

/*
 */
public class LiteNotificationGroup extends LiteBase
{
	private String notificationGroupName = null;
	private boolean disabled = false;
	
	//contains instances of com.cannontech.database.data.lite.LiteContactNotification
	//private java.util.ArrayList notificationDestinations = null;

	private NotifDestinationMap[] notifDestinationMap = new NotifDestinationMap[0];
	private ContactNotifGroupMap[] contactMap = new ContactNotifGroupMap[0];
	private CustomerNotifGroupMap[] customerMap = new CustomerNotifGroupMap[0];


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
			"SELECT GroupName, DisableFlag " +
			"FROM " + NotificationGroup.TABLE_NAME + " " + 
			"WHERE NotificationGroupID = " + 
			Integer.toString(getNotificationGroupID());

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
				setDisabled( rset.getString(2).trim().charAt(0) == CtiUtilities.trueChar.charValue() );
			}

			setNotifDestinationMap(
				com.cannontech.database.data.notification.NotificationGroup.getAllNotifGroupDestinations(
					new Integer(getNotificationGroupID()), conn) );

			setContactMap(
				com.cannontech.database.data.notification.NotificationGroup.getAllNotifGroupContacts(
					new Integer(getNotificationGroupID()), conn) );

			setCustomerMap(
				com.cannontech.database.data.notification.NotificationGroup.getAllNotifGroupCustomers(
					new Integer(getNotificationGroupID()), conn) );

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
//	public void setNotificationDestinationsList(java.util.List newList) {
//		this.notificationDestinations = new java.util.ArrayList(newList);
//	}
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
	 * @param b
	 */
	public void setDisabled(boolean b)
	{
		disabled = b;
	}

	/**
	 * @return
	 */
	public ContactNotifGroupMap[] getContactMap()
	{
		return contactMap;
	}

	/**
	 * @return
	 */
	public CustomerNotifGroupMap[] getCustomerMap()
	{
		return customerMap;
	}

	/**
	 * @param maps
	 */
	public void setContactMap(ContactNotifGroupMap[] maps)
	{
		contactMap = maps;
	}

	/**
	 * @param maps
	 */
	public void setCustomerMap(CustomerNotifGroupMap[] maps)
	{
		customerMap = maps;
	}

	/**
	 * @return
	 */
	public NotifDestinationMap[] getNotifDestinationMap()
	{
		return notifDestinationMap;
	}

	/**
	 * @param maps
	 */
	public void setNotifDestinationMap(NotifDestinationMap[] maps)
	{
		notifDestinationMap = maps;
	}

}
