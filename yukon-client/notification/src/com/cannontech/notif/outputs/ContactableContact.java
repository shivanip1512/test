package com.cannontech.notif.outputs;

import java.util.*;

import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.*;

public class ContactableContact extends ContactableBase {
    List _notifList = new LinkedList();
    private final LiteContact _liteContact;
    private LiteCICustomer _customer = null;
    
    public ContactableContact(LiteContact contact) {
        _liteContact = contact;
        List notifs = contact.getLiteContactNotifications();
        for (Iterator iter = notifs.iterator(); iter.hasNext();) {
            LiteContactNotification liteNotif = (LiteContactNotification) iter.next();
            _notifList.add(new ContactableNotification(liteNotif));
        }
    }
    
    public LiteCICustomer getContactableCustomer() throws UnknownCustomerException {
        if (_customer != null) {
            return _customer;
        }
        _customer = ContactFuncs.getCICustomer(_liteContact.getContactID());
        if (_customer == null) {
            throw new UnknownCustomerException("Can't return LiteCustomer for contact id " + _liteContact.getContactID());
        }
        return _customer;
    }
    
    public List getNotifications(Set notifTypes) {
        List result = new LinkedList();
        for (Iterator iter = _notifList.iterator(); iter.hasNext();) {
            ContactableBase contactable = (ContactableBase) iter.next();
            result.addAll(contactable.getNotifications(notifTypes));
        };
        return result;
    }
    
    public String toString() {
        return _liteContact.toString() + "[ConID-" + _liteContact.getContactID() + "]";
    }
}
