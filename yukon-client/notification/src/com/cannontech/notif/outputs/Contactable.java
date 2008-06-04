package com.cannontech.notif.outputs;

import java.util.*;

import com.cannontech.common.util.NotificationTypeChecker;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.*;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.i18n.ThemeUtils;
import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.user.YukonUserContext;

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
     * for the YukonUserContext will be returned.
     * 
     * @return a TimeZone object
     */
    public TimeZone getTimeZone() {
        try {
            String tzString = _contactableBase.getContactableCustomer().getTimeZone();
            return TimeZone.getTimeZone(tzString);
        } catch (UnknownCustomerException e) {
            return getYukonUserContext().getTimeZone();
        }
    }

    /**
     * Returns an appropriate YukonUserContext object of the parent customer of this object. 
     * The yukon user component will be looked up from the getContactableCustomer().
     * The locale will be set to the system default.
     * The time zone will be set to the result getTimeZone().
     * 
     */
    public YukonUserContext getYukonUserContext() {
        LiteYukonUser yukonUser = null;
        try {
            LiteCICustomer customer = _contactableBase.getContactableCustomer();
            int primaryContactID = customer.getPrimaryContactID();
            yukonUser = DaoFactory.getContactDao().getYukonUser(primaryContactID);
        } catch (UnknownCustomerException e) {
            LiteEnergyCompany energyCompany = getEnergyCompany();
            int userID;
            userID = energyCompany.getUserID();
            yukonUser = DaoFactory.getYukonUserDao().getLiteYukonUser(userID);
        }
        
        TimeZone timeZone = getTimeZone();
        SimpleYukonUserContext userContext = new SimpleYukonUserContext(yukonUser, Locale.getDefault(), timeZone, ThemeUtils.getDefaultThemeName());

        return userContext;
    }
    
    /**
     * Determines the appropriate LiteEnergyCompany by first finding the parent
     * customer. When the parent customer cannot be found, the default energy
     * company will be returned (DaoFactory.getEnergyCompanyDao().DEFAULT_ENERGY_COMPANY_ID).
     * 
     * @return a valid LiteEnergyCompany for this Contactable
     */
    public LiteEnergyCompany getEnergyCompany() {
        int energyCompanyID;
        try {
            energyCompanyID = _contactableBase.getContactableCustomer()
                    .getEnergyCompanyID();
        } catch (UnknownCustomerException e) {
            energyCompanyID = DaoFactory.getEnergyCompanyDao().DEFAULT_ENERGY_COMPANY_ID;
        }
        return DaoFactory.getEnergyCompanyDao().getEnergyCompany(energyCompanyID);
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
    
    public LiteCICustomer getCustomer() throws UnknownCustomerException {
        return _contactableBase.getContactableCustomer();
    }

}
