package com.cannontech.common.bulk.collection.device;

import java.util.Iterator;

import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionByField;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.pao.YukonDevice;

public interface DeviceGroupCollectionHelper {
    public static final String NAME_PARAM_NAME = "name";
    public static final String DESCRIPTION_PARAM_NAME = "description";
    
    /**
     * Creates a new, temporary DeviceGroup containing the specified devices. Then creates and returns a 
     * DeviceCollection based on that group.
     * @param descriptionHint If not blank, this is used as the "description" parameter for the DeviceCollection.
     */
    public DeviceCollection createDeviceGroupCollection(Iterator<? extends YukonDevice> deviceIds, String descriptionHint);

    /**
     * Creates a DeviceCollectionBase from the specified device-group-based DeviceCollection.
     */
    public DeviceCollectionByField buildDeviceCollectionBase(DeviceCollection deviceCollection);
    
    /**
     * Creates and returns a DeviceCollection based on the specified DeviceGroup, with no description.
     */
    public DeviceCollection buildDeviceCollection(final DeviceGroup group);
    
    /**
     * Creates and returns a DeviceCollection based on the specified DeviceGroup, with the specified description.
     */
    public DeviceCollection buildDeviceCollection(final DeviceGroup group, String descriptionHint);
}
