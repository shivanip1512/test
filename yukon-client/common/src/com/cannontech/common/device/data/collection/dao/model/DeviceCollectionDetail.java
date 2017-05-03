package com.cannontech.common.device.data.collection.dao.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.PointValueHolder;

public class DeviceCollectionDetail {
    private PaoIdentifier paoIdentifier;
    private String deviceName;
    private String meterSerialNumber;
    private String route;
    private int address;
    private PointValueHolder value;

    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }

    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMeterSerialNumber() {
        return meterSerialNumber;
    }

    public void setMeterSerialNumber(String meterSerialNumber) {
        this.meterSerialNumber = meterSerialNumber;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public PointValueHolder getValue() {
        return value;
    }

    public void setValue(PointValueHolder value) {
        this.value = value;
    }
}
