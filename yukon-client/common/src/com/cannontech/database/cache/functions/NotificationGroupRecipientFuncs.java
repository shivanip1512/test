package com.cannontech.database.cache.functions;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class NotificationGroupRecipientFuncs {
/**
 * PointFuncs constructor comment.
 */
private NotificationGroupRecipientFuncs() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/26/2001 9:41:59 AM)
 * @return com.cannontech.database.data.lite.LitePoint
 * @param pointID int
 */
public static com.cannontech.database.data.lite.LiteNotificationRecipient getLiteNotificationRecipient(int locationID) 
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List recipients = cache.getAllNotificationRecipients();
		
		for( int j = 0; j < recipients.size(); j++ )
		{
			if( locationID == ((com.cannontech.database.data.lite.LiteNotificationRecipient)recipients.get(j)).getRecipientID() )
				return (com.cannontech.database.data.lite.LiteNotificationRecipient)recipients.get(j);
		}
	}

	return null;
}
}
