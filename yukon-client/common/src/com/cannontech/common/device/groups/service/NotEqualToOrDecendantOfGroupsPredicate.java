package com.cannontech.common.device.groups.service;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.predicate.Predicate;

public class NotEqualToOrDecendantOfGroupsPredicate implements Predicate<DeviceGroup> {

    private DeviceGroup currentGroup;
    
    public NotEqualToOrDecendantOfGroupsPredicate(DeviceGroup currentGroup) {
        this.currentGroup = currentGroup;
    }
    
    @Override
    public boolean evaluate(DeviceGroup deviceGroup) {
        return !deviceGroup.isEqualToOrDescendantOf(this.currentGroup);
    }
    
}
