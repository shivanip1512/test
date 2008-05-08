package com.cannontech.common.device.groups.service;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.predicate.Predicate;

public class NonHiddenDeviceGroupPredicate implements Predicate<DeviceGroup> {

    public boolean evaluate(DeviceGroup deviceGroup) {
        return !deviceGroup.isHidden();
    }
}
