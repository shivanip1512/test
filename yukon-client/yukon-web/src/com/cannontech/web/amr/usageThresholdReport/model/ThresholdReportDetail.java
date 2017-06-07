package com.cannontech.web.amr.usageThresholdReport.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.PointValueQualityHolder;

public class ThresholdReportDetail {

    private PaoIdentifier paoIdentifier;
    private String deviceName;
    private String meterNumber;
    private String addressSerialNumber;
    private boolean isEnabled;
    private Double delta;
    private PointValueQualityHolder earliestReading;
    private PointValueQualityHolder latestReading;

    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getMeterNumber() {
        return meterNumber;
    }

    public String getAddressSerialNumber() {
        return addressSerialNumber;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public PointValueQualityHolder getEarliestReading() {
        return earliestReading;
    }

    public PointValueQualityHolder getLatestReading() {
        return latestReading;
    }

    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public void setAddressSerialNumber(String addressSerialNumber) {
        this.addressSerialNumber = addressSerialNumber;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public void setEarliestReading(PointValueQualityHolder earliestReading) {
        this.earliestReading = earliestReading;
    }

    public void setLatestReading(PointValueQualityHolder latestReading) {
        this.latestReading = latestReading;
    }

    public Integer getPointId() {
        if (earliestReading != null) {
            return earliestReading.getId();
        }
        if (latestReading != null) {
            return latestReading.getId();
        }
        return null;
    }

    public void calculateDelta() {
        if (earliestReading != null && latestReading != null) {
            this.delta = latestReading.getValue() - earliestReading.getValue();
        }
    }

    public Double getDelta() {
        return delta;
    }
}
