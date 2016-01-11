package com.cannontech.common.device.groups.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * This is the primary interface that should be used for determining what devices
 * are in a group. This could potentially be determined by using the Dao or the EditorDao, 
 * but that should be avoided.
 */
public interface DeviceGroupService {
    
    public static String ROOT = "/";
    /**
     * Group names are specified with a slash-separated syntax such as
     *   /Meters/Billing/Week 1
     * All group names must begin with a /. 
     * @param groupName
     * @return
     * @throws NotFoundException
     */
    public DeviceGroup resolveGroupName(String groupName) throws NotFoundException;
    
    /**
     * Attempt to resolve the DeviceGroup for the given name. If the group cannot be found, null is returned.
     * resolveGroupName() is appropriate for most cases where a group is expected to exist.
     * This method is shortcut to catching the NotFoundException thrown by resolveGroupName() and returning a null.
     * @param groupName
     * @return
     */
    public DeviceGroup findGroupName(String groupName);
    
    /**
     * Calls resolveGroupName internally on each entry. In addition, child groups
     * of other entries in the set are removed (if the input is '/Meters' and 'Meters/Billing',
     * the result set will only include the meter group for '/Meters').
     * @param groupNames
     * @return
     * @throws NotFoundException
     */
    public Set<? extends DeviceGroup> resolveGroupNames(Collection<String> groupNames) throws NotFoundException;
    
    
    /**
     * This is the same as calling resolveGroupName("/");
     * @return
     */
    public DeviceGroup getRootGroup();
    
    /**
     * Same as calling getDevices(), but returns just the ids as a list of integers.
     * @param group
     * @return an unmodifiable List
     */
    public Set<Integer> getDeviceIds(Collection<? extends DeviceGroup> groups);
    
    /**
     * Returns a YukonDevice for every device contained in the DeviceGroup.
     * This method works recursively on each child group of the requested group.
     * @param group
     * @return an unmodifiable List
     */
    public Set<SimpleDevice> getDevices(Collection<? extends DeviceGroup> groups);
    
    /**
     * Returns a YukonDevice for every device contained in the DeviceGroup.
     * This method works recursively on each child group of the requested group.
     * Only returns number of devices up to maxSize. Set returned has a stable ordering.
     * @param groups
     * @param maxSize
     * @return
     */
    public Set<SimpleDevice> getDevices(Collection<? extends DeviceGroup> groups, int maxSize);
    
    /**
     * Returns a count of every device contained in the DeviceGroup.
     * This method works recursively on each child group of the requested group.
     * @param group
     * @return an unmodifiable List
     */
    public int getDeviceCount(Collection<? extends DeviceGroup> groups);
    
    /**
     * Returns an SqlFragmentSource that can be placed in an SQL WHERE clause
     * for the identifier field. 
     * The identifier field MUST BE of type PAObjectID or an extension of.
     * The whole SQL statement might look something like:
     *   select * from device where <identifier> in (select distinct paobjectid from yukonpaobject)
     * In the above sql, "<identifier> in (select distinct paobjectid from yukonpaobject)" may be the returned string.
     * By default, all groups are able to return a comma-separated
     * list of integers. Some group types may override this and 
     * return a nested select clause.
     * 
     * The result of this method should be used quickly. There
     * is no guarantee that the result is a dynamic query. Ideally
     * this would be called within the same transaction in which
     * the result is used.
     * @param group
     * @return
     */
    public SqlFragmentSource getDeviceGroupSqlWhereClause(Collection<? extends DeviceGroup> group, String identifier);

    /**
     * Tests if the given PAO is in the given group. If the given PAO
     * is not a device, this method will always return false.
     * @param key
     * @param pao
     */
    public boolean isDeviceInGroup(DeviceGroup group, YukonPao pao);
    
    /**
     * This method will return DeviceGroup for a SystemGroupEnum and groupName.
     * 
     * Example:
     * SystemGroupEnum = METERS
     * GroupName = My Meters
     * This method will find the full path = /Meters/My Meters and use that path to get DeviceGroup
     * 
     */
    public DeviceGroup resolveGroupName(SystemGroupEnum systemGroupEnum, String groupName) throws NotFoundException;

    /**
     * This method will return the DeviceGroup for a SystemGroupEnum.
     */
    public DeviceGroup resolveGroupName(SystemGroupEnum systemGroupEnum);
    
    /**
     * This method will return full path for a SystemGroupEnum. For SystemGroupEnum = METERS, /Meters/ will be returned.
     */
    public String getFullPath(SystemGroupEnum systemGroupEnum);
    
    public List<LiteCommand> getDeviceCommands(List<SimpleDevice> devices, LiteYukonUser user);

}
