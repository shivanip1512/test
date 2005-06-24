package com.cannontech.database.cache.functions;

import java.sql.SQLException;
import java.util.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.contact.Contact;

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
			return (LiteContact)cache.getAllContactsMap().get( new Integer(contactID_) );
		}
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
	 * @param partialMatch If true, lastName_ is to be matched partially from the first character
	 */
	public static LiteContact[] getContactsByLName( String lastName_, boolean partialMatch ) 
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
				String contLastName = ((LiteContact)cstCnts.get(j)).getContLastName();
				if( contLastName.equalsIgnoreCase( lastName_ )
					|| partialMatch && contLastName.toUpperCase().startsWith( lastName_.toUpperCase() ))
					notifs.add( cstCnts.get(j) );
			}
		}
	
		LiteContact[] cArr = new LiteContact[ notifs.size() ];
		return (LiteContact[])notifs.toArray( cArr );
	}
	
	public static LiteContact[] getContactsByLName( String lastName_ ) {
		return getContactsByLName( lastName_, false );
	}
	
	/**
	 * @param phoneNo
	 * @param phoneNotifCatIDs Array of notification category IDs, currently the options are:
	 * YukonListEntryTypes.YUK_ENTRY_ID_PHONE, YUK_ENTRY_ID_HOME_PHONE, YUK_ENTRY_ID_WORK_PHONE
	 * @param partialMatch If true, phoneNo is to be matched partially from the last digit
	 * @return Array of LiteContact for phoneNo
	 */
	public static LiteContact[] getContactsByPhoneNo(String phoneNo, int[] phoneNotifCatIDs, boolean partialMatch) {
		if (phoneNo == null) return null;
		
		java.util.Arrays.sort( phoneNotifCatIDs );
		ArrayList contList = new ArrayList();
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized (cache) {
			List allConts = cache.getAllContacts();
			
			for (int i = 0; i < allConts.size(); i++) {
				LiteContact lCont = (LiteContact) allConts.get(i);
				for (int j = 0; j < lCont.getLiteContactNotifications().size(); j++) {
					LiteContactNotification lNotif = (LiteContactNotification)
							lCont.getLiteContactNotifications().get(j);
					
					if (java.util.Arrays.binarySearch(phoneNotifCatIDs, lNotif.getNotificationCategoryID()) >= 0)
					{
						if (lNotif.getNotification().equalsIgnoreCase(phoneNo)
							|| partialMatch && lNotif.getNotification().endsWith(phoneNo))
						{
							contList.add( lCont );
							break;
						}
					}
				}
			}
		}
		
		LiteContact[] contacts = new LiteContact[ contList.size() ];
		contList.toArray( contacts );
		return contacts;
	}
	
	public static LiteContact[] getContactsByPhoneNo(String phoneNo, int[] phoneNotifCatIDs) {
		return getContactsByPhoneNo( phoneNo, phoneNotifCatIDs, false );
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
	 * Returns all the LiteContacts that do not belong to a Customer.
	 * 
	 */
	public static LiteContact[] getUnassignedContacts() {

		int[] contIDs = new int[0];
		try {
			contIDs = Contact.getOrphanedContacts();
		} catch( SQLException se ) {
			CTILogger.error( "Unable to get the Unassigned Contacts from the database", se );
		}

		ArrayList contList = new ArrayList(32);

		for( int i = 0; i < contIDs.length; i++ ) {
			contList.add( getContact(contIDs[i]) );
		}

		return (LiteContact[])contList.toArray( new LiteContact[contList.size()] );
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
	 * Returns all the PIN entries found for a given contact. If no
	 * PIN entries are found, we return a zero length String array.
	 * 
	 */
	public static LiteContactNotification[] getAllPINNotifDestinations( int contactID_ )
	{
		LiteContact contact = getContact( contactID_ );
		ArrayList strList = new ArrayList(16);

		//find all the PINs in the list ContactNotifications
		for( int j = 0; j < contact.getLiteContactNotifications().size(); j++  )
		{	
			LiteContactNotification ltCntNotif =
					(LiteContactNotification)contact.getLiteContactNotifications().get(j);
					
			if( ltCntNotif.getNotificationCategoryID() == YukonListEntryTypes.YUK_ENTRY_ID_PIN )
				strList.add( ltCntNotif );
		}

		LiteContactNotification[] pins = new LiteContactNotification[ strList.size() ];
		return (LiteContactNotification[])strList.toArray( pins );
	}

    /**
     * Finds all notifcations that are of a phone type. Returns a zero length array
     * when no phone numbers are found.
     * 
     * @param contact
     * @return int
     */
    public static LiteContactNotification[] getAllPhonesNumbers( int contactID_ )
    {
        LiteContact contact = getContact( contactID_ );
        ArrayList phoneList = new ArrayList(16);

        //find all the phone numbers in the list ContactNotifications
        for( int j = 0; j < contact.getLiteContactNotifications().size(); j++  )
        {   
            LiteContactNotification ltCntNotif = 
                    (LiteContactNotification)contact.getLiteContactNotifications().get(j);
                
            if( YukonListFuncs.isPhoneNumber(ltCntNotif.getNotificationCategoryID()) )
            {
                phoneList.add( ltCntNotif );
            }
        }

        LiteContactNotification[] phones = new LiteContactNotification[ phoneList.size() ];
        return (LiteContactNotification[])phoneList.toArray( phones );
    }
    
	/**
	 * Returns all contactNotifications.
	 * @return List LiteContactNotifications
	 */
	public static List getAllContactNotifications() 
	{
		return ContactNotifcationFuncs.getAllContactNotifications();
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
		
		return null;
	}
	
	public static LiteYukonUser getYukonUser(int contactID_) 
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized(cache) 
		{
            LiteContact lc = getContact( contactID_ );
            
            if( lc != null )
                return YukonUserFuncs.getLiteYukonUser(lc.getLoginID());            
		}

		return null;
	}	
    
    public static boolean hasPin(int contactId) {
        LiteContact contact = getContact( contactId );
        
        List liteContactNotifications = contact.getLiteContactNotifications();
        for (Iterator iter = liteContactNotifications.iterator(); iter.hasNext();) {
            LiteContactNotification contactNotification = (LiteContactNotification) iter.next();
            if (contactNotification.getNotificationCategoryID() == YukonListEntryTypes.YUK_ENTRY_ID_PIN) {
                return true;
            }
        }
        return false;
    }
    
}