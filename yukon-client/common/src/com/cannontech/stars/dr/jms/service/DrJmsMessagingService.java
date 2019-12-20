package com.cannontech.stars.dr.jms.service;

import org.joda.time.Instant;

import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.jms.message.DrAttributeDataJmsMessage;
import com.cannontech.stars.dr.optout.model.OptOutEvent;

public interface DrJmsMessagingService {

    /**
     * Pushing enrollment messages to queue ("yukon.notif.obj.dr.DRNotificationMessage")
     */
    public void publishEnrollmentNotice(LMHardwareControlGroup controlInformation);

    /**
     * Pushing unenrollment messages to queue ("yukon.notif.obj.dr.DRNotificationMessage")
     */
    public void publishUnEnrollmentNotice(LMHardwareControlGroup controlInformation);

    /**
     * Pushing start optout messages to queue ("yukon.notif.obj.dr.DRNotificationMessage")
     */
    public void publishStartOptOutNotice(Integer inventoryId, OptOutEvent event);

    /**
     * Pushing stop optout messages to queue ("yukon.notif.obj.dr.DRNotificationMessage")
     */
    public void publishStopOptOutNotice(Integer inventoryId, Instant stopDate);
    
    /**
     * Pushing data messages to queue ("yukon.notif.obj.dr.DRNotificationMessage")
     */
    public void publishAttributeDataMessageNotice(DrAttributeDataJmsMessage drNotificationDataMessage);
    

}
