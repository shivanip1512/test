package com.cannontech.common.bulk.processor;

import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * Factory interface which generates Processors
 */
public interface ProcessorFactory {

    /**
     * Method to get a processor that will assign a device configuration to a
     * device
     * @param configuration - Configuration to assign to device
     * @param user 
     * @return The processor
     */
    Processor<SimpleDevice> createAssignConfigurationToYukonDeviceProcessor(
            final DeviceConfiguration configuration, LiteYukonUser user);
    
    /**
     * Method to get a processor that will unassign a configuration from a device
     * @return The processor
     */
    Processor<SimpleDevice> createUnassignConfigurationToYukonDeviceProcessor(LiteYukonUser user);
}