package com.cannontech.notif.handler;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.*;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.notification.NotifType;
import com.cannontech.notif.outputs.*;
import com.cannontech.user.UserUtils;


public abstract class NotifHandler {
    private static final Logger log = YukonLogManager.getLogger(NotifHandler.class);
    
    private @Autowired OutputHandlerHelper outputHandlerHelper;

    protected void outputNotification(NotificationBuilder notif, LiteNotificationGroup lng) {
        if (lng.isDisabled()) {
            log.warn("Ignoring notification request because notification group is disabled: group=" + lng);
            return;
        }
        List<Contactable>  contactables = NotifMapContactable.getContactablesForGroup(lng);
        outputHandlerHelper.handleNotification(notif, contactables);
    }

    static public void logNotificationActivity(String action, boolean success, LiteContactNotification destination, Contactable contactable, NotifType notifType, Object forDescription) {
        action += " (" + notifType + ")";
        String state = success ? "succeeded" : "failed";
        String description = "Notification " + state + " to " + destination + " of " + contactable + " for " + forDescription;
        
        int userId = UserUtils.USER_YUKON_ID;
        int energyCompanyId = contactable.getEnergyCompany().getEnergyCompanyID();
        int customerId;
        try {
            customerId = contactable.getCustomer().getCustomerID();
        } catch (UnknownCustomerException e) {
            log.debug("Couldn't find customer for contactable while writing to ActivityLogger: " + contactable);
            customerId = -1;
        }
        ActivityLogger.logEvent(userId, -1, energyCompanyId, customerId, -1, action, description);
    }

    static public void logNotificationStatus(String action, boolean success, Contactable contactable, NotifType notifType, Object forDescription) {
        action += " (" + notifType + ")";
        String state = success ? "succeeded" : "failed";
        String description = "Whole notification " + state + " to " + contactable + " for " + forDescription;
        
        int userId = UserUtils.USER_YUKON_ID;
        int energyCompanyId = contactable.getEnergyCompany().getEnergyCompanyID();
        int customerId;
        try {
            customerId = contactable.getCustomer().getCustomerID();
        } catch (UnknownCustomerException e) {
            log.debug("Couldn't find customer for contactable while writing to ActivityLogger: " + contactable);
            customerId = -1;
        }
        ActivityLogger.logEvent(userId, -1, energyCompanyId, customerId, -1, action, description);
    }
    

}
