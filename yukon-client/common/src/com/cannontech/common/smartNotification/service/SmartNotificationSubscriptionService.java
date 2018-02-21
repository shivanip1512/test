package com.cannontech.common.smartNotification.service;

import java.util.List;
import java.util.Optional;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationSubscription;
import com.cannontech.user.YukonUserContext;

/**
 * Service for saving and retrieving users' Smart Notification subscriptions.
 */
public interface SmartNotificationSubscriptionService {
        
    /**
     * deletes subscription
     */
    void deleteSubscription(int id, YukonUserContext userContext);
    
    /**
     * If id is 0 creates subscription otherwise updates subscription.
     */
    int saveSubscription(SmartNotificationSubscription subscription, YukonUserContext userContext);

    /**
     * Delete all subscriptions with the specified type and identifier. The friendly name and user are supplied for 
     * event logging.
     */
    void deleteSubscriptions(SmartNotificationEventType type, String identifier, String name, YukonUserContext userContext);

    /**
     * Fetch Subscription Type and Name. Return only Type for Subscription other than Device Data monitor.
     */
    static String getSubscriptionTypeAndName(SmartNotificationSubscription subscription, List<DeviceDataMonitor> deviceDataMonitorList) {
        if (subscription.getType().equals(SmartNotificationEventType.DEVICE_DATA_MONITOR)) {
            Integer monitorId = DeviceDataMonitorEventAssembler.getMonitorId(subscription.getParameters());
            Optional<DeviceDataMonitor> deviceDataMonitor =
                deviceDataMonitorList.stream().filter(m -> m.getId().equals(monitorId)).findFirst();
            if (deviceDataMonitor.isPresent()) {
                return subscription.getType().toString() + deviceDataMonitor.get().getName();
            }
            return subscription.getType().toString();
        }
        return subscription.getType().toString();
    }
    
}

