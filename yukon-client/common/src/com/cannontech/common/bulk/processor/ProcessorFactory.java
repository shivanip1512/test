package com.cannontech.common.bulk.processor;

import com.cannontech.common.bulk.collection.EditableDevice;
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

    /**
     * Method to get a processor that will delete a device
     * @return The processor
     */
    public Processor<YukonDevice> createDeleteDeviceProcessor();

    /**
     * Method to get a processor that will enable or disable a device
     * @param enable - True if the processor is to enable the device
     * @return The processor
     */
    public Processor<YukonDevice> createEnableDisableProcessor(
            final boolean enable);

    /**
     * Method to get a processor that will update the route for a device
     * @param routeId - Id of route to change device to
     * @return The processor
     */
    public Processor<YukonDevice> createUpdateRouteProcessor(final int routeId);

    /**
     * Method to get a processor that will change the type of a device
     * @param type - Type to change device to
     * @return The processor
     */
    public Processor<YukonDevice> createChangeTypeProcessor(final int type);

    /**
     * Method to get a processor that will edit a device
     * @return The processor
     */
    public Processor<EditableDevice> createEditDeviceProcessor();

}