package com.cannontech.common.device.groups.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.core.dao.NotFoundException;

/**
 * This is the primary interface that should be used for determining what devices
 * are in a group. This could potentially be determined by using the Dao or the EditorDao, 
 * but that should be avoided.
 */
public interface DeviceGroupService {
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
    public Set<YukonDevice> getDevices(Collection<? extends DeviceGroup> groups);
    
    /**
     * Returns a count of every device contained in the DeviceGroup.
     * This method works recursively on each child group of the requested group.
     * @param group
     * @return an unmodifiable List
     */
    public int getDeviceCount(Collection<? extends DeviceGroup> groups);
    
    /**
     * Returns a String that can be placed in an SQL WHERE clause
     * for the identifier field. 
     * The identifier field MUST BE of type PaobjectID or an extension of.
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
    public String getDeviceGroupSqlWhereClause(Collection<? extends DeviceGroup> group, String identifier);

    /**
     * Method to get a hierarchy of the current device groups starting with the
     * given group. Predicate is evaluated to determine if child groups should be included in heirarchy.
     * @param root
     * @param predicate
     * @return
     */
    public DeviceGroupHierarchy getDeviceGroupHierarchy(DeviceGroup root, Predicate<DeviceGroup> predicate);
    
    public DeviceGroupHierarchy getDeviceGroupHierarchy(DeviceGroup root, List<? extends Predicate<DeviceGroup>> predicates);

}
