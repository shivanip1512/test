package com.cannontech.stars.dr.hardware.builder.impl;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.stars.dr.hardware.builder.HardwareTypeExtensionService;
import com.google.common.collect.ImmutableSet;

public interface HardwareTypeExtensionProvider extends HardwareTypeExtensionService {

    public ImmutableSet<HardwareType> getTypes();
    
}