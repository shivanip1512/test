package com.cannontech.notif.outputs;

import java.util.*;

import com.cannontech.common.util.NotificationTypeChecker;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.data.lite.*;
import com.cannontech.spring.YukonSpringHook;

public class ContactableCustomer extends ContactableBase {
    private List<ContactableBase> _contactList = new LinkedList<ContactableBase>();
    private final LiteCICustomer _liteCustomer;
    
    /**
     * Create a Contactable object from a CustomerNotifGroupMap.
     * @param customer
     */
    public ContactableCustomer(LiteCICustomer customer) {
        _liteCustomer = customer;
        List contacts = YukonSpringHook.getBean(CustomerDao.class).getAllContacts(customer);
        for (Iterator iter = contacts.iterator(); iter.hasNext();) {
            LiteContact contact = (LiteContact) iter.next();
            _contactList.add(new ContactableContact(contact));
        }        
    }
    
    public List<LiteContactNotification> getNotifications(NotificationTypeChecker checker) {
        List<LiteContactNotification> result = new LinkedList<LiteContactNotification>();
        for (Iterator iter = _contactList.iterator(); iter.hasNext();) {
            ContactableBase contactable = (ContactableBase) iter.next();
            result.addAll(contactable.getNotifications(checker));
        }
        return result;
    }
    
    public LiteCICustomer getContactableCustomer() {
        return _liteCustomer;
    }
    
    public String toString() {
        return _liteCustomer.toString() + "[customerId=" + _liteCustomer.getCustomerID() + "]";
    }
}
