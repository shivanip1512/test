package com.cannontech.common.device.groups.editor.dao;

import java.util.List;

import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public interface DeviceGroupEditorDao {
    public StoredDeviceGroup getRootGroup();
    public List<StoredDeviceGroup> getChildGroups(StoredDeviceGroup group);
    public StoredDeviceGroup addGroup(StoredDeviceGroup group, DeviceGroupType type, String groupName);
    public void removeGroup(StoredDeviceGroup group);
}
