package com.cannontech.capcontrol.model;

public class ViewableStrategy {
    
    private int strategyId;
    private String strategyName;
    private String controlMethod;
    private String controlUnits;
    private String peakStartTime;
    private String peakStopTime;
    private String controlInterval;
    private String minResponseTime;
    private String passFailPercent;
    private String peakSettings;
    private String offPeakSettings;
    
    public int getStrategyId() {
        return strategyId;
    }
    
    public void setStrategyId(int strategyId) {
        this.strategyId = strategyId;
    }
    
    public String getStrategyName() {
        return strategyName;
    }
    
    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }
    
    public String getControlMethod() {
        return controlMethod;
    }
    
    public void setControlMethod(String controlMethod) {
        this.controlMethod = controlMethod;
    }
    
    public String getControlUnits() {
        return controlUnits;
    }
    
    public void setControlUnits(String controlUnits) {
        this.controlUnits = controlUnits;
    }
    
    public String getPeakStartTime() {
        return peakStartTime;
    }
    
    public void setPeakStartTime(String peakStartTime) {
        this.peakStartTime = peakStartTime;
    }
    
    public String getPeakStopTime() {
        return peakStopTime;
    }
    
    public void setPeakStopTime(String peakStopTime) {
        this.peakStopTime = peakStopTime;
    }
    
    public String getControlInterval() {
        return controlInterval;
    }
    
    public void setControlInterval(String controlInterval) {
        this.controlInterval = controlInterval;
    }
    
    public String getMinResponseTime() {
        return minResponseTime;
    }
    
    public void setMinResponseTime(String minResponseTime) {
        this.minResponseTime = minResponseTime;
    }
    
    public String getPassFailPercent() {
        return passFailPercent;
    }
    
    public void setPassFailPercent(String passFailPercent) {
        this.passFailPercent = passFailPercent;
    }
    
    public String getPeakSettings() {
        return peakSettings;
    }
    
    public void setPeakSettings(String peakSettings) {
        this.peakSettings = peakSettings;
    }
    
    public String getOffPeakSettings() {
        return offPeakSettings;
    }
    
    public void setOffPeakSettings(String offPeakSettings) {
        this.offPeakSettings = offPeakSettings;
    }
    
}