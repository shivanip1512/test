package com.cannontech.common.smartNotification.service;

import java.util.List;
import java.util.Optional;

import com.cannontech.amr.deviceDataMonitor.model.DeviceDataMonitor;
import com.cannontech.common.smartNotification.model.DeviceDataMonitorEventAssembler;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.smartNotification.model.SmartNotificationFrequency;
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
     * Fetch Subscription Type and Name in case of Device Data Monitor and Asset Import. Otherwise return only Type for Subscription.
     */
    static String getSubscriptionTypeAndName(SmartNotificationSubscription subscription, List<DeviceDataMonitor> deviceDataMonitorList) {
        if (subscription.getType() == SmartNotificationEventType.DEVICE_DATA_MONITOR) {
            Integer monitorId = DeviceDataMonitorEventAssembler.getMonitorId(subscription.getParameters());
            Optional<DeviceDataMonitor> deviceDataMonitor =
                deviceDataMonitorList.stream().filter(m -> m.getId().equals(monitorId)).findFirst();
            if (deviceDataMonitor.isPresent()) {
                return subscription.getType().toString() + deviceDataMonitor.get().getName();
            }
            return subscription.getType().toString();
        } else if (subscription.getType() == SmartNotificationEventType.ASSET_IMPORT) {
            return subscription.getType().toString() + subscription.getParameters().get("assetImportResultType");
        }
        return subscription.getType().toString();
    }

    /**
     * Get Frequency of Subscription . For Daily Smart Notification Subscription also considering the time for sorting.
     */
    static String getFrequency(SmartNotificationSubscription subscription) {
        if (subscription.getFrequency() == SmartNotificationFrequency.DAILY_DIGEST) {
            String sendTime = subscription.getParameters().get("sendTime").toString();
            //Expected sendTime Format in HH:mm . where mm is always 00, and getting HH in 24 hour format.
            String[] sendTimeSplittedArray = sendTime.split(":");
            int hourAsciiValue = Integer.parseInt(sendTimeSplittedArray[0]) + 65;
            return subscription.getFrequency().toString() + (char) hourAsciiValue;
        }
        return subscription.getFrequency().toString();
    }
}

