package com.cannontech.stars.dr.hardware.builder;

import com.cannontech.stars.dr.hardware.model.HardwareDto;

public interface DeviceBuilder {
    
    public void createDevice(HardwareDto hardwareDto);
    public void updateDevice(HardwareDto hardwareDto);
    public void deleteDevice(HardwareDto hardwareDto);
    
}
