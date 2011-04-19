package com.cannontech.thirdparty.messaging;

public class SepControlMessage {

    private int groupId;
    
    //unsigned int on C++ side
    private  long utcStartTime;
    //unsigned short on C++ side
    private int controlMinutes;
    //unsigned char on C++ side
    private int criticality;
    //unsigned char on C++ side
    private int coolTempOffset;
    //unsigned char  on C++ side
    private int heatTempOffset;
    
    //Short
    private int coolTempSetpoint;
    //Short
    private int heatTempSetpoint;
    //byte
    private int averageCyclePercent;

    //unsigned char on C++ side
    private int standardCyclePercent;
    //unsigned char on C++ side
    private int eventFlags;
    
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
    public int getControlMinutes() {
        return controlMinutes;
    }
    public void setControlMinutes(int controlMinutes) {
        this.controlMinutes = controlMinutes;
    }
    public int getCriticality() {
        return criticality;
    }
    public void setCriticality(int criticality) {
        this.criticality = criticality;
    }
    public int getCoolTempOffset() {
        return coolTempOffset;
    }
    public void setCoolTempOffset(int coolTempOffset) {
        this.coolTempOffset = coolTempOffset;
    }
    public int getHeatTempOffset() {
        return heatTempOffset;
    }
    public void setHeatTempOffset(int heatTempOffset) {
        this.heatTempOffset = heatTempOffset;
    }
    public int getCoolTempSetpoint() {
        return coolTempSetpoint;
    }
    public void setCoolTempSetpoint(int coolTempSetpoint) {
        this.coolTempSetpoint = coolTempSetpoint;
    }
    public int getHeatTempSetpoint() {
        return heatTempSetpoint;
    }
    public void setHeatTempSetpoint(int heatTempSetpoint) {
        this.heatTempSetpoint = heatTempSetpoint;
    }
    public int getAverageCyclePercent() {
        return averageCyclePercent;
    }
    public void setAverageCyclePercent(int averageCyclePercent) {
        this.averageCyclePercent = averageCyclePercent;
    }
    public int getStandardCyclePercent() {
        return standardCyclePercent;
    }
    public void setStandardCyclePercent(int standardCyclePercent) {
        this.standardCyclePercent = standardCyclePercent;
    }
    public int getEventFlags() {
        return eventFlags;
    }
    public void setEventFlags(int eventFlags) {
        this.eventFlags = eventFlags;
    }
}