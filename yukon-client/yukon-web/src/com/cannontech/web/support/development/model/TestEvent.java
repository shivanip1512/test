package com.cannontech.web.support.development.model;

import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.event.Direction;
import com.cannontech.amr.rfn.message.event.RfnConditionType;

public class TestEvent {
    private int serialFrom = 1000;
    private int serialTo = 1000;
    private String manufacturer;
    private String model;
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
    
    public int getSerialFrom() {
        return serialFrom;
    }
    public void setSerialFrom(int serialFrom) {
        this.serialFrom = serialFrom;
    }
    public int getSerialTo() {
        return serialTo;
    }
    public void setSerialTo(int serialTo) {
        this.serialTo = serialTo;
    }
    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
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

}
