package com.cannontech.common.device.groups.service;
import org.apache.commons.collections.Predicate;

import com.cannontech.common.device.groups.model.DeviceGroup;

    public class ModifiableDeviceGroupPredicate implements Predicate, DeviceGroupPredicate {

    public ModifiableDeviceGroupPredicate() {
        super();
    }
    
    public boolean evaluate(Object obj) {
        if (!(obj instanceof DeviceGroup)) {
            return false;
        }
        DeviceGroup deviceGroup = (DeviceGroup) obj;
        return deviceGroup.isModifiable();
    }
    
}
