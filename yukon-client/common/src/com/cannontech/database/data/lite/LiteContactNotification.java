package com.cannontech.database.data.lite;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.database.db.contact.ContactNotification;

/*
 */
public class LiteContactNotification extends LiteBase
{
	private int contactID = 0;
	private int notificationCategoryID = ContactNotificationType.HOME_PHONE.getDefinitionId(); /* Home Phone category */
	private String disableFlag = null;
	private String notification = null;
	
	private int order = 0;
	
	public LiteContactNotification() {
		super();
		setLiteType(LiteTypes.CONTACT_NOTIFICATION);
	}

	/**
	 * LiteContactNotification
	 */
	public LiteContactNotification( int contNotifID_ ) 
	{
		super();
		setContactNotifID( contNotifID_ );
		setLiteType( LiteTypes.CONTACT_NOTIFICATION );
	}
	/**
	 * LiteContactNotification
	 */
	public LiteContactNotification( int contNotifID_, int contID_, 
			int notifCatID_, String disableFlag_, String notification_ ) 
	{
		this( contNotifID_ );
		setContactID( contID_ );
		setNotificationCategoryID( notifCatID_ );
		setDisableFlag( disableFlag_ );
		setNotification( notification_ );
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 9:29:21 AM)
	 * @return int
	 */
	public int getContactNotifID() {
		return getLiteID();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 9:29:21 AM)
	 * @return int
	 */
	public int getContactID() {
		return contactID;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 9:29:21 AM)
	 * @param newContactID int
	 */
	public void setContactID(int newContactID) 
	{
		contactID = newContactID;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 9:29:21 AM)
	 * @param newContactID int
	 */
	public void setContactNotifID(int newID) 
	{
		setLiteID(newID);
	}
		
	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public String toString() {
		return getNotification();
	}

	/**
	 * retrieve method comment.
	 */
	public void retrieve(String databaseAlias) 
	{
	 	com.cannontech.database.SqlStatement stmt =
	 		new com.cannontech.database.SqlStatement(
				"SELECT c.NotificationCategoryID, c.DisableFlag, c.Notification " + 
		 		"FROM " + ContactNotification.TABLE_NAME + " c " +
		 		" WHERE c.ContactID = " + getContactID(),
		 		databaseAlias);
	
	 	try
	 	{
	 		stmt.execute();

			setNotificationCategoryID( ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue() );
			setDisableFlag( stmt.getRow(0)[1].toString() );
			setNotification( stmt.getRow(0)[2].toString() );
	 	}
	 	catch( Exception e )
	 	{
	 		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	 	}
	}

	/**
	 * Returns the disableFlag.
	 * @return String
	 */
	public String getDisableFlag() {
		return disableFlag;
	}
    
    /**
     * Returns the disableFlag.
     * @return boolean
     */
    public boolean isDisabled() {
        return "y".equalsIgnoreCase(getDisableFlag());
    }    

	/**
	 * Returns the notification.
	 * @return String
	 */
	public String getNotification() {
		return notification;
	}

	/**
	 * Returns the notificationCategoryID.
	 * @return int
	 */
	public int getNotificationCategoryID() {
		return notificationCategoryID;
	}

	/**
	 * Sets the disableFlag.
	 * @param disableFlag The disableFlag to set
	 */
	public void setDisableFlag(String disableFlag) {
		this.disableFlag = disableFlag;
	}

	/**
	 * Sets the notification.
	 * @param notification The notification to set
	 */
	public void setNotification(String notification) {
		this.notification = notification;
	}

	/**
	 * Sets the notificationCategoryID.
	 * @param notificationCategoryID The notificationCategoryID to set
	 */
	public void setNotificationCategoryID(int notificationCategoryID) {
		this.notificationCategoryID = notificationCategoryID;
	}

	public int getOrder() {
        return order;
    }
	
	public void setOrder(int order) {
        this.order = order;
    }
	
	
	public ContactNotificationType getContactNotificationType() {
		return ContactNotificationType.getTypeForNotificationCategoryId(this.getNotificationCategoryID());
	}
}
