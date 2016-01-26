package com.cannontech.core.service;

import java.util.function.Consumer;

import org.joda.time.Duration;
import org.joda.time.Instant;

public interface PorterDynamicPaoInfoService {
    
    class VoltageProfileDetails {
        public Instant enabledUntil;
        public Duration profileInterval;
    }
    
    /**
     * Asynchronously retrieves the voltage profile details from Porter for the given device.
     * @param paoId the device to query for.
     * @param callback the consumer to be called once the service call returns.  As with the synchronous call, it will 
     * provide the details message, or null if there was an error.  Any of the detail message fields may be null if 
     * Porter does not have the DynamicPaoInfo for a given value.
     */
    void getVoltageProfileDetails(int paoId, Consumer<VoltageProfileDetails> callback);

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
