package com.cannontech.notif.outputs;

import java.util.*;

import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.*;

public class ContactableContact extends ContactableBase {
    List _notifList = new LinkedList();
    private final LiteContact _liteContact;
    
    public ContactableContact(LiteContact contact) {
        _liteContact = contact;
        List notifs = contact.getLiteContactNotifications();
        for (Iterator iter = notifs.iterator(); iter.hasNext();) {
            LiteContactNotification liteNotif = (LiteContactNotification) iter.next();
            _notifList.add(new ContactableNotification(liteNotif));
        }
    }
    
    public LiteCustomer getContactableCustomer() throws UnknownCustomerException {
        LiteCustomer customer = ContactFuncs.getCustomer(_liteContact.getContactID());
        if (customer == null) {
            throw new UnknownCustomerException("Can't return LiteCustomer for contact id " + _liteContact.getContactID());
        }
        return customer;
    }
    
    public List getNotifications(Set notifTypes) {
        List result = new LinkedList();
        for (Iterator iter = _notifList.iterator(); iter.hasNext();) {
            ContactableBase contactable = (ContactableBase) iter.next();
            result.addAll(contactable.getNotifications(notifTypes));
        };
        return result;
    }



}
