package com.cannontech.common.bulk.collection;

import java.util.Iterator;

import com.cannontech.common.device.groups.model.DeviceGroup;

public interface DeviceGroupCollectionHelper {
    public DeviceCollection createDeviceGroupCollection(Iterator<Integer> deviceIds, String descriptionHint);
    
    public DeviceCollection buildDeviceCollection(final DeviceGroup group);
    public DeviceCollection buildDeviceCollection(final DeviceGroup group, String descriptionHint);
}
