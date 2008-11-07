package com.cannontech.core.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dynamic.impl.AsyncDynamicDataSourceImpl;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.customer.Customer;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class ContactDaoImpl implements ContactDao {
    private YukonListDao yukonListDao;
    private ContactNotificationDao contactNotificationDao;
    private YukonUserDao yukonUserDao;
    private IDatabaseCache databaseCache;

    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private final ParameterizedRowMapper<LiteContact> rowMapper = new LiteContactRowMapper();

    private DBPersistentDao dbPersistantDao;
    private AsyncDynamicDataSourceImpl asyncDynamicDataSource; 
    
	/**
	 * ContactFuncs constructor comment.
	 */
	private ContactDaoImpl() 
	{
		super();
	}

	@Transactional
    public LiteContact getContact(int contactId) {

        StringBuilder sql = new StringBuilder("SELECT *");
        sql.append(" FROM Contact");
        sql.append(" WHERE ContactId = ?");

        LiteContact contact = simpleJdbcTemplate.queryForObject(sql.toString(),
                                                                rowMapper,
                                                                contactId);
        return contact;

    }
    
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Map<Integer, LiteContact> getContacts(List<Integer> contactIds) {
        ChunkingSqlTemplate<Integer> template = new ChunkingSqlTemplate<Integer>(simpleJdbcTemplate);

        final List<LiteContact> contactList = template.query(new SqlGenerator<Integer>() {
            @Override
            public String generate(List<Integer> subList) {
                SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
                sqlBuilder.append("SELECT * FROM Contact WHERE ContactID IN (");
                sqlBuilder.append(subList);
                sqlBuilder.append(")");
                String sql = sqlBuilder.toString();
                return sql;
            }
        }, contactIds, rowMapper);
        
        final Map<Integer, LiteContact> resultMap = new HashMap<Integer, LiteContact>(contactList.size());
        for (final LiteContact contact : contactList) {
            Integer key = contact.getContactID();
            resultMap.put(key, contact);
        }
        
        return resultMap;
    }

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getContactNotification(com.cannontech.database.data.lite.LiteContact, int)
     */
	public LiteContactNotification getContactNotification(LiteContact liteContact, int notifCatID)
	{
		for (int i = 0; i < liteContact.getLiteContactNotifications().size(); i++) {
			LiteContactNotification liteNotif = liteContact.getLiteContactNotifications().get(i);
			
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

		List<LiteContact> contList = new ArrayList<LiteContact>(32);

		for( int i = 0; i < contIDs.length; i++ ) {
			contList.add( getContact(contIDs[i]) );
		}

		return contList.toArray( new LiteContact[contList.size()] );
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
		List<LiteContactNotification> strList = new ArrayList<LiteContactNotification>(16);

		//find all the PINs in the list ContactNotifications
		for( int j = 0; j < contact.getLiteContactNotifications().size(); j++  )
		{	
			LiteContactNotification ltCntNotif =
					contact.getLiteContactNotifications().get(j);
					
			if( ltCntNotif.getNotificationCategoryID() == YukonListEntryTypes.YUK_ENTRY_ID_PIN )
				strList.add( ltCntNotif );
		}

		LiteContactNotification[] pins = new LiteContactNotification[ strList.size() ];
		return strList.toArray( pins );
	}

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getAllPhonesNumbers(int)
     */
    public LiteContactNotification[] getAllPhonesNumbers( int contactID_ )
    {
        LiteContact contact = getContact( contactID_ );
        List<LiteContactNotification> phoneList = new ArrayList<LiteContactNotification>(16);

        //find all the phone numbers in the list ContactNotifications
        for( int j = 0; j < contact.getLiteContactNotifications().size(); j++  )
        {   
            LiteContactNotification ltCntNotif = 
                    contact.getLiteContactNotifications().get(j);
                
            if( yukonListDao.isPhoneNumber(ltCntNotif.getNotificationCategoryID()) )
            {
                phoneList.add( ltCntNotif );
            }
        }

        LiteContactNotification[] phones = new LiteContactNotification[ phoneList.size() ];
        return phoneList.toArray( phones );
    }
    
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getAllContactNotifications()
     */
	public List<LiteContactNotification> getAllContactNotifications() 
	{
		return contactNotificationDao.getAllContactNotifications();
	}


	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getOwnerCICustomer(int)
     */
	public LiteCICustomer getOwnerCICustomer( int addtlContactID_ ) 
	{
		synchronized(databaseCache)	 
		{
			Iterator<LiteCICustomer> iter = databaseCache.getAllCICustomers().iterator();
			while( iter.hasNext() ) 
			{
				LiteCICustomer cst = iter.next();
				for( int i = 0; i < cst.getAdditionalContacts().size(); i++ )
				{
					if( cst.getAdditionalContacts().get(i).getContactID() == addtlContactID_ )
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
			Iterator<LiteCICustomer> iter = databaseCache.getAllCICustomers().iterator();
			while( iter.hasNext() ) 
			{
				LiteCICustomer cst = iter.next();
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
     * @see com.cannontech.core.dao.ContactDao#isPrimaryContact(int)
     */
	public boolean isPrimaryContact(int contactId) {
	    String sql = "SELECT PrimaryContactId FROM " + Customer.TABLE_NAME + " WHERE PrimaryContactId = " + contactId;
	    try {
	        simpleJdbcTemplate.queryForInt(sql);
	        return true;
	    } catch(IncorrectResultSizeDataAccessException e) {
	        return false;
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
        
        List<LiteContactNotification> liteContactNotifications = contact.getLiteContactNotifications();
        for (Iterator<LiteContactNotification> iter = liteContactNotifications.iterator(); iter.hasNext();) {
            LiteContactNotification contactNotification = iter.next();
            if (contactNotification.getNotificationCategoryID() == YukonListEntryTypes.YUK_ENTRY_ID_PIN) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public LiteContact getPrimaryContactForAccount(int accountId) {

        StringBuilder sql = new StringBuilder("SELECT c.*");
        sql.append(" FROM Contact c, Customer cu, CustomerAccount ca");
        sql.append(" WHERE c.ContactId = cu.PrimaryContactId");
        sql.append(" AND cu.CustomerId = ca.CustomerId");
        sql.append(" AND ca.AccountId = ?");

        LiteContact contact = simpleJdbcTemplate.queryForObject(sql.toString(),
                                                                rowMapper,
                                                                accountId);
        return contact;
    }
    

    @Override
    @Transactional
    public List<LiteContact> getAdditionalContactsForCustomer(int customerId) {
        
        StringBuilder sql = new StringBuilder("SELECT c.*");
        sql.append(" FROM Contact c, CustomerAdditionalContact cac");
        sql.append(" WHERE c.ContactId = cac.ContactId");
        sql.append(" AND cac.CustomerId = ?");
        sql.append(" ORDER BY cac.Ordering");

        List<LiteContact> contactList = simpleJdbcTemplate.query(sql.toString(),
                                                                 rowMapper,
                                                                 customerId);

        return contactList;
    }

    @Override
    @Transactional
    public List<LiteContact> getAdditionalContactsForAccount(int accountId) {

        StringBuilder sql = new StringBuilder("SELECT c.*");
        sql.append(" FROM Contact c, CustomerAdditionalContact cac, Customer cu, CustomerAccount ca");
        sql.append(" WHERE c.ContactId = cac.ContactId");
        sql.append(" AND cac.CustomerId = cu.CustomerId");
        sql.append(" AND cu.CustomerId = ca.CustomerId");
        sql.append(" AND ca.AccountId = ?");
        sql.append(" ORDER BY cac.Ordering");

        List<LiteContact> contactList = simpleJdbcTemplate.query(sql.toString(),
                                                                 rowMapper,
                                                                 accountId);

        return contactList;
    }

    @Override
    @Transactional
    public void saveContact(LiteContact contact) {

        int contactId = contact.getContactID();
        int changeType;
        
        StringBuilder sql = new StringBuilder();
        if (contactId == -1) {
            // Insert if id is -1
            changeType = DBChangeMsg.CHANGE_TYPE_ADD;
            
            contactId = nextValueHelper.getNextValue("Contact");
            contact.setContactID(contactId);

            sql.append("INSERT INTO Contact");
            sql.append(" (ContFirstName, ContLastName, LogInId, AddressId, ContactId)");
            sql.append(" VALUES (?,?,?,?,?)");
        } else {
            // Update if id is not -1
            changeType = DBChangeMsg.CHANGE_TYPE_UPDATE;

            sql.append("UPDATE Contact");
            sql.append(" SET ContFirstName = ?, ContLastName = ?, LogInId = ?, AddressId = ?");
            sql.append(" WHERE ContactId = ?");

        }

        String firstName = SqlUtils.convertStringToDbValue(contact.getContFirstName());
        String lastName = SqlUtils.convertStringToDbValue(contact.getContLastName());
        int loginId = contact.getLoginID();
        int addressId = contact.getAddressID();
        simpleJdbcTemplate.update(sql.toString(),
                                  firstName,
                                  lastName,
                                  loginId,
                                  addressId,
                                  contactId);

        contactNotificationDao.saveNotificationsForContact(contactId,
                                                           contact.getLiteContactNotifications());
        
        DBChangeMsg changeMsg = new DBChangeMsg(contactId,
                                                DBChangeMsg.CHANGE_CONTACT_DB,
                                                DBChangeMsg.CAT_CUSTOMERCONTACT,
                                                DBChangeMsg.CAT_CUSTOMERCONTACT,
                                                changeType);
        
        dbPersistantDao.processDBChange(changeMsg);
        asyncDynamicDataSource.handleDBChange(changeMsg);

    }
    
    @Override
    @Transactional
    public void removeAdditionalContact(int contactId) {

        // Remove all notifications
        contactNotificationDao.removeNotificationsForContact(contactId);
        
        StringBuilder additionalSql = new StringBuilder("DELETE FROM CustomerAdditionalContact");
        additionalSql.append(" WHERE ContactId = ?");
        simpleJdbcTemplate.update(additionalSql.toString(), contactId);

        StringBuilder sql = new StringBuilder("DELETE FROM Contact");
        sql.append(" WHERE ContactId = ?");
        simpleJdbcTemplate.update(sql.toString(), contactId);
        
        DBChangeMsg changeMsg = new DBChangeMsg(contactId,
                                                DBChangeMsg.CHANGE_CONTACT_DB,
                                                DBChangeMsg.CAT_CUSTOMERCONTACT,
                                                DBChangeMsg.CAT_CUSTOMERCONTACT,
                                                DBChangeMsg.CHANGE_TYPE_DELETE);
        
        dbPersistantDao.processDBChange(changeMsg);
        asyncDynamicDataSource.handleDBChange(changeMsg);
    }
    
    @Override
    public void deleteAllAdditionalContactsToCustomerReferences(Integer customerId) {
        String sql = "DELETE FROM CustomerAdditionalContact WHERE CustomerID = ?";
        simpleJdbcTemplate.update(sql, customerId);
    }
    
    @Override
    @Transactional
    public void deleteAllAdditionalContactsForCustomer(Integer customerId) {
        List<LiteContact> contacts = getAdditionalContactsForCustomer(customerId);
        for(LiteContact contact : contacts) {
            deleteContact(contact.getContactID());
        }
    }
    
    @Override
    @Transactional
    public void deleteContact(Integer contactId) {
        Integer addressId = getContact(contactId).getAddressID();
        String deleteNotificationDestinations = "DELETE FROM NotificationDestination " + 
            "WHERE RecipientID in (SELECT ContactNotifID " +
            "FROM ContactNotification WHERE ContactID = ?)";
        simpleJdbcTemplate.update(deleteNotificationDestinations, contactId);
        String deleteContactNotification = "DELETE FROM ContactNotification WHERE ContactId = ?"; 
        simpleJdbcTemplate.update(deleteContactNotification, contactId);
        String deleteContactNotifGroupMap = "DELETE FROM ContactNotifGroupMap WHERE ContactId = ?";
        simpleJdbcTemplate.update(deleteContactNotifGroupMap, contactId);
        removeAdditionalContact(contactId);
        String delete = "DELETE FROM Contact WHERE ContactId = ?";
        simpleJdbcTemplate.update(delete, contactId);
        if(addressId != CtiUtilities.NONE_ZERO_ID) {
            String deleteAddress = "DELETE FROM Address WHERE AddressId = ?";
            simpleJdbcTemplate.update(deleteAddress, addressId);
        }
    }
    
    @Override
    @Transactional
    public void addAdditionalContact(LiteContact contact, LiteCustomer customer) {
        
        this.saveContact(contact);
        
        StringBuilder sql = new StringBuilder("INSERT INTO CustomerAdditionalContact");
        sql.append(" (CustomerId, ContactId, Ordering)");
        sql.append(" VALUES (?,?,?)");
        
        int customerId = customer.getCustomerID();
        int contactId = contact.getContactID();

        StringBuilder orderSql = new StringBuilder("SELECT MAX(Ordering) + 1 FROM CustomerAdditionalContact WHERE CustomerId = ?");
        int order = simpleJdbcTemplate.queryForInt(orderSql.toString(), customerId);
        
        simpleJdbcTemplate.update(sql.toString(), customerId, contactId, order);
        
        DBChangeMsg changeMsg = new DBChangeMsg(contactId,
                                                DBChangeMsg.CHANGE_CONTACT_DB,
                                                DBChangeMsg.CAT_CUSTOMERCONTACT,
                                                DBChangeMsg.CAT_CUSTOMERCONTACT,
                                                DBChangeMsg.CHANGE_TYPE_UPDATE);
        
        dbPersistantDao.processDBChange(changeMsg);
        asyncDynamicDataSource.handleDBChange(changeMsg);
        
    }

    /**
     * Helper class to map a result set into LiteContacts
     * 
     * Note: this mapper MUST be used within a transaction
     */
    private class LiteContactRowMapper implements
            ParameterizedRowMapper<LiteContact> {

        @Override
        public LiteContact mapRow(ResultSet rs, int rowNum) throws SQLException {

            int id = rs.getInt("ContactId");
            int loginId = rs.getInt("LoginId");
            int addressId = rs.getInt("AddressId");
            String firstName = SqlUtils.convertDbValueToString(rs.getString("ContFirstName"));
            String lastName = SqlUtils.convertDbValueToString(rs.getString("ContLastName"));

            LiteContact contact = new LiteContact(id);
            contact.setLoginID(loginId);
            contact.setAddressID(addressId);
            contact.setContFirstName(firstName);
            contact.setContLastName(lastName);

            List<LiteContactNotification> notifications = contactNotificationDao
					.getNotificationsForContact(id);
			contact.setNotifications(notifications);
            
            return contact;
        }

    }

    public void setContactNotificationDao(
            ContactNotificationDao contactNotificationDao) {
        this.contactNotificationDao = contactNotificationDao;
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

    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    public void setDbPersistantDao(DBPersistentDao dbPersistantDao) {
        this.dbPersistantDao = dbPersistantDao;
    }
    
    public void setAsyncDynamicDataSource(
            AsyncDynamicDataSourceImpl asyncDynamicDataSource) {
        this.asyncDynamicDataSource = asyncDynamicDataSource;
    }

}