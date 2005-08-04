package com.cannontech.notif.outputs;

import java.util.*;

import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContactNotification;

public class ContactableNotification extends ContactableBase {
    List _noChildren = new ArrayList(0);
    private final LiteContactNotification _liteNotif;
    private LiteCICustomer _customer = null;
    
    public ContactableNotification(LiteContactNotification liteNotif) {
        _liteNotif = liteNotif;
    }
    
    public LiteCICustomer getContactableCustomer() throws UnknownCustomerException {
        if (_customer != null) {
            return _customer;
        }
        _customer = ContactFuncs.getCICustomer(_liteNotif.getContactID());
        if (_customer == null) {
            throw new UnknownCustomerException("Can't return LiteCustomer for contact id " + _liteNotif.getContactID());
        }
        return _customer;
    }
    
    public List getNotifications(NotificationTypeChecker checker) {
        List result;
        if (checker.validNotifcationType(_liteNotif.getNotificationCategoryID())) {
            result = new ArrayList(1);
            result.add(_liteNotif);
        } else {
            result = new ArrayList(0);
        }
        return result;
    }
    
    public String toString() {
        return _liteNotif.toString() + "[CNtID " + _liteNotif.getContactNotifID() + "]";
    }
}


