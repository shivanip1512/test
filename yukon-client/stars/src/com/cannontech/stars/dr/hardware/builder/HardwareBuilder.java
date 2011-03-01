package com.cannontech.stars.dr.hardware.builder;

import com.cannontech.common.inventory.HardwareType;

public interface HardwareBuilder extends DeviceBuilder {

    public HardwareType getType();
    
}
