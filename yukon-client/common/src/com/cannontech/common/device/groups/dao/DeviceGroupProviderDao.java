package com.cannontech.common.device.groups.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.impl.providers.DeviceGroupProvider;
import com.cannontech.common.device.groups.model.DeviceGroup;

/**
 * This interface inherits most of its methods from the DeviceGroupProvider.
 * However, it adds a few additional methods that would not be appropriate 
 * for each provider to implement on its own.
 */
public interface DeviceGroupProviderDao extends DeviceGroupProvider {
    /**
     * Returns the root, or "/", group.
     * 
     * @return - the DeviceGroup whose parent is null
     */
    public DeviceGroup getRootGroup();
    
    /**
     * Returns all known groups. Shortcut for getGroups(getRootGroup()).
     * 
     * @return - a list of all known groups
     */
    public List<DeviceGroup> getAllGroups();
    
    /**
     * Returns a list of all groups that contain the given device.
     * 
     * Shortcut for getGroupMembership(getRootGroup(), device);
     * 
     * @param device - the device which must be a child of each returned group
     * @return - all groups which have the device as a child
     */
    public Set<DeviceGroup> getGroupMembership(YukonDevice device);
}
