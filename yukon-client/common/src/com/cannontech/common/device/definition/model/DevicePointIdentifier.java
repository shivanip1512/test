package com.cannontech.common.device.definition.model;

import com.cannontech.common.device.YukonDevice;

public class DevicePointIdentifier {
    private YukonDevice yukonDevice;
    private PointIdentifier devicePointIdentifier;
    
    public DevicePointIdentifier(YukonDevice yukonDevice,
            PointIdentifier devicePointIdentifier) {
        super();
        this.yukonDevice = yukonDevice;
        this.devicePointIdentifier = devicePointIdentifier;
    }
    public YukonDevice getYukonDevice() {
        return yukonDevice;
    }
    public void setYukonDevice(YukonDevice yukonDevice) {
        this.yukonDevice = yukonDevice;
    }
    public PointIdentifier getDevicePointIdentifier() {
        return devicePointIdentifier;
    }
    public void setDevicePointIdentifier(PointIdentifier devicePointIdentifier) {
        this.devicePointIdentifier = devicePointIdentifier;
    }
    
}
