package com.cannontech.development.model;

import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.event.DetailedConfigurationStatusCode;
import com.cannontech.amr.rfn.message.event.Direction;
import com.cannontech.amr.rfn.message.event.MeterConfigurationStatus;
import com.cannontech.amr.rfn.message.event.MeterStatusCode;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.common.rfn.model.RfnManufacturerModel;

public class RfnTestEvent {
    private int serialFrom = 1000;
    private Integer serialTo;
    private RfnManufacturerModel manufacturerModel;
    private RfnConditionType rfnConditionType;
    private Boolean cleared;
    private Long count;
    private Direction direction;
    private Double measuredValue;
    private Long outageStartTime;
    private Double thresholdValue;
    private String uom; // V
    private String uomModifiers; // milli, max
    private int numEventPerMeter = 1;
    private int numAlarmPerMeter = 0;
    private Instant timestamp = new Instant();
    private String meterConfigurationId;
    private MeterConfigurationStatus meterConfigurationStatus;
    
    public int getSerialFrom() {
        return serialFrom;
    }
    public void setSerialFrom(int serialFrom) {
        this.serialFrom = serialFrom;
    }
    public Integer getSerialTo() {
        return serialTo;
    }
    public void setSerialTo(Integer serialTo) {
        this.serialTo = serialTo;
    }
    public RfnManufacturerModel getManufacturerModel() {
        return manufacturerModel;
    }
    public void setManufacturerModel(RfnManufacturerModel manufacturerModel) {
        this.manufacturerModel = manufacturerModel;
    }
    public RfnConditionType getRfnConditionType() {
        return rfnConditionType;
    }
    public void setRfnConditionType(RfnConditionType rfnConditionType) {
        this.rfnConditionType = rfnConditionType;
    }
    public Boolean getCleared() {
        return cleared;
    }
    public void setCleared(Boolean cleared) {
        this.cleared = cleared;
    }
    public Long getCount() {
        return count;
    }
    public void setCount(Long count) {
        this.count = count;
    }
    public Direction getDirection() {
        return direction;
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    public Double getMeasuredValue() {
        return measuredValue;
    }
    public void setMeasuredValue(Double measuredValue) {
        this.measuredValue = measuredValue;
    }
    public Long getOutageStartTime() {
        return outageStartTime;
    }
    public void setOutageStartTime(Long outageStartTime) {
        this.outageStartTime = outageStartTime;
    }
    public Double getThresholdValue() {
        return thresholdValue;
    }
    public void setThresholdValue(Double thresholdValue) {
        this.thresholdValue = thresholdValue;
    }
    public String getUom() {
        return uom;
    }
    public void setUom(String uom) {
        this.uom = uom;
    }
    public String getUomModifiers() {
        return uomModifiers;
    }
    public void setUomModifiers(String uomModifiers) {
        this.uomModifiers = uomModifiers;
    }
    public int getNumEventPerMeter() {
        return numEventPerMeter;
    }
    public void setNumEventPerMeter(int numEventPerMeter) {
        this.numEventPerMeter = numEventPerMeter;
    }
    public int getNumAlarmPerMeter() {
        return numAlarmPerMeter;
    }
    public void setNumAlarmPerMeter(int numAlarmPerMeter) {
        this.numAlarmPerMeter = numAlarmPerMeter;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    public Long getTimestampAsMillis() {
        return timestamp.getMillis();
    }
    public void setMeterConfigurationId(String meterConfigurationId) {
        this.meterConfigurationId = meterConfigurationId; 
    }
    public String getMeterConfigurationId() {
        return meterConfigurationId;
    }
    public void setMeterConfigurationStatusCode(Integer meterStatusCode) {
        getMeterConfigurationStatus().setMeterStatusCode(new MeterStatusCode(meterStatusCode.shortValue())); 
    }
    public Integer getMeterConfigurationStatusCode() {
        return (int) getMeterConfigurationStatus().getMeterStatusCode().getCode();
    }
    public void setMeterConfigurationStatusDetail(Integer detail) {
        getMeterConfigurationStatus().setDetailedConfigurationStatusCode(new DetailedConfigurationStatusCode(detail.shortValue()));
    }
    public Integer getMeterConfigurationStatusDetail() {
        return (int) getMeterConfigurationStatus().getDetailedConfigurationStatusCode().getCode();
    }
    public MeterConfigurationStatus getMeterConfigurationStatus() {
        if (meterConfigurationStatus == null) {
            meterConfigurationStatus = new MeterConfigurationStatus();
            meterConfigurationStatus.setMeterStatusCode(new MeterStatusCode((short)0));
            meterConfigurationStatus.setDetailedConfigurationStatusCode(new DetailedConfigurationStatusCode((short)0));
        }
        return meterConfigurationStatus;
    }
}
