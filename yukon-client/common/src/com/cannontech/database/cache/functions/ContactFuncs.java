package com.cannontech.database.cache.functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
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
			return (LiteContact)cache.getAContactByContactID(contactID_);
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

		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized( cache )
		{
			return cache.getContactsByFirstName(firstName_, false);
		}
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

        DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized( cache )
        {
            return cache.getContactsByLastName(lastName_, partialMatch);
        }
	}
    
	/**
     * Returns an array of contactIDS having lastName as partialMatch (true) or exact match (partialMatch=false).
     * If lastName contains a comma, then the text following the comma is used for FIRSTNAME PARTIAL match 
     * @param lastName_
     * @param partialMatch
     * @return
	 */
	public static int[] retrieveContactIDsByLastName(String lastName_, boolean partialMatch)
    {
        int [] contactIDs = null;
        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        
        String lastName = lastName_.trim();
        String firstName = null;
        int commaIndex = lastName_.indexOf(",");
        if( commaIndex > 0 )
        {
            firstName = lastName_.substring(commaIndex+1).trim();
            lastName = lastName_.substring(0, commaIndex -1).trim();
        }
            
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            String sql = "SELECT CONTACTID " + 
                        " FROM " + Contact.TABLE_NAME + 
                        " WHERE UPPER(CONTLASTNAME) LIKE ?";
                        if (firstName != null && firstName.length() > 0)
                            sql += " AND UPPER(CONTFIRSTNAME) LIKE ? ";
            
            pstmt = conn.prepareStatement( sql );
            pstmt.setString( 1, lastName.toUpperCase()+(partialMatch? "%":""));
            if (firstName != null && firstName.length() > 0)
                pstmt.setString(2, firstName.toUpperCase() + "%");
            rset = pstmt.executeQuery();
    
            ArrayList<Integer> contactIDList = new ArrayList<Integer>();
            while(rset.next())
            {
                contactIDList.add(new Integer(rset.getInt(1)));
            }
            contactIDs = new int[contactIDList.size()];
            for (int i = 0; i < contactIDList.size(); i++)
                contactIDs[i] = contactIDList.get(i).intValue();
        }
        catch( Exception e ){
            CTILogger.error( "Error retrieving contacts with last name " + lastName+ ": " + e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) rset.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            }
            catch (java.sql.SQLException e) {}
        }
        return contactIDs;
    }
	
	public static LiteContact[] getContactsByLName( String lastName_ ) {
		return getContactsByLName( lastName_, false );
	}
	
	/**
	 * @param phoneNo
	 * @param phoneNotifCatIDs Array of notification category IDs, currently the options are:
	 * YukonListEntryTypes.YUK_ENTRY_ID_PHONE, YUK_ENTRY_ID_HOME_PHONE, YUK_ENTRY_ID_WORK_PHONE,
	 * @param partialMatch If true, phoneNo is to be matched partially from the last digit
	 * @return Array of LiteContact for phoneNo
	 */
	public static LiteContact[] getContactsByPhoneNo(String phoneNo, int[] phoneNotifCatIDs, boolean partialMatch) {
		if (phoneNo == null) return null;
		
		//java.util.Arrays.sort( phoneNotifCatIDs );
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		
		synchronized (cache) 
        {
			return cache.getContactsByPhoneNumber(phoneNo, partialMatch);
            
            /*
             * For now we won't worry about verifying notification categories, so if somebody put
             * a phone number in for their email or pager, they will find it here too.  Not worth the 
             * performance hit at Xcel right now.
             */
            /*LiteContact[] theCandidates = cache.getContactsByPhoneNumber(phoneNo, partialMatch);
			LiteContact[] theWinners = new LiteContact[theCandidates.length];
            
            //need to double check that these are actually of phone number type since the cache
            //function does not verify notification category.
			for (int i = 0; i < theCandidates.length; i++) 
            {
				for (int j = 0; j < theCandidates[i].getLiteContactNotifications().size(); j++) {
					LiteContactNotification lNotif = (LiteContactNotification)
							theCandidates[i].getLiteContactNotifications().get(j);
					
					if (java.util.Arrays.binarySearch(phoneNotifCatIDs, lNotif.getNotificationCategoryID()) < 0)
					{
						
					}
				}
			}
		}
		
		LiteContact[] contacts = new LiteContact[ contList.size() ];
		contList.toArray( contacts );
		return contacts;*/
        }
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
			return cache.getContactsByEmail(email_);
		}
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
		return ContactNotificationFuncs.getAllContactNotifications();
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
		synchronized (cache) 
        {
			return cache.getACustomerByContactID(contactID);
		}
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