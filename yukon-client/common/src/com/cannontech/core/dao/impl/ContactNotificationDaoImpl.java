package com.cannontech.core.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Functions for the LiteContactNotification data in cache
 * 
 */
public final class ContactNotificationDaoImpl implements ContactNotificationDao 
{
    private IDatabaseCache databaseCache;
    
	public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }

    /**
	 * ContactFuncs constructor comment.
	 */
	private ContactNotificationDaoImpl() 
	{
		super();
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactNotificationDao#getContactNotification(int)
     */
	public LiteContactNotification getContactNotification( int contactNotifID ) 
	{
		synchronized( databaseCache )
		{
			return (LiteContactNotification)
					databaseCache.getAContactNotifByNotifID(contactNotifID);
		}
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactNotificationDao#getAllContactNotifications()
     */
	public List getAllContactNotifications() 
	{
		List cntNotifs = new ArrayList(64);
		synchronized( databaseCache )		
		{
			Iterator it = databaseCache.getAllContactNotifsMap().values().iterator();
			
			while( it.hasNext() )
			{
				cntNotifs.add( it.next() );
			}
		}

		return cntNotifs;
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactNotificationDao#getContactNotificationsParent(int)
     */
	public LiteContact getContactNotificationsParent( int notifCatID ) 
	{
		synchronized( databaseCache )
		{
			List cstCnts = databaseCache.getAllContacts();
			
			for( int i = 0; i < cstCnts.size(); i++ )
			{
				LiteContact ltCntact = (LiteContact)cstCnts.get(i);
				for( int j = 0; j < ltCntact.getLiteContactNotifications().size(); j++ )
				{
					LiteContactNotification ltNotif = 
						(LiteContactNotification)ltCntact.getLiteContactNotifications().get(j);

					if( notifCatID == ltNotif.getContactNotifID() )
						return (LiteContact)cstCnts.get(i);
				}
			}
		}
	
		return null;
	}

}