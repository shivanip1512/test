package com.cannontech.database.cache.functions;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class CustomerContactFuncs 
{
	/**
	 * CustomerContactFuncs constructor comment.
	 */
	private CustomerContactFuncs() 
	{
		super();
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (3/26/2001 9:41:59 AM)
	 * @return com.cannontech.database.data.lite.LitePoint
	 * @param pointID int
	 */
	public static LiteContact getCustomerContact( int contactID_ ) 
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized( cache )
		{
			List cstCnts = cache.getAllContacts();
			
			for( int j = 0; j < cstCnts.size(); j++ )
			{
				if( contactID_ == ((LiteContact)cstCnts.get(j)).getContactID() )
					return (LiteContact)cstCnts.get(j);
			}
		}
	
		return null;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (3/26/2001 9:41:59 AM)
	 * @param pointID int
	 * 
	 * @returns LiteContactNotifications
	 */
	public static List getAllContactNotifications() 
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache = 
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

		ArrayList cntNotifs = null;
		
		synchronized( cache )		
		{
			List cstCnts = cache.getAllContacts();
			cntNotifs = new ArrayList( cstCnts.size() );
			
			for( int j = 0; j < cstCnts.size(); j++ )
			{
				LiteContact contact = (LiteContact)cstCnts.get(j);
				
				cntNotifs.addAll( contact.getLiteContactNotifications() );
			}
		}
	
		return cntNotifs;
	}

}
