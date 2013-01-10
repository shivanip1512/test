package com.cannontech.common.device.groups.service;

import java.util.concurrent.TimeUnit;

import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public interface TemporaryDeviceGroupService {

    /**
     * Create a Device Group under TEMPORARYGROUPS group.
     * Group will be scheduled to be deleted after 24 hours.
     * @return The temporary group.
     */
    public StoredDeviceGroup createTempGroup();
    /**
     * Create a Device Group under TEMPORARYGROUPS group.
     * Group will be scheduled to be deleted after 24 hours.
     * @param groupName If blank or null, a random UUID will be used.
     * @return The temporary group.
     */
    public StoredDeviceGroup createTempGroup(String groupName);
    
    /**
     * Create a Device Group under TEMPORARYGROUPS group.
     * Allows user to specify when the group should be deleted.
     * @param groupName If blank or null, a random UUID will be used.
     * @param deleteDelay 
     * @param deleteDelayUnit
     * @return The temporary group.
     */
    public StoredDeviceGroup createTempGroup(String groupName, int deleteDelay, TimeUnit deleteDelayUnit);
        
    /**
     * This method schedules temporary device groups deletion.
     */
    public void scheduleTempGroupsDeletion();
    
}
