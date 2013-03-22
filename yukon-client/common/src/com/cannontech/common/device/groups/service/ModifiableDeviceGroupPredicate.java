package com.cannontech.common.device.groups.service;
import com.cannontech.common.util.predicate.Predicate;

import com.cannontech.common.device.groups.model.DeviceGroup;

public class ModifiableDeviceGroupPredicate implements Predicate<DeviceGroup> {
    
    /** 
     * Include device groups that are modifiable and are not hidden. 
     * Hidden groups should never be displayed directly to the user in any of our device group pickers or displays.
     */
    @Override
    public boolean evaluate(DeviceGroup deviceGroup) {
        return deviceGroup.isModifiable() && !deviceGroup.isHidden();
    }
}
