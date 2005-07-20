package com.cannontech.notif.outputs;

import java.util.*;

import com.cannontech.database.cache.functions.*;
import com.cannontech.database.data.lite.*;
import com.cannontech.database.data.notification.*;

public class Contactable {

    private final NotifMap _notifMap;

    private final ContactableBase _contactableBase;

    public Contactable(CustomerNotifGroupMap customerMap) {
        _notifMap = customerMap;
        LiteCICustomer liteCustomer = CustomerFuncs
                .getLiteCICustomer(customerMap.getCustomerID());
        _contactableBase = new ContactableCustomer(liteCustomer);
    }

    public Contactable(ContactNotifGroupMap contactMap) {
        _notifMap = contactMap;
        LiteContact liteContact = ContactFuncs.getContact(contactMap
                .getContactID());
        _contactableBase = new ContactableContact(liteContact);
    }

    public Contactable(NotifDestinationMap notifMap) {
        _notifMap = notifMap;
        LiteContactNotification liteNotif = ContactNotifcationFuncs
                .getContactNotification(notifMap.getRecipientID());
        _contactableBase = new ContactableNotification(liteNotif);
    }

    /**
     * @param types
     *            a Set of contact notification types
     * @return A List of LiteContactNotification
     */
    public List getNotifications(Set types) {
        return _contactableBase.getNotifications(types);
    }

    /**
     * Returns the TimeZone object of the parent customer of this object. This
     * is different depeneding on what this object is constructed from:
     * Customer, Contact of NotifDestination.
     * 
     * @return a TimeZone object
     */
    public TimeZone getTimeZone() {
        try {
            String tzString = _contactableBase.getContactableCustomer()
                    .getTimeZone();
            return TimeZone.getTimeZone(tzString);
        } catch (UnknownCustomerException e) {
            return TimeZone.getDefault();
        }

    }

    /**
     * Determines the appropriate LiteEnergyCompany by first finding the parent
     * customer. When the parent customer cannot be found, the default energy
     * company will be returned (EnergyCompanyFuncs.DEFAULT_ENERGY_COMPANY_ID).
     * 
     * @return a valid LiteEnergyCompany for this Contactable
     */
    public LiteEnergyCompany getEnergyCompany() {
        int energyCompanyID;
        try {
            energyCompanyID = _contactableBase.getContactableCustomer()
                    .getEnergyCompanyID();
        } catch (UnknownCustomerException e) {
            energyCompanyID = EnergyCompanyFuncs.DEFAULT_ENERGY_COMPANY_ID;
        }
        return EnergyCompanyFuncs.getEnergyCompany(energyCompanyID);
    }

    /**
     * Returns true if this Contactable supports being notifications of the
     * indicated method. The possible method types are listed in the NotifMap
     * class.
     * 
     * @param notificationMethod
     * @return
     */
    public boolean supportsNotificationMethod(int notificationMethod) {
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
    public static List getContactablesForGroup(LiteNotificationGroup lng) {
        LinkedList resultList = new LinkedList();
        CustomerNotifGroupMap[] customerMap = lng.getCustomerMap();
        for (int i = 0; i < customerMap.length; i++) {
            CustomerNotifGroupMap notifGroupMap = customerMap[i];
            resultList.add(new Contactable(notifGroupMap));
        }

        ContactNotifGroupMap[] contactMap = lng.getContactMap();
        for (int i = 0; i < contactMap.length; i++) {
            ContactNotifGroupMap notifGroupMap = contactMap[i];
            resultList.add(new Contactable(notifGroupMap));
        }

        NotifDestinationMap[] notifDestinationMap = lng
                .getNotifDestinationMap();
        for (int i = 0; i < notifDestinationMap.length; i++) {
            NotifDestinationMap destinationMap = notifDestinationMap[i];
            resultList.add(new Contactable(destinationMap));
        }

        return resultList;
    }

    public String toString() {
        return _contactableBase.toString();
    }

    /**
     * Return the name of the CICustomer associated with this Contactable.
     * 
     * @return name of CICustomer or "" if no CICustomer
     */
    public String getCustomerName() {
        try {
            return _contactableBase.getContactableCustomer().toString();
        } catch (UnknownCustomerException e) {
            return "";
        }
    }

}
