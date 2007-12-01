package com.cannontech.common.device.groups.editor.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public interface DeviceGroupMemberEditorDao {
    public List<YukonDevice> getChildDevices(StoredDeviceGroup group);

    /**
     * This returns only the StoredDeviceGroups which are a direct child of base and
     * have device as a direct child. This definition limits its usefulness which 
     * is why it has been replaced with getGroupMembership().
     * @param base
     * @param device
     * @return
     */
    @Deprecated
    public Set<StoredDeviceGroup> getGroups(StoredDeviceGroup base, YukonDevice device);
    
    /**
     * This is an improved version of getGroups. It returns all groups which are a descendant
     * of base and which have device as a member.
     * @param base
     * @param device
     * @return
     */
    public Set<StoredDeviceGroup> getGroupMembership(StoredDeviceGroup base, YukonDevice device);

    /**
     * Method to add a list of devices to a group (quietly ignores duplicates)
     * @param group - Group to add devices to
     * @param devices - Devices to add
     */
    public void addDevices(StoredDeviceGroup group, YukonDevice... device);
    public void addDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices);

    public void removeDevices(StoredDeviceGroup group, YukonDevice... device);
    public void removeDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices);

    public void updateDevices(StoredDeviceGroup group, YukonDevice... device);
    public void updateDevices(StoredDeviceGroup group, List<? extends YukonDevice> devices);
}
