package com.cannontech.common.device.data.collection.dao.model;

import java.util.Date;

import com.cannontech.common.device.data.collection.dao.RecentPointValueDao.RangeType;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.PointValueQualityHolder;

public class DeviceCollectionDetail {
    private PaoIdentifier paoIdentifier;
    private String deviceName;
    private String meterNumber;
    private String route;
    private String addressSerialNumber;
    private boolean isEnabled;
    private RangeType range;
    private PointValueQualityHolder value;

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

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public PointValueQualityHolder getValue() {
        return value;
    }

    public void setValue(PointValueQualityHolder value) {
        this.value = value;
    }

    public Date getDateTime() {
        if (value != null) {
            return value.getPointDataTimeStamp();
        }
        return null;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getAddressSerialNumber() {
        return addressSerialNumber;
    }

    public void setAddressSerialNumber(String addressSerialNumber) {
        this.addressSerialNumber = addressSerialNumber;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public RangeType getRange() {
        return range;
    }

    public void setRange(RangeType range) {
        this.range = range;
    }

}