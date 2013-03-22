package com.cannontech.common.device.groups.service;

import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;

public interface TemporaryDeviceGroupService {

    /**
     * Create a Device Group under TEMPORARYGROUPS group.
     * Group will be scheduled to be deleted after 24 hours.
     * @return The temporary group.
     */
    public StoredDeviceGroup createTempGroup();
        
    /**
     * This method schedules temporary device groups deletion.
     */
    public void scheduleTempGroupsDeletion();
    
}
