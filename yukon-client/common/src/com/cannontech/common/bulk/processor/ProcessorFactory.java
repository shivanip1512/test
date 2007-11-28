package com.cannontech.common.bulk.processor;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

/**
 * Factory interface which generates Processors
 */
public interface ProcessorFactory {

    /**
     * Method to get a processor that will add a yukon device to a given group
     * @param group - Group to add device to
     * @return The processor
     */
    public Processor<YukonDevice> createAddYukonDeviceToGroupProcessor(final StoredDeviceGroup group);

    /**
     * Method to get a processor that will assign a device configuration to a
     * device
     * @param configuration - Configuration to assign to device
     * @return The processor
     */
    public Processor<YukonDevice> createAssignConfigurationToYukonDeviceProcessor(
            final ConfigurationBase configuration);

}