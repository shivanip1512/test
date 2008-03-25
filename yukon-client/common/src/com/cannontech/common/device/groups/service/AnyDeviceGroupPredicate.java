package com.cannontech.common.device.groups.service;

import com.cannontech.common.util.predicate.Predicate;

import com.cannontech.common.device.groups.model.DeviceGroup;

public class AnyDeviceGroupPredicate implements Predicate<DeviceGroup> {
    
    public boolean evaluate(DeviceGroup deviceGroup) {
        return true;
    }
}
