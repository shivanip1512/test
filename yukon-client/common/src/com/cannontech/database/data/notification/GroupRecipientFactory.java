package com.cannontech.database.data.notification;

/**
 * This type was created in VisualAge.
 */
public final class GroupRecipientFactory {
/**
 * This method was created in VisualAge.
 */
public final static NotificationRecipient createNotificationRecipient() 
{
	return createNotificationRecipient( com.cannontech.database.db.notification.NotificationRecipient.getNextLocationID() );
}
/**
 * This method was created in VisualAge.
 */
public final static NotificationRecipient createNotificationRecipient( Integer locationID ) 
{ 
	return new NotificationRecipient( locationID );
}
/**
 * This method was created in VisualAge.
 * @return NotificationRecipient[]
 */
public final static NotificationRecipient[] getAllNotificationRecipients() throws java.sql.SQLException 
{
	com.cannontech.database.db.notification.NotificationRecipient notificationRecipients[]  =  com.cannontech.database.db.notification.NotificationRecipient.getAllNotificationGroupRecipients();

	java.util.Vector allGroupsV = new java.util.Vector();
	
	for( int i = 0; i < notificationRecipients.length; i++ )
	{
		NotificationRecipient notifRecipient = getNotificationRecipient( notificationRecipients[i].getRecipientID() );
		allGroupsV.addElement( notifRecipient );
	}

	NotificationRecipient allNotifRecipients[] = new NotificationRecipient[allGroupsV.size()];

	allGroupsV.copyInto(allNotifRecipients);

	return allNotifRecipients;
}
/**
 * This method was created in VisualAge.
 * @return NotificationRecipient
 * @param Integer
 */
public final static NotificationRecipient getNotificationRecipient(Integer locationID)
{
	NotificationRecipient returnRecipient = null; 

	returnRecipient = createNotificationRecipient( locationID );

	try
	{
		com.cannontech.database.Transaction t = com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, returnRecipient);
		returnRecipient = (NotificationRecipient)t.execute();
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	
	return returnRecipient;
}
}
