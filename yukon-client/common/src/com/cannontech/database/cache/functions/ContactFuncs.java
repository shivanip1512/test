package com.cannontech.database.cache.functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
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
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
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
	 * Returns the LiteContactNotification of the specified category
	 * @param liteContact com.cannontech.database.data.lite.LiteContact
	 * @param notifCatID int
	 * @return com.cannontech.database.data.lite.LiteContactNotification
	 */
	public static LiteContactNotification getContactNotification(LiteContact liteContact, int notifCatID)
	{
		for (int i = 0; i < liteContact.getLiteContactNotifications().size(); i++) {
			LiteContactNotification liteNotif = (LiteContactNotification) liteContact.getLiteContactNotifications().get(i);
			
			if (liteNotif.getNotificationCategoryID() == notifCatID)
				return liteNotif;
		}
		
		return null;
	}

	/**
	 * Returns the LiteContact for firstName_.
	 * @return com.cannontech.database.data.lite.LiteContact
	 * @param contactID_ int
	 */
	public static LiteContact[] getContactsByFName( String firstName_ ) 
	{
		if( firstName_ == null )
			return null;

		ArrayList notifs = new ArrayList( 16 );
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized( cache )
		{
			List cstCnts = cache.getAllContacts();
			
			for( int j = 0; j < cstCnts.size(); j++ )
			{
				if( firstName_.equalsIgnoreCase( ((LiteContact)cstCnts.get(j)).getContFirstName() ) )
					notifs.add( cstCnts.get(j) );
			}
		}
	
		LiteContact[] cArr = new LiteContact[ notifs.size() ];
		return (LiteContact[])notifs.toArray( cArr );
	}
	
	/**
	 * Returns the LiteContact for lastName_.
	 * @return com.cannontech.database.data.lite.LiteContact
	 * @param contactID_ int
	 */
	public static LiteContact[] getContactsByLName( String lastName_ ) 
	{
		if( lastName_ == null )
			return null;

		ArrayList notifs = new ArrayList( 16 );
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized( cache )
		{
			List cstCnts = cache.getAllContacts();
			
			for( int j = 0; j < cstCnts.size(); j++ )
			{
				if( lastName_.equalsIgnoreCase( ((LiteContact)cstCnts.get(j)).getContLastName() ) )
						notifs.add( cstCnts.get(j) );
			}
		}
	
		LiteContact[] cArr = new LiteContact[ notifs.size() ];
		return (LiteContact[])notifs.toArray( cArr );
	}

	/**
	 * Returns the LiteContact for email_.
	 * @return com.cannontech.database.data.lite.LiteContact
	 * @param contactID_ int
	 */
	public static LiteContact getContactByEmailNotif( String email_ ) 
	{
		if( email_ == null )
			return null;


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

					if( ltNotif.getNotificationCategoryID() == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL
						 && email_.equalsIgnoreCase(ltNotif.getNotification()) )
						return (LiteContact)cstCnts.get(i);
				}
			}
		}
	
		return null;
	}

	/**
	 * Looks the first email notificatoin type in the list passed in. Returns a zero length string
	 * when no emails are found.
	 * @param contact
	 * @return int
	 */
	public static String[] getAllEmailAddresses( int contactID_ )
	{
		LiteContact contact = getContact( contactID_ );
		ArrayList strList = new ArrayList(16);

		//find all the email addresses in the list ContactNotifications
		for( int j = 0; j < contact.getLiteContactNotifications().size(); j++  )
		{	
			LiteContactNotification ltCntNotif = 
					(LiteContactNotification)contact.getLiteContactNotifications().get(j);
					
			if( ltCntNotif.getNotificationCategoryID() == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL )
			{
				strList.add( ltCntNotif.getNotification() );
			}
		}

		String[] emails = new String[ strList.size() ];
		return (String[])strList.toArray( emails );
	}

	/**
	 * Returns all contactNotifications.
	 * @return List LiteContactNotifications
	 */
	public static List getAllContactNotifications() 
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
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
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	
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
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	
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
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();

		LiteCICustomer liteCICust = getPrimaryContactCICustomer(contactID_);
		if( liteCICust == null)
			liteCICust = getOwnerCICustomer(contactID_);
			
		return liteCICust;
	}
	
	/**
	 * Returns the LiteCustomer for contact ID
	 * @param contactID int
	 * @return LiteCustomer
	 */
	public static LiteCustomer getCustomer(int contactID) {
		LiteCICustomer liteCICust = getCICustomer( contactID );
		if (liteCICust != null) return liteCICust;
		
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized (cache) {
			List customers = cache.getAllCustomers();
			for (int i = 0; i < customers.size(); i++) {
				LiteCustomer liteCustomer = (LiteCustomer) customers.get(i);
				if (liteCustomer.getPrimaryContactID() == contactID)
					return liteCustomer;
			}
		}
		
		try {
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
					"SELECT CustomerID FROM Customer WHERE PrimaryContactID=" + contactID,
					CtiUtilities.getDatabaseAlias() );
			stmt.execute();
			
			if (stmt.getRowCount() != 1)
				throw new Exception( "Cannot determine the residential customer with PrimaryContactID=" + contactID );
			
			return cache.getCustomer( ((java.math.BigDecimal) stmt.getRow(0)[0]).intValue() );
		}
		catch (Exception e) {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		
		return null;
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