package com.cannontech.notif.outputs;

import java.util.List;
import java.util.TimeZone;

import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.notification.NotifType;

public class Contactable {

    protected ContactableBase _contactableBase;
    
    public Contactable() {
    }
    
    public Contactable(ContactableBase base) {
        _contactableBase = base;
    }

    /**
     * @param types
     *            a Set of contact notification types
     * @return A List of LiteContactNotification
     */
    public List getNotifications(NotificationTypeChecker checker) {
        return _contactableBase.getNotifications(checker);
    }

    /**
     * Returns the TimeZone object of the parent customer of this object. This
     * is different depeneding on what this object is constructed from:
     * Customer, Contact of NotifDestination.
     * 
     * If the Contactable doesn't have an associated Customer, the default TimeZone
     * for the Energy Company will be returned.
     * 
     * @return a TimeZone object
     */
    public TimeZone getTimeZone() {
        try {
            String tzString = _contactableBase.getContactableCustomer()
                    .getTimeZone();
            return TimeZone.getTimeZone(tzString);
        } catch (UnknownCustomerException e) {
            return EnergyCompanyFuncs.getEnergyCompanyTimeZone(getEnergyCompany());
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
    
    /**
     * Returns true if this Contactable supports being notifications of the
     * indicated method. The possible method types are listed in the NotifMap
     * class.
     * 
     * @param notificationMethod
     * @return
     */
    public boolean supportsNotificationMethod(NotifType notificationMethod) {
        return true;
    }


}
