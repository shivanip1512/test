package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here. Creation date: (3/26/2001 9:40:33 AM)
 * 
 * @author:
 */
public final class CustomerDaoImpl implements CustomerDao {

    private ContactDao contactDao;
    private YukonUserDao yukonUserDao;    
    private IDatabaseCache databaseCache;
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    /**
     * CustomerFuncs constructor comment.
     */
    private CustomerDaoImpl() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cannontech.core.dao.CustomerDao#getAllContacts(int)
     */
    public List<LiteContact> getAllContacts(int customerID_) {
        synchronized (databaseCache) {
            LiteCustomer customer = (LiteCustomer) databaseCache
                    .getACustomerByCustomerID(customerID_);
            Vector<LiteContact> allContacts = new Vector<LiteContact>(5); // guess capacity
            if (customer != null) {
                int primCntctID = customer.getPrimaryContactID();
                LiteContact liteContact = contactDao.getContact(primCntctID);
                if (liteContact != null)
                    allContacts.addElement(liteContact);

                for (int i = 0; i < customer.getAdditionalContacts().size(); i++) {
                    allContacts.addElement(customer.getAdditionalContacts()
                            .get(i));
                }
                return allContacts;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cannontech.core.dao.CustomerDao#getPrimaryContact(int)
     */
    public LiteContact getPrimaryContact(int customerID_) {
        synchronized (databaseCache) {
            LiteCustomer customer = (LiteCustomer) databaseCache
                    .getACustomerByCustomerID(customerID_);
            if (customer != null) {
                int primCntctID = customer.getPrimaryContactID();
                LiteContact liteContact = contactDao.getContact(primCntctID);
                if (liteContact != null)
                    return liteContact;
            }
        }
        return null;
    }

    public LiteCustomer getLiteCustomer(int customerId) {

        StringBuilder sql = new StringBuilder("SELECT c.*, ectam.EnergyCompanyId");
        sql.append(" FROM Customer c, CustomerAccount ca, ECToAccountMapping ectam");
        sql.append(" WHERE ectam.AccountId = ca.AccountId");
        sql.append(" AND ca.CustomerId = c.CustomerId");
        sql.append(" AND c.CustomerId = ?");

        LiteCustomer customer = simpleJdbcTemplate.queryForObject(sql.toString(),
                                                                  new CustomerRowMapper(),
                                                                  customerId);

        
        List<LiteContact> additionalContacts = contactDao.getAdditionalContactsForCustomer(customerId);
        customer.setAdditionalContacts(additionalContacts);
        
        return customer;
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
    public LiteCICustomer getLiteCICustomer(int customerID) {
        // Get the customer from AllCustomersMap (make use of the map), retun
        // null if NOT instance LiteCICustomer
        LiteCustomer lc = getLiteCustomer(customerID);
        if (lc instanceof LiteCICustomer)
            return (LiteCICustomer) lc;

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cannontech.core.dao.CustomerDao#getAllLiteCustomersByEnergyCompany(int)
     */
    public Vector<LiteCustomer> getAllLiteCustomersByEnergyCompany(int energyCompanyID) {
        Vector<LiteCustomer> liteCustomers = new Vector<LiteCustomer>();
        synchronized (databaseCache) {
            List<LiteCustomer> allCustomers = databaseCache.getAllCustomers();
            for (int i = 0; i < allCustomers.size(); i++) {
                LiteCustomer lc = (LiteCustomer) allCustomers.get(i);
                if (lc.getEnergyCompanyID() == energyCompanyID)
                    liteCustomers.add(lc);
            }
        }
        return liteCustomers;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.cannontech.core.dao.CustomerDao#getCustomerForUser(com.cannontech.database.data.lite.LiteYukonUser)
     */
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

        int customerId = simpleJdbcTemplate.queryForInt(sql.toString(), userId);
        LiteCustomer customer = this.getLiteCustomer(customerId);

        return customer;
    }
    
    private class CustomerRowMapper implements ParameterizedRowMapper<LiteCustomer> {

        @Override
        public LiteCustomer mapRow(ResultSet rs, int rowNum)
                throws SQLException {

            int customerId = rs.getInt("CustomerId");
            int primaryContactId = rs.getInt("PrimaryContactId");
            int customerTypeId = rs.getInt("CustomerTypeId");
            String timezone = rs.getString("TimeZone");
            String customerNumber = rs.getString("CustomerNumber");
            int rateScheduleId = rs.getInt("RateScheduleId");
            String altTrackNumber = rs.getString("AltTrackNum");
            String temperatureUnit = rs.getString("TemperatureUnit");
            int energyCompanyId = rs.getInt("energyCompanyId");

            LiteCustomer customer = new LiteCustomer(customerId);
            customer.setPrimaryContactID(primaryContactId);
            customer.setCustomerTypeID(customerTypeId);
            customer.setTimeZone(timezone);
            customer.setCustomerNumber(customerNumber);
            customer.setRateScheduleID(rateScheduleId);
            customer.setTemperatureUnit(temperatureUnit);
            customer.setAltTrackingNumber(altTrackNumber);
            customer.setEnergyCompanyID(energyCompanyId);
            
            return customer;
        }
        
    }

    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }

    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

}
