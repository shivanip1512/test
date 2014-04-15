package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.DeviceCustomerListDao;
import com.cannontech.core.dao.GraphCustomerListDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.customer.model.CustomerInformation;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.spring.SeparableRowMapper;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here. Creation date: (3/26/2001 9:40:33 AM)
 * 
 * @author:
 */
public final class CustomerDaoImpl implements CustomerDao {

    @Autowired private ContactDao contactDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private GraphCustomerListDao graphCustomerListDao;
    @Autowired private DeviceCustomerListDao deviceCustomerListDao;
    @Autowired private AddressDao addressDao;
    @Autowired private EnergyCompanyDao ecDao;
    
    private static final String CUSTOMER_TABLE_NAME = "Customer";
    private static final String CI_CUSTOMER_TABLE_NAME = "CICustomerBase";
    
    /**
     * CustomerFuncs constructor comment.
     */
    private CustomerDaoImpl() {
        super();
    }
    
    private FieldMapper<LiteCustomer> customerFieldMapper = new FieldMapper<LiteCustomer>() {

        @Override
        public void extractValues(MapSqlParameterSource p, LiteCustomer o) {
            p.addValue("PrimaryContactId", o.getPrimaryContactID());
            p.addValue("CustomerTypeId", o.getCustomerTypeID());
            p.addValue("TimeZone", SqlUtils.convertStringToDbValue(o.getTimeZone()));
            p.addValue("CustomerNumber", SqlUtils.convertStringToDbValue(o.getCustomerNumber()));
            p.addValue("RateScheduleId", o.getRateScheduleID());
            p.addValue("AltTrackNum", SqlUtils.convertStringToDbValue(o.getAltTrackingNumber()));
            p.addValue("TemperatureUnit", SqlUtils.convertStringToDbValue(o.getTemperatureUnit()));
        }

        @Override
        public Number getPrimaryKey(LiteCustomer object) {
            return object.getCustomerID();
        }

        @Override
        public void setPrimaryKey(LiteCustomer object, int value) {
            object.setCustomerID(value);
        }
    };
    
    private FieldMapper<LiteCICustomer> ciCustomerFieldMapper = new FieldMapper<LiteCICustomer>() {

        @Override
        public void extractValues(MapSqlParameterSource p, LiteCICustomer o) {
            p.addValue("MainAddressId", o.getMainAddressID());
            p.addValue("CustomerDemandLevel", o.getDemandLevel());
            p.addValue("CurtailmentAgreement", CtiUtilities.STRING_NONE);
            p.addValue("CurtailAmount", o.getCurtailAmount());
            p.addValue("CompanyName", SqlUtils.convertStringToDbValue(o.getCompanyName()));
            p.addValue("CICustType", o.getCICustType());
        }

        @Override
        public Number getPrimaryKey(LiteCICustomer object) {
            return object.getCustomerID();
        }

        @Override
        public void setPrimaryKey(LiteCICustomer object, int value) {
            object.setCustomerID(value);
        }
    };
    
    private SimpleTableAccessTemplate<LiteCustomer> liteCustomerTemplate;
    private SimpleTableAccessTemplate<LiteCICustomer> liteCICustomerTemplate;

