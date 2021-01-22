package com.cannontech.web.bulk.service;

import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.user.YukonUserContext;

public interface DeviceConfigAssignService {

    /**
     * Creates and returns collection action for device config unassign
     */
    CollectionActionResult unassign(DeviceCollection deviceCollection, YukonUserContext userContext);

    /**
     * Creates collection action for device config assign and waits until the action is completed
     */
    void assign(int configuration, SimpleDevice device, YukonUserContext userContext);

    /**
     * Creates and returns collection action for device config assign
     */
    CollectionActionResult assign(int configuration, DeviceCollection deviceCollection, YukonUserContext userContext);

    /**
     * Creates collection action for device config unassign and waits until the action is completed
     */
    void unassign(SimpleDevice device, YukonUserContext userContext);

}
