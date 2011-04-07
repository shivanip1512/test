package com.cannontech.thirdparty.messaging;

public class SepControlMessage {

    private int groupId;
    
    //unsigned int on C++ side
    private  long utcStartTime;
    //unsigned short on C++ side
    private short controlMinutes;
    //unsigned char on C++ side
    private byte criticality;
    //unsigned char on C++ side
    private byte coolTempOffset;
    //unsigned char  on C++ side
    private byte heatTempOffset;
    
    private short coolTempSetpoint;
    private short heatTempSetpoint;
    private byte averageCyclePercent;

    //unsigned char on C++ side
    private byte standardCyclePercent;
    //unsigned char on C++ side
    private byte eventFlags;
    
    
    public int getGroupId() {
        return groupId;
    }
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    
    public long getUtcStartTime() {
        return utcStartTime;
    }
    public void setUtcStartTime(long utcStartTime) {
        this.utcStartTime = utcStartTime;
    }
    
    public short getControlMinutes() {
        return controlMinutes;
    }
    public void setControlMinutes(short controlMinutes) {
        this.controlMinutes = controlMinutes;
    }
    
    public void setCriticality(byte criticality) {
        this.criticality = criticality;
    }
    public byte getCriticality() {
        return criticality;
    }
    
    public byte getCoolTempOffset() {
        return coolTempOffset;
    }
    public void setCoolTempOffset(byte coolTempOffset) {
        this.coolTempOffset = coolTempOffset;
    }
    
    public byte getHeatTempOffset() {
        return heatTempOffset;
    }
    public void setHeatTempOffset(byte heatTempOffset) {
        this.heatTempOffset = heatTempOffset;
    }
    
    public short getCoolTempSetpoint() {
        return coolTempSetpoint;
    }
    public void setCoolTempSetpoint(short coolTempSetpoint) {
        this.coolTempSetpoint = coolTempSetpoint;
    }
    
    public short getHeatTempSetpoint() {
        return heatTempSetpoint;
    }
    public void setHeatTempSetpoint(short heatTempSetpoint) {
        this.heatTempSetpoint = heatTempSetpoint;
    }
    
    public byte getAverageCyclePercent() {
        return averageCyclePercent;
    }
    public void setAverageCyclePercent(byte averageCyclePercent) {
        this.averageCyclePercent = averageCyclePercent;
    }
    
    public byte getStandardCyclePercent() {
        return standardCyclePercent;
    }
    public void setStandardCyclePercent(byte standardCyclePercent) {
        this.standardCyclePercent = standardCyclePercent;
    }
    
    public byte getEventFlags() {
        return eventFlags;
    }
    public void setEventFlags(byte eventFlags) {
        this.eventFlags = eventFlags;
    }
}