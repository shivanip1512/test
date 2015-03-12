package com.cannontech.stars.dr.hardware.model;

import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.stars.model.LiteLmHardware;

public class DeviceAndPointValue {
    
    private LiteLmHardware device;
    private SimplePointValue pointValue;
    
    private DeviceAndPointValue(LiteLmHardware device, SimplePointValue pointValue) {
        this.device = device;
        this.pointValue = pointValue;
    }
    
    public LiteLmHardware getDevice() {
        return device;
    }
    
    public SimplePointValue getPointValue() {
        return pointValue;
    }
    
    public static DeviceAndPointValue of(LiteLmHardware device, SimplePointValue pointValue) {
        return new DeviceAndPointValue(device, pointValue);
    }
    
    @Override
    public String toString() {
        return "DeviceAndPointValue [device=" + device + ", pointValue=" + pointValue + "]";
    }
    
}