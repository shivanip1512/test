package com.cannontech.common.device.groups.service;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.predicate.Predicate;

public enum DeviceGroupPredicateEnum {

    ANY(new AnyDeviceGroupPredicate()),
    MODIFIABLE(new ModifiableDeviceGroupPredicate()),
    NON_HIDDEN(new NonHiddenDeviceGroupPredicate());
    
    private Predicate<DeviceGroup> predicate;
    
    private DeviceGroupPredicateEnum(Predicate<DeviceGroup> predicate) {
        this.predicate = predicate;
    }
    
    public Predicate<DeviceGroup> getPredicate() {
        return predicate;
    }
}