    /*
     * (non-Javadoc)
     * 
     * @see com.cannontech.core.dao.CustomerDao#getAllContacts(int)
     */
    @Override
    public List<LiteContact> getAllContacts(LiteCustomer customer) {
        Vector<LiteContact> allContacts = new Vector<LiteContact>(5); // guess capacity
        if (customer != null) {
            int primCntctID = customer.getPrimaryContactID();
            LiteContact liteContact = contactDao.getContact(primCntctID);
            if (liteContact != null) {
                allContacts.addElement(liteContact);
            }

            for (int i = 0; i < customer.getAdditionalContacts().size(); i++) {
                allContacts.addElement(customer.getAdditionalContacts()
                                       .get(i));
            }
            return allContacts;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cannontech.core.dao.CustomerDao#getPrimaryContact(int)
     */
    @Override
    public LiteContact getPrimaryContact(int customerID_) {
        LiteCustomer customer = databaseCache.getACustomerByCustomerID(customerID_);
        if (customer != null) {
            int primCntctID = customer.getPrimaryContactID();
            LiteContact liteContact = contactDao.getContact(primCntctID);
            if (liteContact != null) {
                return liteContact;
            }
        }
        return null;
    }
    
    @Override
    @Transactional
    public void deleteCustomer(Integer customerId) {
        graphCustomerListDao.deleteGraphsForCustomer(customerId);
        deviceCustomerListDao.deleteDeviceListForCustomer(customerId);
        
        /*
         * Delete additional contacts
         * - needs to happen before Customer is deleted
         */
        List<Integer> additionalContacts = contactDao.getAdditionalContactIdsForCustomer(customerId);
        for (int contactId : additionalContacts) {
            LiteContact contact = contactDao.getContact(contactId);
        	contactDao.deleteContact(contact);
        }

        String deleteCustomerNotifGroupMap = "DELETE FROM CustomerNotifGroupMap WHERE CustomerId = ?";
        yukonJdbcTemplate.update(deleteCustomerNotifGroupMap, customerId);
        boolean isCICustomer = isCICustomer(customerId);
        if(isCICustomer) {
            deleteCICustomer(customerId);
        }
        String delete = "DELETE FROM Customer WHERE CustomerId = ?";
        yukonJdbcTemplate.update(delete, customerId);
        
        /*
         * Delete primary contact
         * - needs to happen after Customer is deleted
         */
        LiteContact primaryContact = getPrimaryContact(customerId);
        contactDao.deleteContact(primaryContact);
        
    }
    
    @Override
    public void deleteCICustomer(Integer customerId) {
        String sql = "DELETE FROM EnergyCompanyCustomerList WHERE CustomerID = ?";
        yukonJdbcTemplate.update(sql, customerId);
        sql = "DELETE FROM LMEnergyExchangeHourlyCustomer WHERE CustomerId = ?";
        yukonJdbcTemplate.update(sql, customerId);
        sql = "DELETE FROM LMEnergyExchangeCustomerReply WHERE CustomerId = ?";
        yukonJdbcTemplate.update(sql, customerId);
        sql = "DELETE FROM LMCurtailCustomerActivity WHERE CustomerId = ?";
        yukonJdbcTemplate.update(sql, customerId);
        sql = "DELETE FROM CICustomerPointData WHERE CustomerId = ?";
        yukonJdbcTemplate.update(sql, customerId);
        sql = "DELETE FROM CustomerBaseLinePoint WHERE CustomerId = ?";
        yukonJdbcTemplate.update(sql, customerId);
        
        int companyAddressId = getAddressIdForCICustomer(customerId);
        String delete = "DELETE FROM CICustomerBase WHERE CustomerId = ?";
        yukonJdbcTemplate.update(delete, customerId);
        if(companyAddressId > CtiUtilities.NONE_ZERO_ID) {
            addressDao.remove(companyAddressId);
        }
    }
    
    @Override
    public int getAddressIdForCICustomer(int customerId) {
        String sql = "SELECT MainAddressId from CICustomerBase WHERE CustomerId = ?";
        int addressId = yukonJdbcTemplate.queryForInt(sql, customerId);
        return addressId;
    }
    
    @Override
    public boolean isCICustomer(Integer customerId) {
        String sql = "SELECT CustomerTypeId FROM Customer WHERE CustomerId = ?";
        Integer type = yukonJdbcTemplate.queryForInt(sql, customerId);
        if(type == CustomerTypes.CUSTOMER_CI) {
            return true;
        }
        return false;
    }

    @Override
    public LiteCustomer getLiteCustomer(int customerId) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT c.*, ectam.EnergyCompanyId, ci.*");
        sql.append("FROM Customer c");
        sql.append("LEFT JOIN CustomerAccount ca ON c.CustomerId = ca.CustomerId");
        sql.append("LEFT JOIN CICustomerBase ci ON c.CustomerID = ci.CustomerID");
        sql.append("LEFT JOIN ECToAccountMapping ectam ON ca.AccountId = ectam.AccountId");
        sql.append("WHERE c.CustomerId").eq(customerId);

        LiteCustomer customer = yukonJdbcTemplate.queryForObject(sql, new TypeAwareCustomerRowMapper());

        List<LiteContact> additionalContacts = contactDao.getAdditionalContactsForCustomer(customerId);
        customer.setAdditionalContacts(additionalContacts);

        return customer;
    }

    @Override
    public CustomerInformation getCustomerInformation(int customerId) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        // customerId customerTypeId primaryContactId companyName

        sql.append("SELECT c.CustomerId, ci.CompanyName, con.ContFirstName, con.ContLastName, cn.Notification");
        sql.append("FROM Customer c");
        sql.append("  JOIN Contact con on c.primaryContactId = con.ContactId");
        sql.append("  LEFT JOIN ContactNotification cn on con.contactId = cn.ContactId and cn.notificationCategoryId").eq_k(ContactNotificationType.HOME_PHONE);
        sql.append("  LEFT JOIN CICustomerBase ci ON c.CustomerID = ci.CustomerID");
        sql.append("WHERE c.CustomerId").eq(customerId);
        sql.append("ORDER BY cn.Ordering");

        List<CustomerInformation> customerInfo = yukonJdbcTemplate.query(sql, new CustomerInformationRowMapper());

        return customerInfo.get(0);
    }

