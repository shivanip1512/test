package com.cannontech.common.device.groups.editor.dao;

import java.util.List;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.model.StaticDeviceGroup;

public interface DeviceGroupEditorDao {
    public StaticDeviceGroup getRootGroup();
    public List<YukonDevice> getChildDevices(StaticDeviceGroup group);
    public List<StaticDeviceGroup> getChildGroups(StaticDeviceGroup group);
    public StaticDeviceGroup addGroup(StaticDeviceGroup group, String groupName);
    public void addDevices(StaticDeviceGroup group, List<? extends YukonDevice> devices);
    public void removeDevices(StaticDeviceGroup group, List<? extends YukonDevice> devices);
    public void updateDevices(StaticDeviceGroup group, List<? extends YukonDevice> devices);
}
