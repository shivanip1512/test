package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.notification.NotifDestinationMap;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author:
 */
public final class NotificationGroupDaoImpl implements NotificationGroupDao 
{
    private IDatabaseCache databaseCache;
  
    /**
	 * NotificationGroupFuncs constructor comment.
	 */
	public NotificationGroupDaoImpl() 
	{
		super();
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.NotificationGroupDao#getNotifEmailsByLiteGroup(com.cannontech.database.data.lite.LiteNotificationGroup)
     */
	public String[] getNotifEmailsByLiteGroup( LiteNotificationGroup lGrp_ )
	{
		ArrayList emailList = new ArrayList(8);

		synchronized( databaseCache )
		{		
			for( int j = 0; j < lGrp_.getNotifDestinationMap().length; j++ )
			{
				NotifDestinationMap notifDest = lGrp_.getNotifDestinationMap()[j];
				
				LiteContactNotification lcn = (LiteContactNotification)
					databaseCache.getAllContactNotifsMap().get(
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


	/* (non-Javadoc)
     * @see com.cannontech.core.dao.NotificationGroupDao#getLiteNotificationGroup(int)
     */
	public LiteNotificationGroup getLiteNotificationGroup( int groupID_ )
	{
		synchronized( databaseCache )
		{		
			for( int i = 0; i < databaseCache.getAllContactNotificationGroupsWithNone().size(); i++ )
			{
				LiteNotificationGroup lGrp =
						(LiteNotificationGroup)databaseCache.getAllContactNotificationGroups().get(i);

				if( groupID_ == lGrp.getNotificationGroupID() )
					return lGrp;
			}
		}
		
		return null;
	}
    
    public Set<LiteNotificationGroup> getAllNotificationGroups() {
        List<LiteNotificationGroup> allContactNotificationGroups = 
            databaseCache.getAllContactNotificationGroups();
        HashSet<LiteNotificationGroup> hashSet = 
            new HashSet<LiteNotificationGroup>(allContactNotificationGroups);
        return hashSet;
    }
    
    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
}