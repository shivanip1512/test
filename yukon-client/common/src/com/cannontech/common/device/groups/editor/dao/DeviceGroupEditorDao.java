package com.cannontech.common.device.groups.editor.dao;

import java.util.List;

import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.core.dao.NotFoundException;

public interface DeviceGroupEditorDao {
    public StoredDeviceGroup getRootGroup();

    public List<StoredDeviceGroup> getChildGroups(StoredDeviceGroup group);
    
    public StoredDeviceGroup getGroupByName(StoredDeviceGroup parent, String groupName);
    
    /**
     * This method will return a Group by Name.  If the Group is not found for this parent and addGroup is true, 
     *  then a new Static group will be added to parent.  If addGroup is false, then throws NotFoundException.
     * @param parent
     * @param groupName
     * @param addGroup
     * @return
     */
    public StoredDeviceGroup getGroupByName(StoredDeviceGroup parent, String groupName, boolean addGroup);
    /**
     * This find all STATIC groups that are descendants of group.
     * @param group
     * @return
     */
    public List<StoredDeviceGroup> getStaticGroups(StoredDeviceGroup group);
    
    /**
     * This find all non STATIC groups that are descendants of group.
     * @param group
     * @return
     */
    public List<StoredDeviceGroup> getNonStaticGroups(StoredDeviceGroup group);

    public StoredDeviceGroup addGroup(StoredDeviceGroup group, DeviceGroupType type,
            String groupName);

    /**
     * Method to remove a stored device group and all of its children and
     * dependencies.
     * @param group - Group to remove
     */
    public void removeGroup(StoredDeviceGroup group);

    public StoredDeviceGroup getGroupById(int groupId);
    
    /**
     * Update the group's parent and name as set on the passed in object.
     * @param group
     */
    public void updateGroup(StoredDeviceGroup group);
    
    /**
     * This is the preferred method for converting a DeviceGroup into a StoredDeviceGroup.
     * It will first try a simple cast, but if that fails it will do a full lookup on
     * group.getFullName(). This adds a bit of future-proofing.
     * @param group
     * @return
     * @throws NotFoundException If group cannot be converted to a StoredDeviceGroup
     */
    public StoredDeviceGroup getStoredGroup(DeviceGroup group) throws NotFoundException;
    
    /**
     * This returns a StoredDeviceGroup for a group name. It is quite different than 
     * calling the DeviceGroupService and should only be used if group manipulation
     * will be performed on the result.
     * @param fullName
     * @param create
     * @return
     * @throws NotFoundException If fullName doesn't represent a StoredDeviceGroup and create=false
     */
    public StoredDeviceGroup getStoredGroup(String fullName, boolean create) throws NotFoundException;
    
    /**
     * This method will return the StoredDeviceGroup for a SystemGroupEnum.
     * If only a DeviceGroup is needed, simply call 
     *   DeviceGroupService.resolveGroupName(systemGroupEnum.getFullPath())
     * as that method uses a cache and this will hit the database every time.
     * @param systemGroupEnum
     * @return
     */
    public StoredDeviceGroup getSystemGroup(SystemGroupEnum systemGroupEnum) throws NotFoundException;
}
