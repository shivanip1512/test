package com.cannontech.web.customer;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.RandomStringUtils;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.data.customer.Customer;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.web.util.JSFUtil;

public class CustomerProfileBean {
    private LiteYukonUser yukonUser;
    private Customer thisCustomer;
    private Contact selectedContact;
    private Contact primaryContact;
    private Contact newContact;
    private List<Contact> additionalContactList = new ArrayList<Contact>();
    private ContactNotification selectedNotification;
    private CustomerDao customerDao;
    private DBPersistentDao dbPersistentDao;
    private boolean needsInitialization = true;
    
    final private SelectItem[] notificationSelections = 
        JSFUtil.convertSelectionList(YukonSelectionListDefs.YUK_LIST_ID_CONTACT_TYPE);

    public LiteYukonUser getYukonUser() {
        return yukonUser;
    }

    public void setYukonUser(LiteYukonUser yukonUser) {
        this.yukonUser = yukonUser;
    }
    
    private synchronized void initialize() {
        if (needsInitialization) {
            doInitialize();
            needsInitialization = false;
        }
    }

    private void doInitialize() {
        LiteCICustomer liteCustomer = customerDao.getCustomerForUser(this.yukonUser);
        Customer customer = (Customer) dbPersistentDao.retrieveDBPersistent(liteCustomer);
        thisCustomer = customer;
        additionalContactList.clear();
        int[] contactIDs = customer.getCustomerContactIDs();
        for (int i = 0; i < contactIDs.length; i++) {
            int contactId = contactIDs[i];
            Contact contact = new Contact(contactId);
            dbPersistentDao.performDBChange(contact, Transaction.RETRIEVE);
            additionalContactList.add(contact);
        }
        
        Integer primaryContactId = customer.getCustomer().getPrimaryContactID();
        primaryContact = new Contact(primaryContactId);
        dbPersistentDao.performDBChange(primaryContact, Transaction.RETRIEVE);
        
        newContact = new Contact();
    }
    
    public String save() {
        //do the saving
        dbPersistentDao.performDBChange(primaryContact, Transaction.UPDATE);
        
        int[] contactIds = new int[additionalContactList.size()];
        int i=0;
        for (Contact contact : additionalContactList) {
            if (contact.getContact().getContactID() == null) {
                YukonUser user = createYukonUser(contact);
                
                contact.getContact().setLogInID(user.getUserID());
                dbPersistentDao.performDBChange(contact, Transaction.INSERT);
            } else {
                dbPersistentDao.performDBChange(contact, Transaction.UPDATE);
            }
            contactIds[i++] = contact.getContact().getContactID();
        }
        thisCustomer.setCustomerContactIDs(contactIds);
        dbPersistentDao.performDBChange(thisCustomer, Transaction.UPDATE);
        
        //re-initialize
        needsInitialization = true;
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public String addNotification() {
        Contact contact = getSelectedContact();
        ContactNotification newNotification = new ContactNotification();
        newNotification.setContactID(contact.getContact().getContactID());
        // hard coding 2 which is email
        Integer defaultCategory = (Integer) getNotificationSelections()[2].getValue();
        newNotification.setNotificationCatID(defaultCategory);
        //DBPersistentFuncs.performDBChange(newNotification, Transaction.INSERT);
        contact.getContactNotifVect().add(newNotification);
        
        return null;
    }
    
    public String deleteNotification() {
        selectedContact.getContactNotifVect().remove(selectedNotification);
        return null;
    }
    
    public String addContact() {
        getAdditionalContactList().add(newContact);
        newContact = new Contact();
        return null;
    }

    private YukonUser createYukonUser(Contact contact) {
        YukonUser user = new YukonUser();
        YukonUser loggedInUser = (YukonUser) LiteFactory.convertLiteToDBPersAndRetrieve(getYukonUser());
        user.setYukonGroups(loggedInUser.getYukonGroups());
        String firstName = contact.getContact().getContFirstName();
        String lastName = contact.getContact().getContLastName();
        String salt = RandomStringUtils.randomNumeric(5);
        String stupidUserName = firstName.toLowerCase().substring(0, 1) + lastName.toLowerCase() + salt;
        user.getYukonUser().setUsername(stupidUserName);
        user.getYukonUser().setPassword(RandomStringUtils.randomAlphanumeric(12));
        user.getYukonUser().setStatus("Enabled");
        dbPersistentDao.performDBChange(user, Transaction.INSERT);
        return user;
    }
    
    public List<Contact> getAdditionalContactList() {
        initialize();
        return additionalContactList;
    }

    public Contact getPrimaryContact() {
        initialize();
        return primaryContact;
    }

    public Customer getThisCustomer() {
        initialize();
        return thisCustomer;
    }

    public Contact getSelectedContact() {
        initialize();
        return selectedContact;
    }

    public void setSelectedContact(Contact selectedContact) {
        this.selectedContact = selectedContact;
    }

    public SelectItem[] getNotificationSelections() {
        initialize();
        return notificationSelections;
    }

    public ContactNotification getSelectedNotification() {
        initialize();
        return selectedNotification;
    }

    public void setSelectedNotification(ContactNotification selectedNotification) {
        this.selectedNotification = selectedNotification;
    }

    public CustomerDao getCustomerDao() {
        return customerDao;
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public DBPersistentDao getDbPersistentDao() {
        return dbPersistentDao;
    }

    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

    public Contact getNewContact() {
        return newContact;
    }

    public void setNewContact(Contact newContact) {
        this.newContact = newContact;
    }
    

    

}
