package com.cannontech.common.device.groups.editor.dao;

import java.util.Collection;
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
    
    /**
     * Extracts the deviceId for each device and delegates to addDevicesById.
     * @param group
     * @param devices
     */
    public void addDevices(StoredDeviceGroup group, Collection<? extends YukonDevice> devices);
    
    /**
     * Add devices to the group by device id (yukon pao id). This is currently
     * the fundamental add method (all other delegate to it).
     * 
     * Duplicates are silently ignored.
     * @param group
     * @param deviceIds
     */
    public void addDevicesById(StoredDeviceGroup group, Collection<Integer> deviceIds);

    /**
     * Remove devices under group that are contained in the devices collection.
     * Devices that exist in the collection put are not a member of the group are
     * silently ignored.
     * @param group
     * @param device
     */
    public void removeDevices(StoredDeviceGroup group, YukonDevice... device);
    
    /**
     * Remove devices from the given group by device id (yukon pao id). Devices that 
     * exist in the collection put are not a member of the group are silently ignored.
     * @param group
     * @param deviceIds
     */
    public void removeDevicesById(StoredDeviceGroup group, Collection<Integer> deviceIds);
    
    /**
     * Remove devices under group that are contained in the devices collection.
     * Devices that exist in the collection put are not a member of the group are
     * silently ignored.
     * @param group
     * @param devices
     */
    public void removeDevices(StoredDeviceGroup group, Collection<? extends YukonDevice> devices);

}
