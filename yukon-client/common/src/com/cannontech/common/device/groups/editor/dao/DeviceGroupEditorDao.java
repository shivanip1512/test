package com.cannontech.common.device.groups.editor.dao;

import java.util.List;

import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public interface DeviceGroupEditorDao {
    public StoredDeviceGroup getRootGroup();

    public List<StoredDeviceGroup> getChildGroups(StoredDeviceGroup group);
    
    public StoredDeviceGroup getGroupByName(StoredDeviceGroup parent, String groupName);
    
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
}
