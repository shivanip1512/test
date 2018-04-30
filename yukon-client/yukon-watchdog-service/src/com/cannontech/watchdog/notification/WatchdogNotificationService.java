package com.cannontech.watchdog.notification;

import java.util.List;

import com.cannontech.common.smartNotification.model.SmartNotificationEvent;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.watchdog.model.WatchdogWarnings;

public interface WatchdogNotificationService {

    /**
     * This will be called by watchdog to send notification 
     * Check status of all the services required to send notification via smartNotification. 
     * If all services are running then will send message via smartNotificaiton. 
     * If any service is down then will call internal notification component to send notification
     */
    void sendNotification(SmartNotificationEventType type, List<SmartNotificationEvent> events);
    
    /**
     * Will call assemble method.    
     */
    List<SmartNotificationEvent> assemble(List<WatchdogWarnings> warnings);
}
