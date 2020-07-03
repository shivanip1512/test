package com.cannontech.common.device.groups.editor.dao;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;

public interface DeviceGroupMemberEditorDao {
    public List<SimpleDevice> getChildDevices(StoredDeviceGroup group);

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
    public void addDevices(StoredDeviceGroup group, YukonPao... yukonPao);
    
    /**
     * Add devices to the group. Illegal or duplicate devices will be silently ignored.
     */
    public void addDevices(StoredDeviceGroup group, Iterable<? extends YukonPao> yukonPaos);
    
    /**
     * Add devices to the group. Illegal or duplicate devices will be silently ignored.
     */
    public void addDevices(StoredDeviceGroup group, Iterator<? extends YukonPao> yukonPaos);
    
    /**
     * Adds device to the group. Returns 1 if the device was added, 0 if not.
     */
    int addDevice(StoredDeviceGroup group, YukonPao device);
    
    /**
     * Remove child devices under group that are contained in the devices collection.
     * Devices that exist in the collection but are not a member of the group are
     * silently ignored.
     * @param group
     * @param device
     * @return number of removed devices
     */
    public int removeDevices(StoredDeviceGroup group, YukonPao... yukonPao);
    
    /**
     * Remove child devices from the given group by device id (yukon pao id). Devices that 
     * exist in the collection but are not a member of the group are silently ignored.
     * @param group
     * @param deviceIds
     * @return number of removed devices
     */
    public int removeDevicesById(StoredDeviceGroup group, Collection<Integer> deviceIds);
    
    /**
     * Remove child devices under group that are contained in the devices collection.
     * Devices that exist in the collection but are not a member of the group are
     * silently ignored.
     * @param group
     * @param devices
     * @return number of removed devices 
     */
    public int removeDevices(StoredDeviceGroup group, Collection<? extends YukonPao> yukonPao);
    
    /**
     * Removes all child devices under group.
     * This method will NOT remove sub groups or their contents.
     * @param group
     * @param devices
     */
    public void removeAllChildDevices(StoredDeviceGroup group);

    /**
     * Determines if device is a direct child of group.
     * @param group
     * @param device
     * @return
     */
    public boolean isChildDevice(StoredDeviceGroup group, YukonPao yukonPao);

    
    /**
     * Determines if a paoId is a direct child of group.
     * @param group
     * @param device
     * @return
     */
    public boolean isChildDevice(StoredDeviceGroup group, int paoId);
}
