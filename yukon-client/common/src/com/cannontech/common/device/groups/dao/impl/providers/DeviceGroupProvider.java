package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.List;
import java.util.Set;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.core.dao.NotFoundException;

public interface DeviceGroupProvider {
    public List<Integer> getDeviceIds(DeviceGroup group);
    public List<YukonDevice> getDevices(DeviceGroup group);
    
    /**
     * This method gets a count of all of the devices in the given group and all
     * of it's child groups
     * @param group - Group to get device count for
     * @return - Count of all devices in group (including child groups)
     */
    public int getDeviceCount(DeviceGroup group);

    /**
     * This method gets a count of all of the devices in the given group
     * @param group - Group to get device count for
     * @return - Count of all devices in group (not including child groups)
     */
    public int getChildDeviceCount(DeviceGroup group);
    
    public String getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier);    
    public String getDeviceGroupSqlWhereClause(DeviceGroup group, String identifier);
    public List<? extends DeviceGroup> getChildGroups(DeviceGroup group);
    
    // Returns an unmodifiable list
    public List<YukonDevice> getChildDevices(DeviceGroup group);
    public DeviceGroup getGroup(DeviceGroup base, String groupName) throws NotFoundException;
    public boolean isDeviceInGroup(DeviceGroup deviceGroup, YukonDevice device);
    public Set<? extends DeviceGroup> getGroups(DeviceGroup base, YukonDevice device);
    
    /**
     * Method to remove any dependancies for a group.  This method will be called 
     * right before the group is removed so any cleanup that is neccessary for
     * deleting the group should be done here.
     * @param group - Group to remove
     */
    public void removeGroupDependancies(DeviceGroup group);
}