    /**
     * Finds all LiteContact instances not used by a CICustomer
     * 
     * @return LiteContact
     * 
     * 
     * public LiteContact[] getUnusedContacts( ) {
     * com.cannontech.database.cache.DefaultDatabaseCache cache =
     * com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
     * 
     * ArrayList retValues = new ArrayList(50);
     * 
     * synchronized(cache) { List customers = cache.getAllCICustomers(); List
     * contacts = cache.getAllContacts();
     * 
     * for( int i = 0; i < contacts.size(); i++ ) { LiteContact cnt =
     * (LiteContact)contacts.get(i); boolean found = false;
     * 
     * for( int j = 0; j < customers.size(); j++ ) { if(
     * !((LiteCICustomer)customers.get(j)).getAdditionalContacts().contains(cnt) )
     * continue; else { found = true; break; } }
     * 
     * if( !found ) retValues.add( cnt );
     *  } }
     * 
     * //sort the contacts java.util.Collections.sort( retValues,
     * com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
     * 
     * LiteContact[] cnts = new LiteContact[ retValues.size() ]; return
     * (LiteContact[])retValues.toArray( cnts ); }
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.cannontech.core.dao.CustomerDao#getLiteCICustomer(int)
     */
    @Override
    public LiteCICustomer getLiteCICustomer(int customerID) {
        // Get the customer from AllCustomersMap (make use of the map), retun
        // null if NOT instance LiteCICustomer
        LiteCustomer lc = getLiteCustomer(customerID);
        if (lc instanceof LiteCICustomer && lc.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI) {
            return (LiteCICustomer) lc;
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cannontech.core.dao.CustomerDao#getCustomerForUser(com.cannontech.database.data.lite.LiteYukonUser)
     */
    @Override
    public LiteCICustomer getCICustomerForUser(LiteYukonUser user) {
        LiteContact liteContact = yukonUserDao.getLiteContact(user.getUserID());
        if (liteContact == null) {
            return null;
        }
        LiteCICustomer customer = contactDao.getCICustomer(liteContact.getContactID());
        return customer;
    }

    @Override
    public LiteCustomer getCustomerForUser(int userId) {

        StringBuilder sql = new StringBuilder("SELECT cu.CustomerId");
        sql.append(" FROM Customer cu, Contact c");
        sql.append(" WHERE cu.PrimaryContactId = c.ContactId");
        sql.append(" AND c.LoginId = ?");

        int customerId = yukonJdbcTemplate.queryForInt(sql.toString(), userId);
        LiteCustomer customer = this.getLiteCustomer(customerId);

        return customer;
    }
    
    @Override
    public void callbackWithAllCiCustomers(final SimpleCallback<LiteCICustomer> callback) {

        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT c.*, ectam.EnergyCompanyId, ci.*");
        sql.append("FROM Customer c");
        sql.append("  JOIN CICustomerBase ci ON c.CustomerID = ci.CustomerID");
        sql.append("  LEFT JOIN CustomerAccount ca ON c.CustomerId = ca.CustomerId");
        sql.append("  LEFT JOIN ECToAccountMapping ectam ON ca.AccountId = ectam.AccountId");
        sql.append("ORDER BY CompanyName");

        final CiCustomerRowMapper customerRowMapper = new CiCustomerRowMapper();
        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                LiteCICustomer lc = customerRowMapper.mapRow(rs);
                try {
                    callback.handle(lc);
                } catch (Exception e) {
                    throw new RuntimeException("Unable to use call callback", e);
                }
            }
        });
    }
    
    @Override
    public int getAllCiCustomerCount() {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append("FROM CICustomerBase");
        int result = yukonJdbcTemplate.queryForInt(sql.getSql());
        return result;
    }
    
    private static class TypeAwareCustomerRowMapper implements YukonRowMapper<LiteCustomer> {
        private YukonRowMapper<LiteCustomer> customer = new CustomerRowMapper();
        private YukonRowMapper<LiteCICustomer> ciCustomer = new CiCustomerRowMapper();
        @Override
        public LiteCustomer mapRow(YukonResultSet rs) throws SQLException {
            final int customerTypeId = rs.getInt("CustomerTypeId");
            final boolean isCiCustomer = customerTypeId == CustomerTypes.CUSTOMER_CI;
             
            return isCiCustomer ? ciCustomer.mapRow(rs) : customer.mapRow(rs);
        }
    }
    
    private static class CustomerRowMapper extends SeparableRowMapper<LiteCustomer> {
        @Override
        protected LiteCustomer createObject(YukonResultSet rs) throws SQLException {
            return new LiteCustomer(rs.getInt("CustomerId"));
        }
        
        @Override
        public void mapRow(YukonResultSet rs, LiteCustomer customer) throws SQLException {
            customer.setPrimaryContactID(rs.getInt("PrimaryContactId"));
            customer.setCustomerTypeID(rs.getInt("CustomerTypeId"));
            customer.setTimeZone(SqlUtils.convertDbValueToString(rs.getString("TimeZone")));
            customer.setCustomerNumber(SqlUtils.convertDbValueToString(rs.getString("CustomerNumber")));
            customer.setRateScheduleID(rs.getInt("RateScheduleId"));
            customer.setTemperatureUnit(SqlUtils.convertDbValueToString(rs.getString("TemperatureUnit")));
            customer.setAltTrackingNumber(SqlUtils.convertDbValueToString(rs.getString("AltTrackNum")));
            int energyCompanyId = rs.getInt("energyCompanyId");
            if (!rs.wasNull())
            {
                customer.setEnergyCompanyID(energyCompanyId);
            }
        }
    }
    
    private static class CiCustomerRowMapper extends SeparableRowMapper<LiteCICustomer> {
        public CiCustomerRowMapper() {
            super(new CustomerRowMapper());
        }
        
        @Override
        protected LiteCICustomer createObject(YukonResultSet rs) throws SQLException {
            return new LiteCICustomer(rs.getInt("CustomerId"));
        }
        
        @Override
        public void mapRow(YukonResultSet rs, LiteCICustomer customer) throws SQLException {

            final LiteCICustomer ciCustomer = customer;
            ciCustomer.setMainAddressID(rs.getInt("MainAddressID"));            
            ciCustomer.setCompanyName(SqlUtils.convertDbValueToString(rs.getResultSet(), "CompanyName"));   
            ciCustomer.setDemandLevel(rs.getDouble("CustomerDemandLevel"));
            ciCustomer.setCurtailAmount(rs.getDouble("CurtailAmount"));
            ciCustomer.setCICustType(rs.getInt("CICustType"));
        }
    }

    private class CustomerInformationRowMapper implements ParameterizedRowMapper<CustomerInformation> {
        @Override
        public CustomerInformation mapRow(ResultSet rs, int rowNum)
                throws SQLException {

            int customerId = rs.getInt("CustomerId");
            String firstName = SqlUtils.convertDbValueToString(rs.getString("ContFirstName"));
            String lastName = SqlUtils.convertDbValueToString(rs.getString("ContLastName"));
            String homePhone = SqlUtils.convertDbValueToString(rs.getString("Notification"));
            String companyName = SqlUtils.convertDbValueToString(rs.getString("CompanyName"));

            CustomerInformation customerInfo = new CustomerInformation(customerId);
            customerInfo.setContactFirstName(firstName);
            customerInfo.setContactLastName(lastName);
            customerInfo.setContactHomePhone(homePhone);
			customerInfo.setCompanyName(companyName);   

            return customerInfo;
        }
    }

    @Override
    public void setTemperatureUnit(int customerId, String temp) throws IllegalArgumentException {
        if ("C".equalsIgnoreCase(temp) || "F".equalsIgnoreCase(temp)) {
            String sql = "UPDATE Customer SET TemperatureUnit = ? WHERE CustomerId = ?";
            yukonJdbcTemplate.update(sql.toString(), temp ,customerId);
            return;
        }
        throw new IllegalArgumentException("Temperature preference invalid.  Supplied: ["+temp+"]. Acceptable (escaped) values: ['F', 'C']");
    }
    
    @Override
    @Transactional
    public void addCustomer(LiteCustomer liteCustomer) throws DataAccessException {
        liteCustomerTemplate.insert(liteCustomer);
    }
    
    @Override
    @Transactional
    public void addCICustomer(LiteCICustomer customer) throws DataAccessException {
        liteCICustomerTemplate.insert(customer);
        EnergyCompany energyCompany = ecDao.getEnergyCompany(customer.getEnergyCompanyID());
        ecDao.addCiCustomer(customer.getCustomerID(), energyCompany);
    }
    
    @Override
    @Transactional
    public void updateCustomer(LiteCustomer customer) {
        liteCustomerTemplate.update(customer);
    }
    
    @Override
    @Transactional
    public void updateCICustomer(LiteCICustomer customer) {
        liteCICustomerTemplate.update(customer);
    }

    @PostConstruct
    public void init() throws Exception {
        liteCustomerTemplate = new SimpleTableAccessTemplate<LiteCustomer>(yukonJdbcTemplate, nextValueHelper);
        liteCustomerTemplate.setTableName(CUSTOMER_TABLE_NAME);
        liteCustomerTemplate.setPrimaryKeyField("CustomerId");
        liteCustomerTemplate.setFieldMapper(customerFieldMapper);
        liteCustomerTemplate.setPrimaryKeyValidOver(-1);
        
        liteCICustomerTemplate = new SimpleTableAccessTemplate<LiteCICustomer>(yukonJdbcTemplate, nextValueHelper);
        liteCICustomerTemplate.setTableName(CI_CUSTOMER_TABLE_NAME);
        liteCICustomerTemplate.setPrimaryKeyField("CustomerId");
        liteCICustomerTemplate.setFieldMapper(ciCustomerFieldMapper); 
    }
}
