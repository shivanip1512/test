package com.cannontech.web.widget.meterInfo.model;

import com.cannontech.amr.meter.model.IedMeter;
import com.cannontech.amr.meter.model.YukonMeter;

public class MeterModel {
    
    private int deviceId;
    private String name;
    private String meterNumber;
    private boolean disabled;
    private Integer portId;
    
    public int getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getMeterNumber() {
        return meterNumber;
    }
    
    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }
    
    public boolean isDisabled() {
        return disabled;
    }
    
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
    
    public Integer getPortId() {
        return portId;
    }

    public void setPortId(Integer portId) {
        this.portId = portId;
    }

    public static MeterModel of(YukonMeter meter) {
        
        MeterModel model = new MeterModel();
        model.setDeviceId(meter.getDeviceId());
        model.setDisabled(meter.isDisabled());
        model.setMeterNumber(meter.getMeterNumber());
        model.setName(meter.getName());
        if (meter instanceof IedMeter) {
            model.setPortId(((IedMeter) meter).getPortId());
        }
        return model;
    }
}