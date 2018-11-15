package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class CustomerDaoImpl implements CustomerDao {
    
    @Autowired private ContactDao contactDao;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private GraphCustomerListDao graphCustomerListDao;
    @Autowired private DeviceCustomerListDao deviceCustomerListDao;
    @Autowired private AddressDao addressDao;
    @Autowired private EnergyCompanyDao ecDao;
    
    private static final String CUSTOMER_TABLE_NAME = "Customer";
    private static final String CI_CUSTOMER_TABLE_NAME = "CiCustomerBase";
    
    private static final String selectCustomer = new SqlStatementBuilder()
    .append("select c.CustomerId, c.PrimaryContactId, c.CustomerTypeId, c.Timezone, c.CustomerNumber,")
    .append("c.RateScheduleId, c.AltTrackNum, c.TemperatureUnit,")
    .append("ci.MainAddressId, ci.CustomerDemandLevel, ci.CurtailmentAgreement, ci.CurtailAmount,")
    .append("ci.CompanyName, ci.CiCustType,")
    .append("ectam.EnergyCompanyId")
    .append("from Customer c")
    .append("left join CustomerAccount ca on c.CustomerId = ca.CustomerId")
    .append("left join CICustomerBase ci on c.CustomerId = ci.CustomerId")
    .append("left join ECToAccountMapping ectam on ca.AccountId = ectam.AccountId").getSql();
    
    private FieldMapper<LiteCustomer> customerFieldMapper = new FieldMapper<LiteCustomer>() {
        
        @Override
        public void extractValues(MapSqlParameterSource p, LiteCustomer o) {
            p.addValue("PrimaryContactId", o.getPrimaryContactID());
            p.addValue("CustomerTypeId", o.getCustomerTypeID());
            p.addValue("Timezone", SqlUtils.convertStringToDbValue(o.getTimeZone()));
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
            p.addValue("CiCustType", o.getCICustType());
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
    
    @Override
    public List<LiteContact> getAllContacts(LiteCustomer customer) {
        
        List<LiteContact> contacts = new ArrayList<>();
        if (customer != null) {
            LiteContact contact = contactDao.getContact(customer.getPrimaryContactID());
            if (contact != null) {
                contacts.add(contact);
            }
            
            for (LiteContact additional: customer.getAdditionalContacts()) {
                contacts.add(additional);
            }
            
            return contacts;
        }
        
        return null;
    }
    
    @Override
    public LiteContact getPrimaryContact(int customerId) {
        
        LiteCustomer customer = cache.getCustomer(customerId);
        if (customer != null) {
            LiteContact liteContact = contactDao.getContact(customer.getPrimaryContactID());
            if (liteContact != null) {
                return liteContact;
            }
        }
        
        return null;
    }
    
    @Override
    @Transactional
    public void deleteCustomer(int customerId) {
        
        boolean ciCustomer = isCICustomer(customerId);
        
        graphCustomerListDao.deleteGraphsForCustomer(customerId);
        deviceCustomerListDao.deleteDeviceListForCustomer(customerId);
        
        /* Need to load the primaryContact before deleting the customer */
        LiteContact primaryContact = getPrimaryContact(customerId);
        
        /* delete additional contacts - needs to happen before Customer is deleted */
        List<Integer> additionalContacts = contactDao.getAdditionalContactIdsForCustomer(customerId);
        for (int contactId : additionalContacts) {
            LiteContact contact = contactDao.getContact(contactId);
            contactDao.deleteContact(contact);
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder("delete from CustomerNotifGroupMap where CustomerId")
        .eq(customerId);
        yukonJdbcTemplate.update(sql);
        
        if (ciCustomer) {
            deleteCICustomer(customerId);
        }
        
        sql = new SqlStatementBuilder("delete from Customer where CustomerId").eq(customerId);
        yukonJdbcTemplate.update(sql);
        
        /* delete primary contact - needs to happen after Customer is deleted */
        contactDao.deleteContact(primaryContact);
        
    }
    
    @Override
    public void deleteCICustomer(int customerId) {
        
        String sql = "delete from EnergyCompanyCustomerList where CustomerId = ?";
        yukonJdbcTemplate.update(sql, customerId);
        sql = "delete from LmEnergyExchangeHourlyCustomer where CustomerId = ?";
        yukonJdbcTemplate.update(sql, customerId);
        sql = "delete from LmEnergyExchangeCustomerReply where CustomerId = ?";
        yukonJdbcTemplate.update(sql, customerId);
        sql = "delete from LmCurtailCustomerActivity where CustomerId = ?";
        yukonJdbcTemplate.update(sql, customerId);
        sql = "delete from CiCustomerPointData where CustomerId = ?";
        yukonJdbcTemplate.update(sql, customerId);
        sql = "delete from CustomerBaseLinePoint where CustomerId = ?";
        yukonJdbcTemplate.update(sql, customerId);
        
        int companyAddressId = getAddressIdForCICustomer(customerId);
        String delete = "delete from CiCustomerBase where CustomerId = ?";
        yukonJdbcTemplate.update(delete, customerId);
        if(companyAddressId > CtiUtilities.NONE_ZERO_ID) {
            addressDao.remove(companyAddressId);
        }
    }
    
    @Override
    public int getAddressIdForCICustomer(int customerId) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MainAddressId");
        sql.append("FROM CiCustomerBase");
        sql.append("WHERE CustomerId").eq(customerId);
        int addressId = yukonJdbcTemplate.queryForInt(sql);
        return addressId;
    }
    
    @Override
    public boolean isCICustomer(int customerId) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CustomerTypeId");
        sql.append("FROM Customer");
        sql.append("WHERE CustomerId").eq(customerId);
        Integer type = yukonJdbcTemplate.queryForInt(sql);
        if(type == CustomerTypes.CUSTOMER_CI) {
            return true;
        }
        return false;
    }
    
    @Override
    public LiteCustomer getLiteCustomer(int customerId) {
        
        final SqlStatementBuilder sql = new SqlStatementBuilder(selectCustomer);
        sql.append("where c.CustomerId").eq(customerId);
        LiteCustomer customer = yukonJdbcTemplate.queryForObject(sql, new TypeAwareCustomerRowMapper());
        
        List<LiteContact> additionalContacts = contactDao.getAdditionalContactsForCustomer(customerId);
        customer.setAdditionalContacts(additionalContacts);
        
        return customer;
    }
    
    @Override
    public LiteCustomer getLiteCustomerByPrimaryContact(int primaryContactId) {
        
        LiteCustomer customer;
        
        final SqlStatementBuilder sql = new SqlStatementBuilder(selectCustomer);
        sql.append("where c.PrimaryContactId").eq(primaryContactId);
        
        try {
            customer = yukonJdbcTemplate.queryForObject(sql, new TypeAwareCustomerRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        
        List<LiteContact> additionalContacts = contactDao.getAdditionalContactsForCustomer(primaryContactId);
        customer.setAdditionalContacts(additionalContacts);
        
        return customer;
    }
    
    @Override
    public CustomerInformation getCustomerInformation(int customerId) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        // customerId customerTypeId primaryContactId companyName
        
        sql.append("select c.CustomerId, ci.CompanyName, con.ContFirstName, con.ContLastName, cn.Notification");
        sql.append("from Customer c");
        sql.append("  join Contact con on c.PrimaryContactId = con.ContactId");
        sql.append("  left join ContactNotification cn on con.ContactId = cn.ContactId and cn.NotificationCategoryId").eq_k(ContactNotificationType.HOME_PHONE);
        sql.append("  left join CiCustomerBase ci on c.CustomerId = ci.CustomerId");
        sql.append("where c.CustomerId").eq(customerId);
        sql.append("order by cn.Ordering");
        
        List<CustomerInformation> customerInfo = yukonJdbcTemplate.query(sql, new CustomerInformationRowMapper());
        
        return customerInfo.get(0);
    }
    
    @Override
    public LiteCICustomer getLiteCICustomer(int customerId) {
        // Get the customer from AllCustomersMap (make use of the map), retun
        // null if NOT instance LiteCICustomer
        LiteCustomer lc = getLiteCustomer(customerId);
        if (lc instanceof LiteCICustomer && lc.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI) {
            return (LiteCICustomer) lc;
        }

        return null;
    }
    
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
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT cu.CustomerId");
        sql.append("FROM Customer cu, Contact c");
        sql.append("WHERE cu.PrimaryContactId = c.ContactId");
        sql.append(  "AND c.LoginId").eq(userId);

        int customerId = yukonJdbcTemplate.queryForInt(sql);
        LiteCustomer customer = this.getLiteCustomer(customerId);

        return customer;
    }
    
    @Override
    public void callbackWithAllCiCustomers(final SimpleCallback<LiteCICustomer> callback) {
        
        final SqlStatementBuilder sql = new SqlStatementBuilder(selectCustomer);
        sql.append("WHERE CustomerTypeId").eq_k(CustomerTypes.CUSTOMER_CI);
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
        sql.append("select count(*)");
        sql.append("from CiCustomerBase");
        int result = yukonJdbcTemplate.queryForInt(sql);
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
            customer.setTimeZone(SqlUtils.convertDbValueToString(rs.getString("Timezone")));
            customer.setCustomerNumber(SqlUtils.convertDbValueToString(rs.getString("CustomerNumber")));
            customer.setRateScheduleID(rs.getInt("RateScheduleId"));
            customer.setTemperatureUnit(SqlUtils.convertDbValueToString(rs.getString("TemperatureUnit")));
            customer.setAltTrackingNumber(SqlUtils.convertDbValueToString(rs.getString("AltTrackNum")));
            int energyCompanyId = rs.getInt("EnergyCompanyId");
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
    
    private class CustomerInformationRowMapper implements RowMapper<CustomerInformation> {
        @Override
        public CustomerInformation mapRow(ResultSet rs, int rowNum) throws SQLException {
            
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
            String sql = "update Customer set TemperatureUnit = ? where CustomerId = ?";
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
    
    @Override
    public Map<Integer, LiteCustomer> getCustomersWithEmptyAltTrackNum(List<Integer> customerIds) {
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT CustomerId, PrimaryContactId, CustomerTypeId, Timezone, CustomerNumber, RateScheduleId, AltTrackNum, TemperatureUnit");
                sql.append("FROM Customer");
                sql.append("WHERE AltTrackNum IN('(none)', '', ' '");
                sql.append("AND CustomerId").in(subList);
                return sql;           
            }
        };
        
        YukonRowMapper<LiteCustomer> mapper = (YukonResultSet rs) -> {
            LiteCustomer customer = new LiteCustomer();
            customer.setCustomerID(rs.getInt("CustomerId"));
            customer.setCustomerNumber("CustomerNumber");
            customer.setPrimaryContactID(rs.getInt("PrimaryContactId"));
            customer.setCustomerTypeID(rs.getInt("CustomerTypeId"));
            customer.setTimeZone(rs.getStringSafe("Timezone"));
            customer.setRateScheduleID(rs.getInt("RateScheduleId"));
            customer.setAltTrackingNumber(rs.getStringSafe("AltTrackNum"));
            customer.setTemperatureUnit(rs.getStringSafe("TemperatureUnit"));
            return customer;
        };
        
        ChunkingSqlTemplate chunkingSqlTemplate = new ChunkingSqlTemplate(yukonJdbcTemplate);
        List<Entry<Integer, LiteCustomer>> customers = chunkingSqlTemplate.query(sqlGenerator,  Lists.newArrayList(customerIds), (YukonResultSet rs) -> {
            return Maps.immutableEntry(rs.getInt("CustomerId"), mapper.mapRow(rs));
        });
        
        return customers.stream()
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
    
    @PostConstruct
    public void init() throws Exception {
        liteCustomerTemplate = new SimpleTableAccessTemplate<>(yukonJdbcTemplate, nextValueHelper);
        liteCustomerTemplate.setTableName(CUSTOMER_TABLE_NAME);
        liteCustomerTemplate.setPrimaryKeyField("CustomerId");
        liteCustomerTemplate.setFieldMapper(customerFieldMapper);
        liteCustomerTemplate.setPrimaryKeyValidOver(-1);
        
        liteCICustomerTemplate = new SimpleTableAccessTemplate<>(yukonJdbcTemplate, nextValueHelper);
        liteCICustomerTemplate.setTableName(CI_CUSTOMER_TABLE_NAME);
        liteCICustomerTemplate.setPrimaryKeyField("CustomerId");
        liteCICustomerTemplate.setFieldMapper(ciCustomerFieldMapper); 
    }
}