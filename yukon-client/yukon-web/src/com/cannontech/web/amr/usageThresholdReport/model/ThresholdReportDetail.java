package com.cannontech.web.amr.usageThresholdReport.model;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dynamic.PointValueQualityHolder;

public class ThresholdReportDetail {

    private PaoIdentifier paoIdentifier;
    private PaoIdentifier gatewayPaoIdentifier;
    private String deviceName;
    private String gatewayName;
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
    
    public void setPaoIdentifier(PaoIdentifier paoIdentifier) {
        this.paoIdentifier = paoIdentifier;
    }
    
    public PaoIdentifier getGatewayPaoIdentifier() {
        return gatewayPaoIdentifier;
    }
    
    public void setGatewayPaoIdentifier(PaoIdentifier gatewayPaoIdentifier) {
        this.gatewayPaoIdentifier = gatewayPaoIdentifier;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    
    public String getGatewayName() {
        return gatewayName;
    }
    
    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
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

    public Double getDelta() {
        return delta;
    }

    public void setDelta(Double delta) {
        this.delta = delta;
    }

    public void calculateDelta() {
        delta = earliestReading == null ? null: latestReading.getValue() - earliestReading.getValue();
    }
      
    public PointValueQualityHolder getEarliestReading() {
        return earliestReading;
    }

    public void setEarliestReading(PointValueQualityHolder earliestReading) {
        this.earliestReading = earliestReading;
    }
    
    public PointValueQualityHolder getLatestReading() {
        return latestReading;
    }

    public void setLatestReading(PointValueQualityHolder latestReading) {
        this.latestReading = latestReading;
    }
  
    public DataAvailability getAvailability() {
        return availability;
    }

    public void setAvailability(DataAvailability availability) {
        this.availability = availability;
    }

    public Integer getPointId() {
        return pointId;
    }

    public void setPointId(Integer pointId) {
        this.pointId = pointId;
    }
}
