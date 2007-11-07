package com.cannontech.common.device.groups.editor.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public interface DeviceGroupMemberEditorDao {
    public List<YukonDevice> getChildDevices(StoredDeviceGroup group);

    public Set<StoredDeviceGroup> getGroups(StoredDeviceGroup base, YukonDevice device);

    /**
     * Method to add a list of devices to a group (quietly ignores duplicates)
     * @param group - Group to add devices to
     * @param devices - Devices to add
     */
    public void addDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices);

    public void removeDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices);

    public void updateDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices);
}
