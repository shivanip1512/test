package com.cannontech.notif.outputs;

import java.util.*;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NotificationTypeChecker;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.database.data.lite.*;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.i18n.ThemeUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
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
    public List<LiteContactNotification> getNotifications(NotificationTypeChecker checker) {
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
            return CtiUtilities.getValidTimeZone(tzString);
        } catch (UnknownCustomerException e) {
            return YukonSpringHook.getBean(AuthDao.class).getUserTimeZone(getLiteYukonUser());
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
        LiteYukonUser yukonUser = getLiteYukonUser();
        TimeZone timeZone = getTimeZone();
        SimpleYukonUserContext userContext = new SimpleYukonUserContext(yukonUser, Locale.getDefault(), timeZone, ThemeUtils.getDefaultThemeName());

        return userContext;
    }
    
    /**
     * Returns the liteYukonUser component which is looked up from the getContactableCustomer().
     * @return
     */
    private LiteYukonUser getLiteYukonUser() {
        LiteYukonUser yukonUser = null;
        try {
            LiteCICustomer customer = _contactableBase.getContactableCustomer();
            int primaryContactID = customer.getPrimaryContactID();
            yukonUser = YukonSpringHook.getBean(ContactDao.class).getYukonUser(primaryContactID);
        } catch (UnknownCustomerException e) {
            EnergyCompany energyCompany = getEnergyCompany();
            yukonUser = energyCompany.getUser();
        }
        return yukonUser;
    }
    /**
     * Determines the appropriate EnergyCompany by first finding the parent
     * customer. When the parent customer cannot be found, the default energy
     * company will be returned.
     * 
     * @return a valid EnergyCompany for this Contactable
     */
    public EnergyCompany getEnergyCompany() {
        int energyCompanyID;
        try {
            energyCompanyID = _contactableBase.getContactableCustomer().getEnergyCompanyID();
        } catch (UnknownCustomerException e) {
            energyCompanyID = YukonSpringHook.getBean(YukonEnergyCompanyService.class).DEFAULT_ENERGY_COMPANY_ID;
        }
        return YukonSpringHook.getBean(YukonEnergyCompanyService.class).getEnergyCompany(energyCompanyID);
    }

    @Override
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
