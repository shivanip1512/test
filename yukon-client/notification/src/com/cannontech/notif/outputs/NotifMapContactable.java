package com.cannontech.notif.outputs;

import java.util.LinkedList;
import java.util.List;

import com.cannontech.core.dao.*;
import com.cannontech.database.data.lite.*;
import com.cannontech.database.data.notification.*;
import com.cannontech.spring.YukonSpringHook;

public class NotifMapContactable extends Contactable {

    private final NotifMap _notifMap;

    public NotifMapContactable(CustomerNotifGroupMap customerMap) {
        _notifMap = customerMap;
        LiteCICustomer liteCustomer = YukonSpringHook.getBean(CustomerDao.class)
                .getLiteCICustomer(customerMap.getCustomerID());
        _contactableBase = new ContactableCustomer(liteCustomer);
    }

    public NotifMapContactable(ContactNotifGroupMap contactMap) {
        _notifMap = contactMap;
        LiteContact liteContact = YukonSpringHook.getBean(ContactDao.class).getContact(contactMap
                .getContactID());
        _contactableBase = new ContactableContact(liteContact);
    }

    public NotifMapContactable(NotifDestinationMap notifMap) {
        _notifMap = notifMap;
        LiteContactNotification liteNotif = YukonSpringHook.getBean(ContactNotificationDao.class)
                .getContactNotification(notifMap.getRecipientID());
        _contactableBase = new ContactableNotification(liteNotif);
    }
    
    public NotifMapContactable(ContactableBase base, NotifMap notifMap) {
        super(base);
        _notifMap = notifMap;
    }

    /**
     * Returns true if this Contactable supports being notifications of the
     * indicated method. The possible method types are listed in the NotifMap
     * class.
     * 
     * @param notificationMethod
     * @return
     */
    public boolean supportsNotificationMethod(NotifType notificationMethod) {
        return _notifMap.supportsMethod(notificationMethod);
    }
    
    /**
     * Returns a list of Contactables given a LiteNotificationGroup. The
     * LiteNotificationGroup can be composed of LiteCICustomers, LiteContacts,
     * and LiteContactNotifications. The resulting list will have one entry for
     * each entry in the LiteNotificationGroup.
     * 
     * @param lng
     *            The LiteNotificationGroup to use
     * @return A list of Contactable objects
     */
    public static List<Contactable> getContactablesForGroup(LiteNotificationGroup lng) {
        LinkedList<Contactable> resultList = new LinkedList<Contactable>();
        CustomerNotifGroupMap[] customerMap = lng.getCustomerMap();
        for (int i = 0; i < customerMap.length; i++) {
            CustomerNotifGroupMap notifGroupMap = customerMap[i];
            resultList.add(new NotifMapContactable(notifGroupMap));
        }
    
        ContactNotifGroupMap[] contactMap = lng.getContactMap();
        for (int i = 0; i < contactMap.length; i++) {
            ContactNotifGroupMap notifGroupMap = contactMap[i];
            resultList.add(new NotifMapContactable(notifGroupMap));
        }
    
        NotifDestinationMap[] notifDestinationMap = lng
                .getNotifDestinationMap();
        for (int i = 0; i < notifDestinationMap.length; i++) {
            NotifDestinationMap destinationMap = notifDestinationMap[i];
            resultList.add(new NotifMapContactable(destinationMap));
        }
    
        return resultList;
    }


}
