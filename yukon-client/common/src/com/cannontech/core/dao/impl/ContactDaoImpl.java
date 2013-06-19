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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.PoolManager;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.customer.Customer;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;

public final class ContactDaoImpl implements ContactDao {
    
    @Autowired private AddressDao addressDao;
    @Autowired private YukonListDao yukonListDao;
    @Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DbChangeManager dbChangeManager;
    
    private final YukonRowMapper<LiteContact> rowMapper = new LiteContactRowMapper();

    @Override
    public LiteContact getContact(int contactId) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT ContactId, ContFirstName, ContLastName, LogInID, AddressID");
        sql.append("FROM Contact");
        sql.append("WHERE ContactId").eq(contactId);

        LiteContact contact = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        return contact;
    }
	
	@Override
    public List<LiteContact> getContactsByLoginId(int loginId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ContactId, ContFirstName, ContLastName, LogInID, AddressID");
        sql.append("FROM Contact");
        sql.append("WHERE LoginId").eq(loginId);
        
	    final List<LiteContact> contactList = yukonJdbcTemplate.query(sql, rowMapper);
        return contactList;
    }
    
    @Override
    public Map<Integer, LiteContact> getContacts(List<Integer> contactIds) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);

        final List<LiteContact> contactList = template.query(new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
                sqlBuilder.append("SELECT ContactId, ContFirstName, ContLastName, LogInID, AddressID");
                sqlBuilder.append("FROM Contact");
                sqlBuilder.append("WHERE ContactID").in(subList);
                return sqlBuilder;
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
     * @see com.cannontech.core.dao.ContactDao#getContactsByPhoneNo(java.lang.String, int[], boolean)
     */
	@Override
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
	@Override
    public LiteContact[] getContactsByPhoneNo(String phoneNo, int[] phoneNotifCatIDs) {
		return getContactsByPhoneNo( phoneNo, phoneNotifCatIDs, false );
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getContactByEmailNotif(java.lang.String)
     */
	@Override
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
	@Override
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
	@Override
    public String[] getAllEmailAddresses( int contactId ) {
		LiteContact contact = getContact( contactId );
		List<String> strList = new ArrayList<String>();

		//find all the email addresses in the list ContactNotifications
		for( LiteContactNotification liteContactNotification : contactNotificationDao.getNotificationsForContactByType(contact, ContactNotificationType.EMAIL)) {	
		    strList.add( liteContactNotification.getNotification() );
		}

		return strList.toArray( new String[strList.size()] );
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getAllPINNotifDestinations(int)
     */
	@Override
    public LiteContactNotification[] getAllPINNotifDestinations( int contactId )
	{
		LiteContact contact = getContact( contactId );
		List<LiteContactNotification> notificationsForContactByType = contactNotificationDao.getNotificationsForContactByType(contact, ContactNotificationType.VOICE_PIN);
		return notificationsForContactByType.toArray( new LiteContactNotification[notificationsForContactByType.size()] );
	}

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getAllPhonesNumbers(int)
     */
    @Override
    public LiteContactNotification[] getAllPhonesNumbers( int contactID_ )
    {
        LiteContact contact = getContact( contactID_ );
        List<LiteContactNotification> phoneList = new ArrayList<LiteContactNotification>(16);

        //find all the phone numbers in the list ContactNotifications
        for(LiteContactNotification ltCntNotif : contactNotificationDao.getNotificationsForContact(contact)) {   
                
            if (yukonListDao.isPhoneNumber(ltCntNotif.getNotificationCategoryID())) {
                phoneList.add( ltCntNotif );
            }
        }

        LiteContactNotification[] phones = new LiteContactNotification[ phoneList.size() ];
        return phoneList.toArray( phones );
    }
    
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getAllContactNotifications()
     */
	@Override
    public List<LiteContactNotification> getAllContactNotifications() 
	{
		return contactNotificationDao.getAllContactNotifications();
	}


	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getOwnerCICustomer(int)
     */
	@Override
    public LiteCICustomer getOwnerCICustomer( int addtlContactID_ ) 
	{
		int customerId = -1;
        String sql = "SELECT c.CustomerID " 
            + " FROM CustomerAdditionalContact ca, Customer c " 
            + " WHERE c.CustomerTypeID = " + CustomerTypes.CUSTOMER_CI + " " 
            + " AND c.CustomerID = ca.CustomerID " 
            + " AND ca.ContactID = ?";
        try {
            customerId = yukonJdbcTemplate.queryForInt(sql, addtlContactID_);
        } catch (EmptyResultDataAccessException e) {
            // will return null customer
        }

        LiteCICustomer liteCICust = null;
        if (customerId >= 0) {
            LiteCustomer liteCust = databaseCache.getACustomerByCustomerID(customerId);
            if (liteCust != null && liteCust instanceof LiteCICustomer) {
                liteCICust = (LiteCICustomer) liteCust;
            }
        }
        return liteCICust;
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getPrimaryContactCICustomer(int)
     */
	@Override
    public LiteCICustomer getPrimaryContactCICustomer( int primaryContactID_ ) 
	{
	    LiteCICustomer liteCICust = null;
        LiteCustomer liteCust = databaseCache.getACustomerByPrimaryContactID(primaryContactID_);
        if (liteCust != null && liteCust instanceof LiteCICustomer) {
            liteCICust = (LiteCICustomer) liteCust;
        }
        return liteCICust;
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getCICustomer(int)
     */
	@Override
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
	@Override
    public LiteCustomer getCustomer(int contactID) {
	    LiteCustomer liteCust = databaseCache.getACustomerByPrimaryContactID(contactID);
        if( liteCust == null) {
            liteCust = getOwnerCICustomer(contactID);
        }
		return liteCust;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#isPrimaryContact(int)
     */
	@Override
    public boolean isPrimaryContact(int contactId) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT PrimaryContactId");
		sql.append("FROM Customer");
		sql.append("WHERE PrimaryContactId").eq(contactId);;
	    try {
	    	yukonJdbcTemplate.queryForInt(sql);
	        return true;
	    } catch(IncorrectResultSizeDataAccessException e) {
	        return false;
	    }
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.ContactDao#getYukonUser(int)
     */
	@Override
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
    @Override
    public boolean hasPin(int contactId) {
        
        List<LiteContactNotification> liteContactNotifications = contactNotificationDao.getNotificationsForContact(contactId);
        for (Iterator<LiteContactNotification> iter = liteContactNotifications.iterator(); iter.hasNext();) {
            LiteContactNotification contactNotification = iter.next();
            if (contactNotification.getNotificationCategoryID() == YukonListEntryTypes.YUK_ENTRY_ID_PIN) {
                return true;
            }
        }
        return false;
    }

    @Override
    public LiteContact getPrimaryContactForAccount(int accountId) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT c.ContactId, c.ContFirstName, c.ContLastName, c.LogInID, c.AddressID");
        sql.append("FROM Contact c, Customer cu, CustomerAccount ca");
        sql.append("WHERE c.ContactId = cu.PrimaryContactId");
        sql.append("AND cu.CustomerId = ca.CustomerId");
        sql.append("AND ca.AccountId").eq(accountId);

        LiteContact contact = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        return contact;
    }
    

    @Override
    public List<LiteContact> getAdditionalContactsForCustomer(int customerId) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT c.ContactId, c.ContFirstName, c.ContLastName, c.LogInID, c.AddressID");
        sql.append("FROM Contact c, CustomerAdditionalContact cac");
        sql.append("WHERE c.ContactId = cac.ContactId");
        sql.append("AND cac.CustomerId").eq(customerId);
        sql.append("ORDER BY cac.Ordering");

        List<LiteContact> contactList = yukonJdbcTemplate.query(sql, rowMapper);

        return contactList;
    }
    
    @Override
    public List<Integer> getAdditionalContactIdsForCustomer(int customerId) {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT ContactId");
    	sql.append("FROM CustomerAdditionalContact");
    	sql.append("WHERE Customerid").eq(customerId);
    	
        List<Integer> contactIds = yukonJdbcTemplate.query(sql, RowMapper.INTEGER);
        return contactIds;
    }

    @Override
    public List<LiteContact> getAdditionalContactsForAccount(int accountId) {

    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT c.ContactId, c.ContFirstName, c.ContLastName, c.LogInID, c.AddressID");
        sql.append("FROM Contact c, CustomerAdditionalContact cac, Customer cu, CustomerAccount ca");
        sql.append("WHERE c.ContactId = cac.ContactId");
        sql.append("AND cac.CustomerId = cu.CustomerId");
        sql.append("AND cu.CustomerId = ca.CustomerId");
        sql.append("AND ca.AccountId").eq(accountId);
        sql.append("ORDER BY cac.Ordering");

        List<LiteContact> contactList = yukonJdbcTemplate.query(sql, rowMapper);

        return contactList;
    }
    
    @Override
    public int getAllContactCount() {
    	SqlStatementBuilder sql = new SqlStatementBuilder("select count(*) from Contact");
        int count = yukonJdbcTemplate.queryForInt(sql);
        return count;
    }
    
    @Override
    public void callbackWithAllContacts(final SimpleCallback<LiteContact> callback) {
        // the following code may look familiar, I lifted it from the ContactLoader!
    	
        //get all the customer contacts that are assigned to a customer
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT cnt.contactID, cnt.ContFirstName, cnt.ContLastName, cnt.Loginid, cnt.AddressID");
        sql.append("FROM Contact cnt");
        sql.append("where cnt.ContactID").gt(CtiUtilities.NONE_ZERO_ID);
        sql.append("order by cnt.ContLastName, cnt.ContFirstName");

        final LiteContactNoNotificationsRowMapper liteContactNoNotificationsRowMapper = new LiteContactNoNotificationsRowMapper();
        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                LiteContact lc = liteContactNoNotificationsRowMapper.mapRow(rs);
                try {
                    callback.handle(lc);
                } catch (Exception e) {
                    throw new RuntimeException("Unable to use call callback", e);
                }

            }
        });

    }

    @Override
    @Transactional
    public void saveContact(LiteContact contact) {

        int contactId = contact.getContactID();
        DbChangeType dbChangeType;
        
        StringBuilder sql = new StringBuilder();
        if (contactId == -1) { // Insert if id is -1, otherwise update
            /*
             * HACK: Legacy code depends on conacts having a dummy address row.
             * This should get fixed which will involve fixing dbeditor.
             */
            LiteAddress address = new LiteAddress();
            address.setCityName(CtiUtilities.STRING_NONE);
            address.setCounty(CtiUtilities.STRING_NONE);
            address.setLocationAddress1(CtiUtilities.STRING_NONE);
            address.setLocationAddress2(CtiUtilities.STRING_NONE);
            address.setZipCode(CtiUtilities.STRING_NONE);
            address.setStateCode("");
            addressDao.add(address);
            contact.setAddressID(address.getAddressID());
            // END HACK
            
            dbChangeType = DbChangeType.ADD;
            
            contactId = nextValueHelper.getNextValue("Contact");
            contact.setContactID(contactId);

            sql.append("INSERT INTO Contact");
            sql.append(" (ContFirstName, ContLastName, LogInId, AddressId, ContactId)");
            sql.append(" VALUES (?,?,?,?,?)");
        } else {
            // Update if id is not -1
            dbChangeType = DbChangeType.UPDATE;

            sql.append("UPDATE Contact");
            sql.append(" SET ContFirstName = ?, ContLastName = ?, LogInId = ?, AddressId = ?");
            sql.append(" WHERE ContactId = ?");

        }

        String firstName = SqlUtils.convertStringToDbValue(contact.getContFirstName());
        String lastName = SqlUtils.convertStringToDbValue(contact.getContLastName());
        int loginId = contact.getLoginID();
        int addressId = contact.getAddressID();
        yukonJdbcTemplate.update(sql.toString(),
                                  firstName,
                                  lastName,
                                  loginId,
                                  addressId,
                                  contactId);

        contactNotificationDao.saveNotificationsForContact(contactId,
                                                           contact.getLiteContactNotifications());
        
        dbChangeManager.processDbChange(contactId,
                                        DBChangeMsg.CHANGE_CONTACT_DB,
                                        DBChangeMsg.CAT_CUSTOMERCONTACT,
                                        DBChangeMsg.CAT_CUSTOMERCONTACT,
                                        dbChangeType);
    }
    
    @Override
    @Transactional
    public void deleteContact(int contactId) {
    	
    	SqlStatementBuilder sql = null;
    	LiteContact contact = getContact(contactId); 
    	int addressId = contact.getAddressID();

    	// delete notifications
    	List<Integer> notificationIds = contactNotificationDao.getNotificationIdsForContact(contactId);
    	contactNotificationDao.removeNotifications(notificationIds);
    	
    	// delete ContactNotifGroupMap
    	sql = new SqlStatementBuilder();
    	sql.append("DELETE FROM ContactNotifGroupMap WHERE ContactId").eq(contactId);
    	yukonJdbcTemplate.update(sql);
        
        // delete CustomerAdditionalContact
    	sql = new SqlStatementBuilder();
    	sql.append("DELETE FROM CustomerAdditionalContact WHERE ContactId").eq(contactId);
    	yukonJdbcTemplate.update(sql);
        
        // delete contact
    	sql = new SqlStatementBuilder();
    	sql.append("DELETE FROM Contact WHERE ContactId").eq(contactId);
    	yukonJdbcTemplate.update(sql);
        
        // delete address
        if(addressId != CtiUtilities.NONE_ZERO_ID) {
        	
        	sql = new SqlStatementBuilder();
        	sql.append("DELETE FROM Address WHERE AddressId").eq(addressId);
        	yukonJdbcTemplate.update(sql);
        }

        // db change
        dbChangeManager.processDbChange(contactId,
                                        DBChangeMsg.CHANGE_CONTACT_DB,
                                        DBChangeMsg.CAT_CUSTOMERCONTACT,
                                        DBChangeMsg.CAT_CUSTOMERCONTACT,
                                        DbChangeType.DELETE);
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

        this.associateAdditionalContact(customerId, contactId);
        
    }
    
    @Override
    @Transactional
    public void associateAdditionalContact(int customerId, int contactId) {

    	StringBuilder sql = new StringBuilder("INSERT INTO CustomerAdditionalContact");
        sql.append(" (CustomerId, ContactId, Ordering)");
        sql.append(" VALUES (?,?,?)");
        
        StringBuilder orderSql = new StringBuilder("SELECT MAX(Ordering) + 1 FROM CustomerAdditionalContact WHERE CustomerId = ?");
        int order = yukonJdbcTemplate.queryForInt(orderSql.toString(), customerId);
        
        yukonJdbcTemplate.update(sql.toString(), customerId, contactId, order);
        
        dbChangeManager.processDbChange(contactId,
                                        DBChangeMsg.CHANGE_CONTACT_DB,
                                        DBChangeMsg.CAT_CUSTOMERCONTACT,
                                        DBChangeMsg.CAT_CUSTOMERCONTACT,
                                        DbChangeType.UPDATE);
    	
    }

    /**
     * Helper class to map a result set into LiteContacts
     * 
     * Note: this mapper MUST be used within a transaction
     */
    private class LiteContactRowMapper implements YukonRowMapper<LiteContact> {
        
        private final LiteContactNoNotificationsRowMapper baseMapper = new LiteContactNoNotificationsRowMapper();

        @Override
        public LiteContact mapRow(YukonResultSet rs) throws SQLException {

            LiteContact contact = baseMapper.mapRow(rs);
            
            List<LiteContactNotification> notifications = contactNotificationDao
					.getNotificationsForContact(contact.getContactID());
            contact.getLiteContactNotifications().addAll(notifications);
            
            return contact;
        }

    }
    
    private class LiteContactNoNotificationsRowMapper implements YukonRowMapper<LiteContact> {
        @Override
        public LiteContact mapRow(YukonResultSet rs) throws SQLException {
            int id = rs.getInt("ContactId");
            int loginId = rs.getInt("LoginId");
            int addressId = rs.getInt("AddressId");
            String firstName = SqlUtils.convertDbValueToString(rs.getString("ContFirstName")).trim();
            String lastName = SqlUtils.convertDbValueToString(rs.getString("ContLastName")).trim();

            LiteContact contact = new LiteContact(id);
            contact.setLoginID(loginId);
            contact.setAddressID(addressId);
            contact.setContFirstName(firstName);
            contact.setContLastName(lastName);
            
            return contact;
        }
    }
}