package com.cannontech.common.bulk.processor;

import java.util.Map;

import com.cannontech.common.device.config.model.DeviceConfigState;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;

/**
 * Factory interface which generates Processors
 */
public interface ProcessorFactory {

    /**
     * Method to get a processor that will assign a device configuration to a
     * device
     * 
     * @param configuration - Configuration to assign to device
     * @return The processor
     */
    Processor<SimpleDevice> createAssignConfigurationToYukonDeviceProcessor(DeviceConfiguration configuration,
            Map<Integer, DeviceConfigState> deviceToState, YukonUserContext userContext);

    /**
     * Method to get a processor that will unassign a configuration from a device
     * 
     * @param deviceToState - map of device ids to device config states
     * @return The processor
     */
    Processor<SimpleDevice> createUnassignConfigurationToYukonDeviceProcessor(Map<Integer, DeviceConfigState> deviceToState,
            LiteYukonUser user);
}