package com.cannontech.common.device.definition.model;

import com.cannontech.common.device.model.SimpleDevice;

public class DevicePointIdentifier {
    private SimpleDevice yukonDevice;
    private PointIdentifier devicePointIdentifier;
    
    public DevicePointIdentifier(SimpleDevice yukonDevice,
            PointIdentifier devicePointIdentifier) {
        super();
        this.yukonDevice = yukonDevice;
        this.devicePointIdentifier = devicePointIdentifier;
    }
    public SimpleDevice getYukonDevice() {
        return yukonDevice;
    }
    public void setYukonDevice(SimpleDevice yukonDevice) {
        this.yukonDevice = yukonDevice;
    }
    public PointIdentifier getDevicePointIdentifier() {
        return devicePointIdentifier;
    }
    public void setDevicePointIdentifier(PointIdentifier devicePointIdentifier) {
        this.devicePointIdentifier = devicePointIdentifier;
    }
    
}
