package com.cannontech.database.cache.functions;

import java.util.ArrayList;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.notification.NotifDestinationMap;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author:
 */
public final class NotificationGroupFuncs 
{
	/**
	 * NotificationGroupFuncs constructor comment.
	 */
	private NotificationGroupFuncs() 
	{
		super();
	}


	public static String[] getNotifEmailsByLiteGroup( LiteNotificationGroup lGrp_ )
	{
		DefaultDatabaseCache c = new DefaultDatabaseCache();
		ArrayList emailList = new ArrayList(8);

		synchronized( c )
		{		
			for( int j = 0; j < lGrp_.getNotifDestinationMap().length; j++ )
			{
				NotifDestinationMap notifDest = lGrp_.getNotifDestinationMap()[j];
				
				LiteContactNotification lcn = (LiteContactNotification)
					c.getAllContactNotifsMap().get(
						new Integer(notifDest.getRecipientID()) );

				//we have an email address, add it to our list
				if( lcn.getNotificationCategoryID() == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL )
					emailList.add( lcn.getNotification() );
			}
			
		}
		
		String[] retVals = new String[ emailList.size() ];
		if( !emailList.isEmpty() )
			retVals = (String[])emailList.toArray( retVals );
		
		return retVals;
	}


	public static LiteNotificationGroup getLiteNotificationGroup( int groupID_ )
	{
		DefaultDatabaseCache c = new DefaultDatabaseCache();

		synchronized( c )
		{		
			for( int i = 0; i < c.getAllContactNotificationGroups().size(); i++ )
			{
				LiteNotificationGroup lGrp =
						(LiteNotificationGroup)c.getAllContactNotificationGroups().get(i);

				if( groupID_ == lGrp.getNotificationGroupID() )
					return lGrp;
			}
		}
		
		return null;
	}
}