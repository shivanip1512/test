package com.cannontech.common.bulk.collection.device;

import java.util.Iterator;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.pao.YukonDevice;

public interface DeviceGroupCollectionHelper {
    public DeviceCollection createDeviceGroupCollection(Iterator<? extends YukonDevice> deviceIds, String descriptionHint);
    
    public DeviceCollection buildDeviceCollection(final DeviceGroup group);
    public DeviceCollection buildDeviceCollection(final DeviceGroup group, String descriptionHint);
}
