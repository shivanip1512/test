package com.cannontech.web.customer;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.data.customer.Customer;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.contact.ContactNotification;
import com.cannontech.web.util.JSFUtil;

public class CustomerProfileBean {
    private LiteYukonUser yukonUser;
    private Customer thisCustomer;
    private Contact selectedContact;
    private Contact primaryContact;
    private List<Contact> additionalContactList = new ArrayList<Contact>();
    private ContactNotification selectedNotification;
    private CustomerDao customerDao;
    private DBPersistentDao dbPersistentDao;
    
    final private SelectItem[] notificationSelections = 
        JSFUtil.convertSelectionList(YukonSelectionListDefs.YUK_LIST_ID_CONTACT_TYPE);;

    public LiteYukonUser getYukonUser() {
        return yukonUser;
    }

    public void setYukonUser(LiteYukonUser yukonUser) {
        this.yukonUser = yukonUser;
        initialize();
    }

    private void initialize() {
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
    }
    
    public String save() {
        //do the saving
        dbPersistentDao.performDBChange(primaryContact, Transaction.UPDATE);
        
        int[] contactIds = new int[additionalContactList.size()];
        int i=0;
        for (Contact contact : additionalContactList) {
            dbPersistentDao.performDBChange(contact, Transaction.UPDATE);
            contactIds[i++] = contact.getContact().getContactID();
        }
        thisCustomer.setCustomerContactIDs(contactIds);
        dbPersistentDao.performDBChange(thisCustomer, Transaction.UPDATE);
        
        //re-initialize
        initialize();
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
        Contact contact = new Contact(null);
        getAdditionalContactList().add(contact);
        dbPersistentDao.performDBChange(contact, Transaction.INSERT);
        return null;
    }
    
    public List<Contact> getAdditionalContactList() {
        return additionalContactList;
    }

    public Contact getPrimaryContact() {
        return primaryContact;
    }

    public Customer getThisCustomer() {
        return thisCustomer;
    }

    public Contact getSelectedContact() {
        return selectedContact;
    }

    public void setSelectedContact(Contact selectedContact) {
        this.selectedContact = selectedContact;
    }

    public SelectItem[] getNotificationSelections() {
        return notificationSelections;
    }

    public ContactNotification getSelectedNotification() {
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

}
