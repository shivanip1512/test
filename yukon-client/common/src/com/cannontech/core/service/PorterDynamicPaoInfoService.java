package com.cannontech.core.service;

import org.joda.time.Duration;
import org.joda.time.Instant;

public interface PorterDynamicPaoInfoService {
    
    class VoltageProfileDetails {
        public Instant enabledUntil;
        public Duration profileInterval;
    }

    /**
     * Retrieves the programming progress for the specified device IDs. 
     * @param deviceId the device ID to query for.
     * @return The programming progress.  Valid values are from 0-100.  The value can be null if the device is not in progress.
     */
    Double getProgrammingProgress(Integer deviceId);
    
    /**
     * Retrieves the voltage profile details from Porter for the given device.
     * @param paoId the device to query for.
     * @return the details message, or null if there was an error.  Any of the detail message fields may be null 
     * if Porter does not have the DynamicPaoInfo for a given value.
     */
    VoltageProfileDetails getVoltageProfileDetails(int paoId);

    /**
     * Retrieves the MCT-430's IED load profile rate.
     * @param paoId
     * @return the load profile rate, or null if none. 
     */
    Duration getMctIedLoadProfileInterval(int paoId);
}
