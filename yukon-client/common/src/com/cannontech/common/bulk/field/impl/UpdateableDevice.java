package com.cannontech.common.bulk.field.impl;

import com.cannontech.common.device.model.SimpleDevice;

public class UpdateableDevice {

    private SimpleDevice device;
    private YukonDeviceDto deviceDto;
    
    public SimpleDevice getDevice() {
        return device;
    }
    public void setDevice(SimpleDevice device) {
        this.device = device;
    }
    
    public YukonDeviceDto getDeviceDto() {
        return deviceDto;
    }
    public void setDeviceDto(YukonDeviceDto deviceDto) {
        this.deviceDto = deviceDto;
    }
}