package com.cannontech.dr.eatonCloud.job.service;

import com.cannontech.dr.eatonCloud.job.service.impl.EventSummary;

public interface EatonCloudJobSmartNotifService {

    /**
     * Creates and sends smart notification if failure threshold is met
     */
    void sendSmartNotifications(EventSummary summary, int totalDevices, int totalFailed);
}
