/**
 * 
 */
package com.cannontech.common.validation.model;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.monitors.PointMonitor;

public class ValidationMonitor implements PointMonitor {
    private Integer validationMonitorId;
    private double reasonableMaxKwhPerDay = 150.0;
    private double kwhReadingError = 0.1000001;
    private double kwhSlopeError = 4.0;
    private double peakHeightMinimum = 1.0;
    private boolean reReadOnUnreasonable = false;
    private boolean setQuestionableOnPeak = false;
    private String deviceGroupName;
    private String name;
    private MonitorEvaluatorStatus evaluatorStatus;
    
    public void setReasonableMaxKwhPerDay(double reasonableMaxKwhPerDay) {
        this.reasonableMaxKwhPerDay = reasonableMaxKwhPerDay;
    }
    
    public double getReasonableMaxKwhPerDay() {
        return reasonableMaxKwhPerDay;
    }
    
    public void setKwhReadingError(double kwhReadingError) {
        this.kwhReadingError = kwhReadingError;
    }
    
    public double getKwhReadingError() {
        return kwhReadingError;
    }
    
    public void setKwhSlopeError(double kwhSlopeError) {
        this.kwhSlopeError = kwhSlopeError;
    }
    
    public double getKwhSlopeError() {
        return kwhSlopeError;
    }
    
    public void setPeakHeightMinimum(double peakHeightMinimum) {
        this.peakHeightMinimum = peakHeightMinimum;
    }
    
    public double getPeakHeightMinimum() {
        return peakHeightMinimum;
    }
    
    public void setReReadOnUnreasonable(boolean reReadOnUnreasonable) {
        this.reReadOnUnreasonable = reReadOnUnreasonable;
    }
    
    public boolean isReReadOnUnreasonable() {
        return reReadOnUnreasonable;
    }
    
    public void setDeviceGroupName(String deviceGroupName) {
        this.deviceGroupName = deviceGroupName;
    }
    
    public String getDeviceGroupName() {
        return deviceGroupName;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setQuestionableOnPeak(boolean setQuestionableOnPeak) {
        this.setQuestionableOnPeak = setQuestionableOnPeak;
    }
    
    public boolean isSetQuestionableOnPeak() {
        return setQuestionableOnPeak;
    }
    
    public void setValidationMonitorId(int validationMonitorId) {
        this.validationMonitorId = validationMonitorId;
    }
    
    public Integer getValidationMonitorId() {
        return validationMonitorId;
    }
    
    public MonitorEvaluatorStatus getEvaluatorStatus() {
        return evaluatorStatus;
    }
    
    public void setEvaluatorStatus(MonitorEvaluatorStatus status) {
        this.evaluatorStatus = status;
    }
    
    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("name", getName());
        tsc.append("deviceGroupName", getDeviceGroupName());
        tsc.append("reasonableMaxKwhPerDay", getReasonableMaxKwhPerDay());
        tsc.append("reReadOnUnreasonable", isReReadOnUnreasonable());
        tsc.append("kwhSlopeError", getKwhSlopeError());
        tsc.append("kwhReadingError", getKwhReadingError());
        tsc.append("peakHeightMinimum", getPeakHeightMinimum());
        tsc.append("setQuestionableQuality", isSetQuestionableOnPeak());
        tsc.append("evaluatorStatus", getEvaluatorStatus());
        return tsc.toString();
    }

}