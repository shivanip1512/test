package com.cannontech.common.bulk.collection.device;

import java.util.Iterator;
import java.util.Set;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionByField;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.pao.YukonDevice;

public interface DeviceGroupCollectionHelper {
    
    static final String NAME_PARAM_NAME = "name";
    static final String DESCRIPTION_PARAM_NAME = "description";
    
    /**
     * Creates a new, temporary DeviceGroup containing the specified devices. Then creates and returns a 
     * DeviceCollection based on that group. errorDevices and header are used to generate a csv file with errors.
     * @param descriptionHint If not blank, this is used as the "description" parameter for the DeviceCollection.
     */
    DeviceCollection createDeviceGroupCollection(Iterator<? extends YukonDevice> deviceIds, String descriptionHint);
    
    /**
     * Creates a new, temporary DeviceGroup containing the specified devices. Then creates and returns a 
     * DeviceCollection based on that group.
     * @param descriptionHint If not blank, this is used as the "description" parameter for the DeviceCollection.
     * 
     */
    DeviceCollection createDeviceGroupCollection(Iterator<? extends YukonDevice> devices, String descriptionHint,
            Set<String> errorDevices, String header);

    /**
     * Creates a DeviceCollectionBase from the specified device-group-based DeviceCollection.
     */
    DeviceCollectionByField buildDeviceCollectionBase(DeviceCollection deviceCollection);
    
    /**
     * Creates and returns a DeviceCollection based on the specified DeviceGroup, with no description.
     */
    DeviceCollection buildDeviceCollection(final DeviceGroup group);
    
    /**
     * Creates and returns a DeviceCollection based on the specified DeviceGroup, with the specified description.
     */
    DeviceCollection buildDeviceCollection(final DeviceGroup group, String descriptionHint);
    
    /**
     * Creates and returns a DeviceCollection based on a set of DeviceGroups.
     */
    DeviceCollection buildDeviceCollection(Set<DeviceGroup> groups);

    /**
     * Creates and returns a DeviceCollection based on the specified DeviceGroup, with the specified description.
     * errorDevices and header are used to generate a csv file with errors.
     */
    DeviceCollection buildDeviceCollection(DeviceGroup group, String descriptionHint, Set<String> errorDevices,
            String header);
    
}