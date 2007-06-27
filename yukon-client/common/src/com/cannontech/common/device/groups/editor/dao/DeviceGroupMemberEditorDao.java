package com.cannontech.common.device.groups.editor.dao;

import java.util.List;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public interface DeviceGroupMemberEditorDao {
    public List<YukonDevice> getChildDevices(StoredDeviceGroup group);
    public void addDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices);
    public void removeDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices);
    public void updateDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices);
}
