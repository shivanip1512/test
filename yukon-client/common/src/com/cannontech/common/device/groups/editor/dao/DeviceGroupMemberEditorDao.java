package com.cannontech.common.device.groups.editor.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;

public interface DeviceGroupMemberEditorDao {
    public List<YukonDevice> getChildDevices(StoredDeviceGroup group);
    public Set<StoredDeviceGroup> getGroups(StoredDeviceGroup base, YukonDevice device);
    public void addDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices);
    public void removeDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices);
    public void updateDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices);
}
