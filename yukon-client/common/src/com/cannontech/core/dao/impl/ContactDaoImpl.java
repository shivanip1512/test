package com.cannontech.core.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class ContactDaoImpl implements ContactDao 
{
    private YukonListDao yukonListDao;
    private ContactNotificationDao contactNotifcationDao;
    private YukonUserDao yukonUserDao;
    private IDatabaseCache databaseCache;
    
	/**
	 * ContactFuncs constructor comment.
	 */
	private ContactDaoImpl() 
	{
		super();
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getContact(int)
     */
	public LiteContact getContact( int contactID_ ) 
	{
		synchronized( databaseCache )
		{
			return (LiteContact)databaseCache.getAContactByContactID(contactID_);
		}
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getContactNotification(com.cannontech.database.data.lite.LiteContact, int)
     */
	public LiteContactNotification getContactNotification(LiteContact liteContact, int notifCatID)
	{
		for (int i = 0; i < liteContact.getLiteContactNotifications().size(); i++) {
			LiteContactNotification liteNotif = (LiteContactNotification) liteContact.getLiteContactNotifications().get(i);
			
			if (liteNotif.getNotificationCategoryID() == notifCatID)
				return liteNotif;
		}
		
		return null;
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getContactsByFName(java.lang.String)
     */
	public LiteContact[] getContactsByFName( String firstName_ ) 
	{
		if( firstName_ == null )
			return null;

		synchronized( databaseCache )
		{
			return databaseCache.getContactsByFirstName(firstName_, false);
		}
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getContactsByLName(java.lang.String, boolean)
     */
	public LiteContact[] getContactsByLName( String lastName_, boolean partialMatch ) 
	{
		if( lastName_ == null )
			return null;

        synchronized( databaseCache )
        {
            return databaseCache.getContactsByLastName(lastName_, partialMatch);
        }
	}
    
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#retrieveContactIDsByLastName(java.lang.String, boolean)
     */
	public int[] retrieveContactIDsByLastName(String lastName_, boolean partialMatch)
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
            lastName = lastName_.substring(0, commaIndex).trim();
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
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getContactsByLName(java.lang.String)
     */
	public LiteContact[] getContactsByLName( String lastName_ ) {
		return getContactsByLName( lastName_, false );
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getContactsByPhoneNo(java.lang.String, int[], boolean)
     */
	public LiteContact[] getContactsByPhoneNo(String phoneNo, int[] phoneNotifCatIDs, boolean partialMatch) {
		if (phoneNo == null) return null;
		
		//java.util.Arrays.sort( phoneNotifCatIDs );
		synchronized (databaseCache) 
        {
			return databaseCache.getContactsByPhoneNumber(phoneNo, partialMatch);
            
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
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getContactsByPhoneNo(java.lang.String, int[])
     */
	public LiteContact[] getContactsByPhoneNo(String phoneNo, int[] phoneNotifCatIDs) {
		return getContactsByPhoneNo( phoneNo, phoneNotifCatIDs, false );
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getContactByEmailNotif(java.lang.String)
     */
	public LiteContact getContactByEmailNotif( String email_ ) 
	{
		if( email_ == null )
			return null;

		synchronized( databaseCache )
		{
			return databaseCache.getContactsByEmail(email_);
		}
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getUnassignedContacts()
     */
	public LiteContact[] getUnassignedContacts() {

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

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getAllEmailAddresses(int)
     */
	public String[] getAllEmailAddresses( int contactID_ )
	{
		LiteContact contact = getContact( contactID_ );
		ArrayList<String> strList = new ArrayList<String>(16);

		//find all the email addresses in the list ContactNotifications
		for( int j = 0; j < contact.getLiteContactNotifications().size(); j++  )
		{	
			LiteContactNotification ltCntNotif = 
					contact.getLiteContactNotifications().get(j);
					
			if( ltCntNotif.getNotificationCategoryID() == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL )
			{
				strList.add( ltCntNotif.getNotification() );
			}
		}

		return strList.toArray( new String[strList.size()] );
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getAllPINNotifDestinations(int)
     */
	public LiteContactNotification[] getAllPINNotifDestinations( int contactID_ )
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

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getAllPhonesNumbers(int)
     */
    public LiteContactNotification[] getAllPhonesNumbers( int contactID_ )
    {
        LiteContact contact = getContact( contactID_ );
        ArrayList phoneList = new ArrayList(16);

        //find all the phone numbers in the list ContactNotifications
        for( int j = 0; j < contact.getLiteContactNotifications().size(); j++  )
        {   
            LiteContactNotification ltCntNotif = 
                    (LiteContactNotification)contact.getLiteContactNotifications().get(j);
                
            if( yukonListDao.isPhoneNumber(ltCntNotif.getNotificationCategoryID()) )
            {
                phoneList.add( ltCntNotif );
            }
        }

        LiteContactNotification[] phones = new LiteContactNotification[ phoneList.size() ];
        return (LiteContactNotification[])phoneList.toArray( phones );
    }
    
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getAllContactNotifications()
     */
	public List<LiteContactNotification> getAllContactNotifications() 
	{
		return contactNotifcationDao.getAllContactNotifications();
	}


	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getOwnerCICustomer(int)
     */
	public LiteCICustomer getOwnerCICustomer( int addtlContactID_ ) 
	{
		synchronized(databaseCache)	 
		{
			Iterator iter = databaseCache.getAllCICustomers().iterator();
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
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getPrimaryContactCICustomer(int)
     */
	public LiteCICustomer getPrimaryContactCICustomer( int primaryContactID_ ) 
	{
		synchronized(databaseCache)	 
		{
			Iterator iter = databaseCache.getAllCICustomers().iterator();
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

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getCICustomer(int)
     */
	public LiteCICustomer getCICustomer (int contactID_)
	{
		LiteCICustomer liteCICust = getPrimaryContactCICustomer(contactID_);
		if( liteCICust == null)
			liteCICust = getOwnerCICustomer(contactID_);
			
		return liteCICust;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getCustomer(int)
     */
	public LiteCustomer getCustomer(int contactID) {
		LiteCICustomer liteCICust = getCICustomer( contactID );
		if (liteCICust != null) return liteCICust;
		
		synchronized (databaseCache) 
        {
			return databaseCache.getACustomerByContactID(contactID);
		}
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getYukonUser(int)
     */
	public LiteYukonUser getYukonUser(int contactID_) 
	{
		synchronized(databaseCache) 
		{
            LiteContact lc = getContact( contactID_ );
            
            if( lc != null )
                return yukonUserDao.getLiteYukonUser(lc.getLoginID());            
		}

		return null;
	}	
    
    /* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#hasPin(int)
     */
    public boolean hasPin(int contactId) {
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

    public void setContactNotifcationDao(
            ContactNotificationDao contactNotifcationDao) {
        this.contactNotifcationDao = contactNotifcationDao;
    }

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }

    public void setYukonListDao(YukonListDao yukonListDao) {
        this.yukonListDao = yukonListDao;
    }

    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
}