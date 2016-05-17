package com.cannontech.dr.rfn.model;

import java.io.Serializable;

import org.joda.time.Hours;

public class SimulatorSettings implements Serializable{
    
    //lcr data simulator
    private int lcr6200serialFrom;
    private int lcr6200serialTo;
    private int lcr6600serialFrom;
    private int lcr6600serialTo;
    
    //meter data simulator
    private String paoType;
    
    //% of duplicates to generate
    private int percentOfDuplicates;
    
    //used for testing simulator
    private int deviceId;
    
    private int controlInterval = Hours.hours(1).toStandardSeconds().getSeconds();
    
    public SimulatorSettings(int lcr6200serialFrom, int lcr6200serialTo, int lcr6600serialFrom, int lcr6600serialTo, int percentOfDuplicates, int controlInterval) {
        this.lcr6200serialFrom = lcr6200serialFrom;
        this.lcr6200serialTo = lcr6200serialTo;
        this.lcr6600serialFrom = lcr6600serialFrom;
        this.lcr6600serialTo = lcr6600serialTo;
        this.percentOfDuplicates =  percentOfDuplicates;
        this.controlInterval = controlInterval;
    }
    
    public SimulatorSettings(String paoType, int percentOfDuplicates, int controlInterval) {
        this.paoType = paoType;
        this.percentOfDuplicates =  percentOfDuplicates;
        this.controlInterval = controlInterval;
    }
    public SimulatorSettings(int deviceId) {
        this.setDeviceId(deviceId);
    }
    public SimulatorSettings() {
    }
    
    public int getLcr6200serialFrom() {
        return lcr6200serialFrom;
    }
    public void setLcr6200serialFrom(int lcr6200serialFrom) {
        this.lcr6200serialFrom = lcr6200serialFrom;
    }
    public int getLcr6200serialTo() {
        return lcr6200serialTo;
    }
    public void setLcr6200serialTo(int lcr6200serialTo) {
        this.lcr6200serialTo = lcr6200serialTo;
    }
    public int getLcr6600serialFrom() {
        return lcr6600serialFrom;
    }
    public void setLcr6600serialFrom(int lcr6600serialFrom) {
        this.lcr6600serialFrom = lcr6600serialFrom;
    }
    public int getLcr6600serialTo() {
        return lcr6600serialTo;
    }
    public void setLcr6600serialTo(int lcr6600serialTo) {
        this.lcr6600serialTo = lcr6600serialTo;
    }
    public int getPercentOfDuplicates() {
        return percentOfDuplicates;
    }
    public void setPercentOfDuplicates(int percentOfDuplicates) {
        this.percentOfDuplicates = percentOfDuplicates;
    }

    public String getPaoType() {
        return paoType;
    }

    public void setPaoType(String paoType) {
        this.paoType = paoType;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getControlInterval() {
        return controlInterval;
    }

    public void setControlInterval(int controlInterval) {
        this.controlInterval = controlInterval;
    }
}
