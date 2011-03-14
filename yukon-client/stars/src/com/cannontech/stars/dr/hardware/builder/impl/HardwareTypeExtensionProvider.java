package com.cannontech.stars.dr.hardware.builder.impl;

import com.cannontech.common.inventory.HardwareType;
import com.cannontech.stars.dr.hardware.builder.HardwareTypeExtensionService;

public interface HardwareTypeExtensionProvider extends HardwareTypeExtensionService {

    public HardwareType getType();
    
}
