package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.List;
import java.util.Set;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.core.dao.NotFoundException;

/**
 * This is the standard interface that all DeviceGroup providers must implement.
 * Implementers of this interface can extend the DeviceGroupProviderBase to reduce
 * the number of methods that must be implemented (all though for best performance,
 * each method will most likely need to be implemented individually). Throughout this 
 * interface, there is a consistent naming convention that must be followed if new 
 * methods are added. This convention is best illustrated by looking at the two 
 * most important methods:
 * 
 *    getDevices(DeviceGroup group)
 *    getChildDevices(DeviceGroup group)
 *    
 * The method with the word "child" in it should only return devices that are 
 * direct children of the given group. In this context, "child" and "direct child"
 * are used interchangeably and are assumed to mean there are no additional group
 * layers between the device and the group. The other method, getDevices, is
 * meant to work in a recursive fashion and should not only return everything
 * returned by getChildDevices, but also every device contained in a sub group
 * of group. In other words, it returns all of the devices that are descendants
 * of group.
 * 
 * For example, if device A is contained in group /foo/bar, the following
 * would be true (assumes String -> DeviceGroup conversion):
 * 
 *    getDevices(/foo).contains(A);
 *    getChildDevices(/foo).contains(A) == false;
 *    getDevices(/foo/bar).contains(A);
 *    getChildDevices(/foo/bar).contains(A);
 *    
 * Most of the methods in this interface are expected to operate on a "base" 
 * (which is implicit if they just accept a group). At first this may seem 
 * strange, but it is integral to the whole provider delegation model
 * that is used by the DeviceGroupProviderDaoMain class.
 */
public interface DeviceGroupProvider {
    /**
     * Returns a list of the IDs of each device that is a descendant of the given
     * group. The implementation of this may be considerably faster than calling
     * getDevices() if only the ID is needed.
     * 
     * @param group - The device group that the devices must be a descendant of
     * @return - A list of device IDs (the order is arbitrary)
     */
    public List<Integer> getDeviceIds(DeviceGroup group);
    
    /**
     * Returns a list of the YukonDevices for each device that is a descendant of the given
     * group. The implementation of this may be faster than calling getDeviceIds() if the
     * YukonDevice object is needed.
     * 
     * @param group
     * @return - A list of YukonDevices (the order is arbitrary)
     */
    public List<YukonDevice> getDevices(DeviceGroup group);
    
    /**
     * This method gets a count of all of the devices that are a descendant of the
     * given group.
     * 
     * @param group - Group to get device count for
     * @return - Count of all devices in group (including child groups)
     */
    public int getDeviceCount(DeviceGroup group);

    /**
     * This method gets a count of all of the devices that are a direct child
     * of the given group.
     * 
     * @param group - Group to get device count for
     * @return - Count of all devices in group (not including child groups)
     */
    public int getChildDeviceCount(DeviceGroup group);
    
    /**
     * Returns an SQL fragment that can be added to the middle of an existing SQL WHERE 
     * clause. This fragment should restrict the SQL statement to only apply to Devices
     * that are direct children of this group (for the more standard recursive behavior,
     * use the getDeviceGroupSqlWhereClause() method). For example, an implementation
     * may simply return a list of the deviceIds that are in the group. If the identifier
     * was "deviceId" and the group contained the devices with the ids 1,2,3,4, and 5, 
     * this method could return the following:
     *     deviceId in (1,2,3,4,5)
     *     
     * Alternatively, this method could return an SQL statement with a nested query or
     * with several clauses AND'd or OR'd together.
     * 
     * Note: these SQL fragments are meant to be used "immediately". They should not
     * be cached for any length of time exceeding that normally required to process
     * a single request.
     * 
     * @param group - The device group that the devices must be a immediate child of
     * @param identifier - The local name for the deviceId/yukonPaobjectId column
     * @return - An SQL WHERE clause fragment
     */
    public String getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier);    
    
    /**
     * Returns an SQL fragment that can be added to the middle of an existing SQL WHERE 
     * clause. This fragment should restrict the SQL statement to only apply to Devices
     * that are descendants of this group (for the direct children only,
     * use the getChildDeviceGroupSqlWhereClause() method). For example, an implementation
     * may simply return a list of the deviceIds that are in the group. If the identifier
     * was "deviceId" and the group (or its sub groups) contained the devices with the ids 
     * 1,2,3,4, and 5, this method could return the following:
     *     deviceId in (1,2,3,4,5)
     *     
     * Alternatively, this method could return an SQL statement with a nested query or
     * with several clauses AND'd or OR'd together.
     * 
     * Note: these SQL fragments are meant to be used "immediately". They should not
     * be cached for any length of time exceeding that normally required to process
     * a single request.
     * 
     * @param group - The device group that the devices must be a descendant of
     * @param identifier - The local name for the deviceId/yukonPaobjectId column
     * @return - An SQL WHERE clause fragment
     */
    public String getDeviceGroupSqlWhereClause(DeviceGroup group, String identifier);
    
    /**
     * Returns a list of all groups that are direct children of the given group.
     * 
     * @param group - The device group that the returned groups must be a direct child of
     * @return - An unmodifiable list of DeviceGroups that are direct children of group
     */
    public List<DeviceGroup> getChildGroups(DeviceGroup group);
    
    /**
     * Returns a list of all groups that are descendants of the given group.
     * 
     * @param group - The device group that the returned group must be descendants of
     * @return - An unmodifiable list of DeviceGroups that are descendants of group
     */
    public List<DeviceGroup> getGroups(DeviceGroup group);

    /**
     * Returns a list of the YukonDevices for each device that is a direct child of 
     * the given group. The implementation of this may be faster than calling 
     * getDeviceIds() if the YukonDevice object is needed.
     * 
     * @param group - The device group that the returned devices must be children of
     * @return - An unmodifiable list of YukonDevices (the order is arbitrary)
     */
    public List<YukonDevice> getChildDevices(DeviceGroup group);

    /**
     * Returns the DeviceGroup that is a child of base and has a name (not full name)
     * that is equal to groupName. This is the fundamental method that 
     * DeviceGroupService.resovleGroupName() is built on, but provides little functionality
     * otherwise.
     * 
     * The following should always be true if no Exception is thrown for any DeviceGroup i
     * and any String j:
     * 
     * getGroup(i,j).equals(i.getFullNameInternal() + "/" + j);
     * getGroup(i,j).getName().equals(j);
     * getGroup(i,j).getParent.equals(i);
     * 
     * @param base - The group to look under
     * @param groupName - The name of the group (short name, not full)
     * @return - A device group
     * @throws NotFoundException
     */
    public DeviceGroup getGroup(DeviceGroup base, String groupName) throws NotFoundException;

    /**
     * Tests if the device is a descendant of deviceGroup. The following should be true
     * for any DeviceGroup i and YukonDevice j:
     * 
     * isDeviceInGroup(i,j) == getDevices(i).contains(j);
     * 
     * @param deviceGroup - The group to look under
     * @param device - The device
     * @return - True if the device is found
     */
    public boolean isDeviceInGroup(DeviceGroup deviceGroup, YukonDevice device);

    /**
     * Returns a Set of DeviceGroups that are descendants of base and contain device.
     * Implementations of this method should just return DeviceGroups of which the device
     * is a direct child.
     * 
     * @param base - The group to look under
     * @param device - The device
     * @return - A Set of groups under base which contain device
     */
    public Set<DeviceGroup> getGroupMembership(DeviceGroup base, YukonDevice device);

}
