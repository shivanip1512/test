package com.cannontech.deviceReadings.model;

import java.util.List;

import com.cannontech.common.pao.attribute.model.BuiltInAttribute;

public class DeviceReading {

    private String SerialNumber;
    private String meterNumber;
    // TODO
    private int paoId;
    private String paoName;
    private List<BuiltInAttribute> attributes;

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public int getPaoId() {
        return paoId;
    }

    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }

    public String getPaoName() {
        return paoName;
    }

    public void setPaoName(String paoName) {
        this.paoName = paoName;
    }

    public List<BuiltInAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<BuiltInAttribute> attributes) {
        this.attributes = attributes;
    }

}
