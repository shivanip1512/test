package com.cannontech.yukon.api.stars.model;

import org.joda.time.LocalTime;

import com.cannontech.common.temperature.Temperature;

public class SchedulePeriod {
    private LocalTime periodStartTime;
    private Temperature coolTemperature;
    private Temperature heatTemperature;
    
    public LocalTime getPeriodStartTime() {
        return periodStartTime;
    }
    public void setPeriodStartTime(LocalTime periodStartTime) {
        this.periodStartTime = periodStartTime;
    }
    
    public Temperature getCoolTemperature() {
        return coolTemperature;
    }
    public void setCoolTemperature(Temperature coolTemperature) {
        this.coolTemperature = coolTemperature;
    }
    
    public Temperature getHeatTemperature() {
        return heatTemperature;
    }
    public void setHeatTemperature(Temperature heatTemperature) {
        this.heatTemperature = heatTemperature;
    }
    
    @Override
    public String toString() {
        String result = "["+periodStartTime+", Cool="+coolTemperature+", Heat="+heatTemperature+"]";
        return result;
    }
}