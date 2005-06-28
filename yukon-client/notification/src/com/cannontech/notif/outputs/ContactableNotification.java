package com.cannontech.notif.outputs;

import java.util.*;

import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;

public class ContactableNotification extends ContactableBase {
    List _noChildren = new ArrayList(0);
    private final LiteContactNotification _liteNotif;
    
    public ContactableNotification(LiteContactNotification liteNotif) {
        _liteNotif = liteNotif;
    }
    
    public LiteCustomer getContactableCustomer() throws UnknownCustomerException {
        LiteCustomer customer = ContactFuncs.getCustomer(_liteNotif.getContactID());
        if (customer == null) {
            throw new UnknownCustomerException("Can't return LiteCustomer for contact id " + _liteNotif.getContactID());
        }
        return customer;
    }
    
    public List getNotifications(Set notifTypes) {
        List result;
        Integer type = new Integer(_liteNotif.getNotificationCategoryID());
        if (notifTypes.contains(type)) {
            result = new ArrayList(1);
            result.add(_liteNotif);
        } else {
            result = new ArrayList(0);
        }
        return result;
    }
    
    public String toString() {
        return _liteNotif.toString();
    }
}


