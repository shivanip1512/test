package com.cannontech.database.cache.functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;

/**
 * Functions for the LiteContactNotification data in cache
 * 
 */
public final class ContactNotifcationFuncs 
{
	/**
	 * ContactFuncs constructor comment.
	 */
	private ContactNotifcationFuncs() 
	{
		super();
	}

	/**
	 * Returns the LiteContactNotification for contactNotifID.
	 * 
	 */
	public static LiteContactNotification getContactNotification( int contactNotifID ) 
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized( cache )
		{
			return (LiteContactNotification)
					cache.getAllContactNotifsMap().get( new Integer(contactNotifID) );
		}
	}

	/**
	 * Returns all contactNotifications.
	 *
	 */
	public static List getAllContactNotifications() 
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		List cntNotifs = new ArrayList(64);
		synchronized( cache )		
		{
			Iterator it = cache.getAllContactNotifsMap().values().iterator();
			
			while( it.hasNext() )
			{
				cntNotifs.add( it.next() );
			}
		}

		return cntNotifs;
	}

	/**
	 * Returns the parent Contact for this ContactNotifcation
	 *
	 */
	public static LiteContact getContactNotificationsParent( int notifCatID ) 
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized( cache )
		{
			List cstCnts = cache.getAllContacts();
			
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