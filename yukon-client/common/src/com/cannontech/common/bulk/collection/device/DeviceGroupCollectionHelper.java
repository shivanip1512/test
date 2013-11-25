package com.cannontech.common.bulk.collection.device;

import java.util.Iterator;

import com.cannontech.common.bulk.collection.device.persistable.FieldBasedCollectionPersistable;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.pao.YukonDevice;

public interface DeviceGroupCollectionHelper {
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    
    public DeviceCollection createDeviceGroupCollection(Iterator<? extends YukonDevice> deviceIds, String descriptionHint);

    /**
     * Creates a DeviceCollectionPersistable from the specified device-group-based DeviceCollection.
     */
    public FieldBasedCollectionPersistable buildDeviceCollectionPersistable(DeviceCollection deviceCollection);
    public DeviceCollection buildDeviceCollection(final DeviceGroup group);
    public DeviceCollection buildDeviceCollection(final DeviceGroup group, String descriptionHint);
}
