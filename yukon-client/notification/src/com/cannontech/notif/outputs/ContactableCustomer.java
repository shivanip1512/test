package com.cannontech.notif.outputs;

import java.util.*;

import com.cannontech.database.cache.functions.CustomerFuncs;
import com.cannontech.database.data.lite.*;

public class ContactableCustomer extends ContactableBase {
    private List _contactList = new LinkedList();
    private final LiteCICustomer _liteCustomer;
    
    /**
     * Create a Contactable object from a CustomerNotifGroupMap.
     * @param customer
     */
    public ContactableCustomer(LiteCICustomer customer) {
        _liteCustomer = customer;
        List contacts = CustomerFuncs.getAllContacts(customer.getCustomerID());
        for (Iterator iter = contacts.iterator(); iter.hasNext();) {
            LiteContact contact = (LiteContact) iter.next();
            _contactList.add(new ContactableContact(contact));
        }        
    }
    
    public List getNotifications(NotificationTypeChecker checker) {
        List result = new LinkedList();
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
        return _liteCustomer.toString() + "[CusID " + _liteCustomer.getCustomerID() + "]";
    }
}
