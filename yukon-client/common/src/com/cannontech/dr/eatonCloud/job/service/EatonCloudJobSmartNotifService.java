package com.cannontech.dr.eatonCloud.job.service;

public interface EatonCloudJobSmartNotifService {

    /**
     * Creates and sends smart notification if failure threshold is met
     */
    void sendSmartNotifications(int programId, int groupId, int totalDevices, int totalFailed, EatonCloudJobControlType controlType,
            String debugText);
}
