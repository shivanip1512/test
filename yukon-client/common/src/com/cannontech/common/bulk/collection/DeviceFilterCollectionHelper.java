package com.cannontech.common.bulk.collection;

import java.util.Iterator;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.device.groups.model.DeviceGroup;

public interface DeviceFilterCollectionHelper {
    public DeviceCollection createDeviceGroupCollection(Iterator<Integer> deviceIds);
    
    public DeviceCollection buildDeviceCollection(final DeviceGroup group);
}
