package com.cannontech.core.dao.impl;

import java.util.List;
import java.util.Vector;

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
    public List getAllContacts(int customerID_) {
        synchronized (databaseCache) {
            LiteCustomer customer = (LiteCustomer) databaseCache
                    .getACustomerByCustomerID(customerID_);
            java.util.Vector allContacts = new java.util.Vector(5); // guess
                                                                    // capacity
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

    /*
     * (non-Javadoc)
     * 
     * @see com.cannontech.core.dao.CustomerDao#getLiteCustomer(int)
     */
    public LiteCustomer getLiteCustomer(int custID) {
        synchronized (databaseCache) {
            return (LiteCustomer) databaseCache
                    .getACustomerByCustomerID(custID);
        }
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
    public Vector getAllLiteCustomersByEnergyCompany(int energyCompanyID) {
        Vector liteCustomers = new Vector();
        synchronized (databaseCache) {
            List allCustomers = databaseCache.getAllCustomers();
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
    public LiteCICustomer getCustomerForUser(LiteYukonUser user) {
        LiteContact liteContact = yukonUserDao.getLiteContact(user.getUserID());
        if (liteContact == null) {
            return null;
        }
        LiteCICustomer customer = contactDao.getCICustomer(liteContact.getContactID());
        return customer;
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

}
