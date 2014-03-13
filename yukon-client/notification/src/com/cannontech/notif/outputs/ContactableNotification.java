package com.cannontech.notif.outputs;

import java.util.*;

import com.cannontech.common.util.NotificationTypeChecker;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.spring.YukonSpringHook;

public class ContactableNotification extends ContactableBase {
    List _noChildren = new ArrayList(0);
    private final LiteContactNotification _liteNotif;
    private LiteCICustomer _customer = null;
    
    public ContactableNotification(LiteContactNotification liteNotif) {
        _liteNotif = liteNotif;
    }
    
    @Override
    public LiteCICustomer getContactableCustomer() throws UnknownCustomerException {
        if (_customer != null) {
            return _customer;
        }
        _customer = YukonSpringHook.getBean(ContactDao.class).getCICustomer(_liteNotif.getContactID());
        if (_customer == null) {
            throw new UnknownCustomerException("Can't return LiteCustomer for contact id " + _liteNotif.getContactID());
        }
        return _customer;
    }
    
    @Override
    public List<LiteContactNotification> getNotifications(NotificationTypeChecker checker) {
        List<LiteContactNotification> result;
        if (checker.validNotifcationType(_liteNotif.getContactNotificationType())) {
            result = new ArrayList<LiteContactNotification>(1);
            result.add(_liteNotif);
        } else {
            result = Collections.emptyList();
        }
        return result;
    }
    
    @Override
    public String toString() {
        return _liteNotif.toString() + "[contactNotificationId=" + _liteNotif.getContactNotifID() + "]";
    }
}


