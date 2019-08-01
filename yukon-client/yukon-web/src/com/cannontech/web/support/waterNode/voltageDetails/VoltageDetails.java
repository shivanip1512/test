package com.cannontech.web.support.waterNode.voltageDetails;

import java.util.ArrayList;

import org.joda.time.Instant;

public class VoltageDetails {
    private ArrayList<Double> voltages;
    private ArrayList<Instant> timestamps;
    private String serialNumber;
    private Integer PaObjectId;
    
    public VoltageDetails() {
        voltages = new ArrayList<Double>();
        timestamps = new ArrayList<Instant>();
    }
    
    
    public Integer getPaObjectId() {
        return PaObjectId;
    }

    public void setPaObjectId(Integer PaObjectId) {
        this.PaObjectId = PaObjectId;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public ArrayList<Instant> getTimestamps() {
        return timestamps;
    }
    
    public void addTimestamp(Instant timestamp) {
        timestamps.add(timestamp);
    }
    
    public ArrayList<Double> getVoltages() {
        return voltages;
    }
    
    public void addVoltage(Double voltage) {
        voltages.add(voltage);
    }
}
