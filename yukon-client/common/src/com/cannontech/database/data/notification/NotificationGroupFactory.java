package com.cannontech.database.data.notification;

/**
 * This type was created in VisualAge.
 */
public final class NotificationGroupFactory {
/**
 * This method was created in VisualAge.
 */
public final static GroupNotification createGroupNotification() 
{
	return createGroupNotification( com.cannontech.database.db.notification.NotificationGroup.getNextNotificationGroupID() );
}
/**
 * This method was created in VisualAge.
 */
public final static GroupNotification createGroupNotification( Integer notificationGroupID ) 
{ 
	return new GroupNotification( notificationGroupID );
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.data.state.GroupState[]
 */
public final static GroupNotification[] getAllGroupNotifications() throws java.sql.SQLException 
{
	com.cannontech.database.db.notification.NotificationGroup notificationGroups[]  =  com.cannontech.database.db.notification.NotificationGroup.getNotificationGroups();

	java.util.Vector allGroupsV = new java.util.Vector();
	
	for( int i = 0; i < notificationGroups.length; i++ )
	{
		GroupNotification groupNotification = getGroupNotification( notificationGroups[i].getNotificationGroupID() );
		allGroupsV.addElement( groupNotification);
	}

	GroupNotification allGroupNotificaions[] = new GroupNotification[allGroupsV.size()];

	allGroupsV.copyInto(allGroupNotificaions);

	return allGroupNotificaions;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.data.route.RouteBase
 * @param name java.lang.String
 */
public final static GroupNotification getGroupNotification(Integer notificationGroupID)
{
	GroupNotification returnGroupNotif = null; 

	returnGroupNotif = createGroupNotification( notificationGroupID );
	try
	{
		com.cannontech.database.Transaction t = com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, returnGroupNotif);
		t.execute();
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	
	return returnGroupNotif;
}
}
