package com.cannontech.stars.dr.jms.notification;

import org.joda.time.Instant;

import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.jms.notification.message.DRNotificationDataMessage;
import com.cannontech.stars.dr.optout.model.OptOutEvent;

public interface DRNotificationMessagingService {

    /**
     * Pushing enrollment messages to queue ("yukon.notif.obj.dr.DRNotificationMessage")
     */
    public void sendEnrollmentNotification(LMHardwareControlGroup controlInformation);

    /**
     * Pushing unenrollment messages to queue ("yukon.notif.obj.dr.DRNotificationMessage")
     */
    public void sendUnenrollmentNotification(LMHardwareControlGroup controlInformation);

    /**
     * Pushing start optout messages to queue ("yukon.notif.obj.dr.DRNotificationMessage")
     */
    public void sendStartOptOutNotification(Integer inventoryId, OptOutEvent event);

    /**
     * Pushing stop optout messages to queue ("yukon.notif.obj.dr.DRNotificationMessage")
     */
    public void sendStopOptOutNotification(Integer inventoryId, Instant stopDate);
    
    /**
     * Pushing data messages to queue ("yukon.notif.obj.dr.DRNotificationMessage")
     */
    public void sendDataMessageNotification(DRNotificationDataMessage drNotificationDataMessage);
    

}
