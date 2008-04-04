package com.cannontech.common.device.groups.service;

import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;

public interface CopyDeviceGroupService {

    public void copyGroupAndDevicesToGroup(DeviceGroup fromGroup, StoredDeviceGroup toParent);
}
