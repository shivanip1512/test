package com.cannontech.common.device.groups.editor.dao;

import java.util.List;

import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
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
     * dependancies.
     * @param group - Group to remove
     */
    public void removeGroup(StoredDeviceGroup group);

    /**
     * Method to change a group's parent group
     * @param group - Group to move
     * @param parentGroup - New parent for the given group
     */
    public void moveGroup(StoredDeviceGroup group, StoredDeviceGroup parentGroup);

    public StoredDeviceGroup getGroupById(int groupId);
    
    public void updateGroup(StoredDeviceGroup group);
    
    /**
     * This method will return the StoredDeviceGroup for a SystemGroupEnum.
     * If only a DeviceGroup is needed, simply call 
     *   DeviceGroupService.resolveGroupName(systemGroupEnum.getFullPath())
     * as that method uses a cache and this will hit the database every time.
     * @param systemGroupEnum
     * @return
     */
    public StoredDeviceGroup getSystemGroup(SystemGroupEnum systemGroupEnum);
}
