package com.cannontech.common.bulk.processor;

import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.model.SimpleDevice;

/**
 * Factory interface which generates Processors
 */
public interface ProcessorFactory {

    /**
     * Method to get a processor that will assign a device configuration to a
     * device
     * @param configuration - Configuration to assign to device
     * @return The processor
     */
    public Processor<SimpleDevice> createAssignConfigurationToYukonDeviceProcessor(
            final ConfigurationBase configuration);
    
    /**
     * Method to get a processor that will unassign a configuration from a device
     * @return The processor
     */
    public Processor<SimpleDevice> createUnassignConfigurationToYukonDeviceProcessor();
}