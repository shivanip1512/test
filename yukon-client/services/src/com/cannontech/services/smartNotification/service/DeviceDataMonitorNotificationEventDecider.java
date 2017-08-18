package com.cannontech.services.smartNotification.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorSmartNotificationEvent;
import com.cannontech.common.smartNotification.service.SmartNotificationSubscriptionService;

/**
 * Processes Device Data Monitor Smart Notification events and determines when to send notifications, who to send them 
 * to, and how to send them. When the service determines that a notification should be sent, it places a 
 * SmartNotificationMessageParameters object on the Smart Notification assembler queue for processing.
 */
public class DeviceDataMonitorNotificationEventDecider implements SmartNotificationDecider<DeviceDataMonitorSmartNotificationEvent> {
    private static final Logger log = YukonLogManager.getLogger(DeviceDataMonitorNotificationEventDecider.class);
    @Autowired private SmartNotificationSubscriptionService smartNotificationSubscriptionService;
    
    //for each event
    //  add event to list
    //  check if we've exceeded the max event count
    //  if so, queue a SmartNotificationMessageParameters to the assembler, update lastNotif time and nextNotif time,
    //    and clear the event list
    
    //if a timer goes off
    //  check if there have been any events
    //  if not, reset the timer
    //  if so, queue a SmartNotificationMessageParameters to the assembler, update lastNotif and nextNotif, and clear 
    //    the event list
    
    //we'll have to pull user settings from SmartNotificationSubscriptionService (cached) to know who to send to. 
    //Optimize to ignore events that nobody is subscribed to, or only daily digest.
    
    //TODO: wire this listener to the appropriate input queue
    @Override
    public void handle(DeviceDataMonitorSmartNotificationEvent event) {
        
    }

}
