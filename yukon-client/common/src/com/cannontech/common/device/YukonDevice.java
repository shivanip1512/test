package com.cannontech.common.device;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.amr.meter.model.Meter;

public class YukonDevice {
    private int deviceId;
    private int type;
    
    public YukonDevice(int deviceId, int type) {
        super();
        this.deviceId = deviceId;
        this.type = type;
    }
    
    public YukonDevice() {
    }
    
    public int getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("deviceId", getDeviceId());
        tsc.append("type", getType());
        return tsc.toString();
    }

}
