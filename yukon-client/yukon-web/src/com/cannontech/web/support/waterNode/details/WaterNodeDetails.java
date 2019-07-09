package com.cannontech.web.support.waterNode.details;

import java.util.ArrayList;

import com.cannontech.web.support.waterNode.batteryLevel.WaterNodeBatteryLevel;

import java.time.Instant;

public class WaterNodeDetails {
    private ArrayList<Integer> voltages;
    private Integer meterNumber;
    private ArrayList<Instant> timestamps;
    private String serialNumber;
    private String name;
    private String type;
    private boolean highSleepingCurrentIndicator;
    private WaterNodeBatteryLevel batteryLevel;
    
    public WaterNodeDetails() {
        voltages = new ArrayList<Integer>();
        timestamps = new ArrayList<Instant>();
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public Integer getMeterNumber() {
        return meterNumber;
    }
    
    public void setMeterNumber(Integer meterNumber) {
        this.meterNumber = meterNumber;
    }
    
    public ArrayList<Instant> getTimestamps() {
        return timestamps;
    }
    
    public void addTimestamp(Instant timestamp) {
        timestamps.add(timestamp);
    }
    
    public ArrayList<Integer> getVoltages() {
        return voltages;
    }
    
    public void addVoltage(Integer voltage) {
        voltages.add(voltage);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public boolean getHighSleepingCurrentIndicator() {
        return highSleepingCurrentIndicator;
    }
    
    public void setHighSleepingCurrentIndicator(boolean highSleepingCurrentIndicator) {
        this.highSleepingCurrentIndicator = highSleepingCurrentIndicator;
    }
    
    public WaterNodeBatteryLevel getBatteryLevel() {
        return batteryLevel;
    }
    
    public void setBatteryLevel(WaterNodeBatteryLevel batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
}
