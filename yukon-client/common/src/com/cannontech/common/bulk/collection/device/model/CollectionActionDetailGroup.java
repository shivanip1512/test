package com.cannontech.common.bulk.collection.device.model;

import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public class CollectionActionDetailGroup {

    private DeviceCollection devices;
    private StoredDeviceGroup group;

    public CollectionActionDetailGroup(DeviceCollection devices, StoredDeviceGroup group) {
        this.devices = devices;
        this.group = group;
    }

    public DeviceCollection getDevices() {
        return devices;
    }

    public StoredDeviceGroup getGroup() {
        return group;
    }
}
