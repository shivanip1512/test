package com.cannontech.database.cache.functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class ContactFuncs 
{
	/**
	 * ContactFuncs constructor comment.
	 */
	private ContactFuncs() 
	{
		super();
	}

	/**
	 * Returns the LiteContact for contactID_.
	 * @return com.cannontech.database.data.lite.LiteContact
	 * @param contactID_ int
	 */
	public static LiteContact getContact( int contactID_ ) 
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
	 * Returns all contactNotifications.
	 * @return List LiteContactNotifications
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


	/**
	 * Returns the LiteCICustomer for addltContactID_.
	 * @param addtlContact_ int
	 * @return LiteCICustomer
	 */
	public static LiteCICustomer getOwnerCICustomer( int addtlContactID_ ) 
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache = 
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	
		synchronized(cache)	 
		{
			Iterator iter = cache.getAllCICustomers().iterator();
			while( iter.hasNext() ) 
			{
				LiteCICustomer cst = (LiteCICustomer) iter.next();
				for( int i = 0; i < cst.getAdditionalContacts().size(); i++ )
				{
					if( ((LiteContact)cst.getAdditionalContacts().get(i)).getContactID() == addtlContactID_ )
					{
						return cst;
					}
				}
			}		
		}
			
		//no owner CICustomer...strange
		return null;
	}
	
	/**
	 * Returns the LiteCICustomer for primaryContactID_.
	 * @param primaryContact_ int
	 * @return LiteCICustomer
	 */
	public static LiteCICustomer getPrimaryContactCICustomer( int primaryContactID_ ) 
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache = 
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	
		synchronized(cache)	 
		{
			Iterator iter = cache.getAllCICustomers().iterator();
			while( iter.hasNext() ) 
			{
				LiteCICustomer cst = (LiteCICustomer) iter.next();
				if( cst.getPrimaryContactID() == primaryContactID_)
					return cst;
			}		
		}
			
		//no owner CICustomer...strange
		return null;
	}

	/**
	 * Returns the LiteCICustomer for contactID_.
	 * If contactID_ not the primaryContactID, then check the CustomerAdditionalContact(s).
	 * @param contact_ int
	 * @return LiteCICustomer
	 */
	public static LiteCICustomer getCICustomer (int contactID_)
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache =
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

		LiteCICustomer liteCICust = getPrimaryContactCICustomer(contactID_);
		if( liteCICust == null)
			liteCICust = getOwnerCICustomer(contactID_);
			
		return liteCICust;
	}
	
	public static LiteYukonUser getYukonUser(int contactID_) 
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized(cache) 
		{
			Iterator iter = cache.getAllContacts().iterator();
			while(iter.hasNext())
			{
				LiteContact contact = (LiteContact) iter.next();
				if(contact.getContactID() == contactID_)
				{
					LiteYukonUser liteYukonUser = YukonUserFuncs.getLiteYukonUser(contact.getLoginID());
					return liteYukonUser;
				}
			}		
		}
		return null;
	}	
}