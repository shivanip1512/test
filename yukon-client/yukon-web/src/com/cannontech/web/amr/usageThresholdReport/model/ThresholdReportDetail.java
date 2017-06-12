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
    private DataAvailability availability;
    private Integer pointId;

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

    public void calculateDelta() {
        delta = earliestReading == null ? null: latestReading.getValue() - earliestReading.getValue();
    }
    
    public DataAvailability getAvailability() {
        return availability;
    }

    public void setAvailability(DataAvailability availability) {
        this.availability = availability;
    }

    public Double getDelta() {
        return delta;
    }

    public void setDelta(Double delta) {
        this.delta = delta;
    }

    public Integer getPointId() {
        return pointId;
    }

    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }
}
