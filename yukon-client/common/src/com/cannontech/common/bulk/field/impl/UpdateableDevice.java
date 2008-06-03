package com.cannontech.common.bulk.field.impl;

import com.cannontech.common.device.YukonDevice;

public class UpdateableDevice {

    private YukonDevice device;
    private YukonDeviceDto deviceDto;
    
    public YukonDevice getDevice() {
        return device;
    }
    public void setDevice(YukonDevice device) {
        this.device = device;
    }
    
    public YukonDeviceDto getDeviceDto() {
        return deviceDto;
    }
    public void setDeviceDto(YukonDeviceDto deviceDto) {
        this.deviceDto = deviceDto;
    }
}