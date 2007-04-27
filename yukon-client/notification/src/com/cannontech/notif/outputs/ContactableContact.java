package com.cannontech.notif.outputs;

import java.util.*;

import com.cannontech.common.util.NotificationTypeChecker;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.*;

public class ContactableContact extends ContactableBase {
    List<ContactableBase> _notifList = new LinkedList<ContactableBase>();
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
        _customer = DaoFactory.getContactDao().getCICustomer(_liteContact.getContactID());
        if (_customer == null) {
            throw new UnknownCustomerException("Can't return LiteCustomer for contact id " + _liteContact.getContactID());
        }
        return _customer;
    }
    
    public List<LiteContactNotification> getNotifications(NotificationTypeChecker checker) {
        List<LiteContactNotification> result = new LinkedList<LiteContactNotification>();
        for (Iterator iter = _notifList.iterator(); iter.hasNext();) {
            ContactableBase contactable = (ContactableBase) iter.next();
            result.addAll(contactable.getNotifications(checker));
        };
        return result;
    }
    
    public String toString() {
        return _liteContact.toString() + "[contactId=" + _liteContact.getContactID() + "]";
    }
}
