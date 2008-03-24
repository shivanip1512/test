package com.cannontech.common.device.groups.service;

import org.apache.commons.collections.Predicate;

import com.cannontech.common.device.groups.model.DeviceGroup;

public class AnyDeviceGroupPredicate implements Predicate, DeviceGroupPredicate {

    public AnyDeviceGroupPredicate() {
        super();
    }
    
    public boolean evaluate(Object obj) {
        if (!(obj instanceof DeviceGroup)) {
            return false;
        }
        return true;
    }
}
