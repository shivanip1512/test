package com.cannontech.core.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.model.ContactNotificationMethodType;
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
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SqlParameterSink;
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
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableCollection;

public final class ContactDaoImpl implements ContactDao {
    
    @Autowired private AddressDao addressDao;
    @Autowired private CustomerDao customerDao;
    @Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private YukonUserDao userDao;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DbChangeManager dbChangeManager;

    private final Logger log = YukonLogManager.getLogger(ContactDaoImpl.class);
    private final YukonRowMapper<LiteContact> rowMapper = new LiteContactRowMapper();

    @Override
    public LiteContact getContact(int contactId) {
        
        // Return null if 0. This is the "default contact" that we ship with, representing null assignment to customers
        if (contactId == CtiUtilities.NONE_ZERO_ID) {
            return null;
        }
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
        sql.append("AND ContactID").gt(CtiUtilities.NONE_ZERO_ID);
        
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
                sqlBuilder.append("AND ContactID").gt(CtiUtilities.NONE_ZERO_ID);
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
                        if (firstName != null && firstName.length() > 0) {
                            sql += " AND UPPER(CONTFIRSTNAME) LIKE ? ";
                        }
            
            pstmt = conn.prepareStatement( sql );
            pstmt.setString( 1, lastName.toUpperCase()+(partialMatch? "%":""));
            if (firstName != null && firstName.length() > 0) {
                pstmt.setString(2, firstName.toUpperCase() + "%");
            }
            rset = pstmt.executeQuery();
    
            ArrayList<Integer> contactIDList = new ArrayList<Integer>();
            while(rset.next())
            {
                contactIDList.add(new Integer(rset.getInt(1)));
            }
            contactIDs = new int[contactIDList.size()];
            for (int i = 0; i < contactIDList.size(); i++) {
                contactIDs[i] = contactIDList.get(i).intValue();
            }
        }
        catch( Exception e ){
            log.error("Error retrieving contacts with last name " + lastName+ ": " + e.getMessage(), e);
        }
        finally {
            try {
                if (rset != null) {
                    rset.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (java.sql.SQLException e) {}
        }
        return contactIDs;
    }

    @Override
    public List<LiteContact> findContactsByPhoneNo(String phone, boolean partialMatch) {
        if (phone == null) {
            return null;
        }
        
        SqlStatementBuilder sql = getContactByNotificationSql(phone, ContactNotificationMethodType.PHONE, partialMatch);
        List<LiteContact> contacts = yukonJdbcTemplate.query(sql, rowMapper);
        
        //legacy numbers have hyphens in the db; let's at least attempt to be understanding about it
        if(phone.length() == 10) {
            String oldPhoneSyntax = phone.substring(0, 3) + "-" + phone.substring(3, 6) + "-" + phone.substring(6);
            SqlStatementBuilder legacySql = getContactByNotificationSql(oldPhoneSyntax, ContactNotificationMethodType.PHONE, true);
            contacts.addAll(yukonJdbcTemplate.query(legacySql, rowMapper));
        }

        return contacts;
    }

    @Override
    public LiteContact findContactByEmail(String email) 
    {
        if (email == null) {
            return null;
        }

        SqlStatementBuilder sql = getContactByNotificationSql(email, ContactNotificationMethodType.EMAIL, false);
        try {
            return yukonJdbcTemplate.queryForObject(sql, rowMapper);
        } catch(IncorrectResultSizeDataAccessException e) {
            log.error("Error finding contact for email " + email+ "; Actual size:" + e.getActualSize(), e);
            return null;
        }
    }

    @Override
    public List<LiteContact> getUnassignedContacts() {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ContactId, ContFirstName, ContLastName, LogInID, AddressID");
        sql.append("FROM CONTACT cnt");
        sql.append("WHERE NOT EXISTS (SELECT 1 FROM CustomerAdditionalContact ca WHERE ca.ContactId = cnt.ContactId)");
        sql.append("AND NOT EXISTS (SELECT 1 FROM Customer cs WHERE cs.PrimaryContactId = cnt.ContactId)");
        sql.append("AND ContactID").gt(CtiUtilities.NONE_ZERO_ID);
        sql.append("ORDER BY cnt.ContFirstName, cnt.ContLastName");

        final List<LiteContact> contactList = yukonJdbcTemplate.query(sql, rowMapper);
        return contactList;
    }

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

    @Override
    public LiteContactNotification[] getAllPINNotifDestinations( int contactId )
    {
        LiteContact contact = getContact( contactId );
        List<LiteContactNotification> notificationsForContactByType = contactNotificationDao.getNotificationsForContactByType(contact, ContactNotificationType.VOICE_PIN);
        return notificationsForContactByType.toArray( new LiteContactNotification[notificationsForContactByType.size()] );
    }

    @Override
    public List<LiteContactNotification> getAllContactNotifications() 
    {
        return contactNotificationDao.getAllContactNotifications();
    }

    @Override
    public LiteCICustomer getOwnerCICustomer(int additionalContactId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT c.CustomerId");
        sql.append("FROM CustomerAdditionalContact ca, Customer c");
        sql.append("WHERE c.CustomerTypeId").eq_k(CustomerTypes.CUSTOMER_CI);
        sql.append("AND c.CustomerID = ca.CustomerId");
        sql.append("AND ca.ContactID").eq(additionalContactId);
        
        try {
            int customerId = yukonJdbcTemplate.queryForInt(sql);
            if (customerId >= 0) {
                LiteCustomer customer = databaseCache.getCustomer(customerId);
                if (customer != null && customer instanceof LiteCICustomer) {
                    return (LiteCICustomer) customer;
                }
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        
        return null;
    }
    
    @Override
    public LiteCICustomer getPrimaryContactCICustomer(int primaryContactId) {
        
        LiteCustomer customer = customerDao.getLiteCustomerByPrimaryContact(primaryContactId);
        
        if (customer != null && customer instanceof LiteCICustomer) {
            return (LiteCICustomer) customer;
        }
        
        return null;
    }
    
    @Override
    public LiteCICustomer getCICustomer (int contactID_)
    {
        LiteCICustomer liteCICust = getPrimaryContactCICustomer(contactID_);
        if( liteCICust == null) {
            liteCICust = getOwnerCICustomer(contactID_);
        }
            
        return liteCICust;
    }
    
    @Override
    public LiteCustomer getCustomer(int contactId) {
        LiteCustomer customer = customerDao.getLiteCustomerByPrimaryContact(contactId);
        if (customer == null) {
            customer = getOwnerCICustomer(contactId);
        }
        
        return customer;
    }

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

    @Override
    public LiteYukonUser getYukonUser(int contactId) {
        
        LiteContact contact = getContact(contactId);
        
        if (contact != null) {
            return userDao.getLiteYukonUser(contact.getLoginID());
        }
        
        return null;
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
        sql.append("AND c.ContactID").gt(CtiUtilities.NONE_ZERO_ID);
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
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink;
        
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
            
            sink = sql.insertInto("Contact");
            sink.addValue("ContFirstName", SqlUtils.convertStringToDbValue(contact.getContFirstName()));
            sink.addValue("ContLastName", SqlUtils.convertStringToDbValue(contact.getContLastName()));
            sink.addValue("LogInId", contact.getLoginID());
            sink.addValue("AddressId", contact.getAddressID());
            sink.addValue("ContactId", contactId);
        } else {
            // Update if id is not -1
            dbChangeType = DbChangeType.UPDATE;
            sink = sql.update("Contact");
            sink.addValue("ContFirstName", SqlUtils.convertStringToDbValue(contact.getContFirstName()));
            sink.addValue("ContLastName", SqlUtils.convertStringToDbValue(contact.getContLastName()));
            sink.addValue("LogInId", contact.getLoginID());
            sink.addValue("AddressId", contact.getAddressID());
            sql.append("WHERE ContactId").eq(contactId);
        }
        yukonJdbcTemplate.update(sql);

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
    public void deleteContact(LiteContact contact) {
        
        SqlStatementBuilder sql = null;
        if (contact == null || contact.getContactID() == CtiUtilities.NONE_ZERO_ID) {
            // safety check to make sure we didn't somehow get in here with the default LiteContact
            log.warn("Contact was not deleted. Cannot delete default contactId=0.");
            return;
        }
        
        int contactId = contact.getContactID();
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

        SqlStatementBuilder orderSql = new SqlStatementBuilder();
        orderSql.append("SELECT MAX(Ordering) + 1 FROM CustomerAdditionalContact WHERE CustomerId").eq(customerId);
        int order = yukonJdbcTemplate.queryForInt(orderSql);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("CustomerAdditionalContact");
        sink.addValue("CustomerId", customerId);
        sink.addValue("ContactId", contactId);
        sink.addValue("Ordering", order);
        yukonJdbcTemplate.update(sql);

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
    
    /** 
     * Helper method to return sqlStatementBuilder for returning LiteContact.
     * @param notification - notification value to search for
     * @param notificationMethodType - category of types to limit to.
     * @param partialMatch - true when doing a "like" clause. 
     * @return
     */
    private SqlStatementBuilder getContactByNotificationSql(String notification, 
            ContactNotificationMethodType notificationMethodType, boolean partialMatch) {
        
        ImmutableCollection<ContactNotificationType> methodTypes = ContactNotificationType.getFor(notificationMethodType);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT c.ContactId, c.ContFirstName, c.ContLastName, c.LoginID, c.AddressID ");
        sql.append("FROM Contact c JOIN ContactNotification cn on c.ContactId = cn.ContactId");
        sql.append("WHERE NotificationCategoryId").in(methodTypes);
        if (partialMatch) {
            sql.append("AND UPPER(Notification)").contains(notification.toUpperCase());
        } else {
            sql.append("AND UPPER(Notification)").eq(notification.toUpperCase());
        }
        return sql;
    }
    
    @Override
    public String getUserEmail(LiteYukonUser user) {
        LiteContact contact = userDao.getLiteContact(user.getUserID());
        String email = "";
        if (contact != null) {
            String[] allEmailAddresses = getAllEmailAddresses(contact.getContactID());
            if (allEmailAddresses.length > 0) {
                email = allEmailAddresses[0];
            }
        }
        return email;
    }


}