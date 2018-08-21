/**
 * 
 */
package com.cannontech.common.validation.model;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.monitors.PointMonitor;

public class ValidationMonitor implements PointMonitor, Comparable<ValidationMonitor> {
    
    private String name;
    private String groupName;
    private MonitorEvaluatorStatus evaluatorStatus;
    
    private Integer validationMonitorId;
    private double reasonableMaxKwhPerDay = 150.0;
    private double kwhReadingError = 0.1000001;
    private double kwhSlopeError = 4.0;
    private double peakHeightMinimum = 1.0;
    private boolean reReadOnUnreasonable = false;
    private boolean questionableOnPeak = false;

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

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String getGroupName() {
        return groupName;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isQuestionableOnPeak() {
        return questionableOnPeak;
    }

    public void setQuestionableOnPeak(boolean questionableOnPeak) {
        this.questionableOnPeak = questionableOnPeak;
    }

    public void setValidationMonitorId(int validationMonitorId) {
        this.validationMonitorId = validationMonitorId;
    }

    public Integer getValidationMonitorId() {
        return validationMonitorId;
    }

    @Override
    public MonitorEvaluatorStatus getEvaluatorStatus() {
        return evaluatorStatus;
    }

    public void setEvaluatorStatus(MonitorEvaluatorStatus status) {
        this.evaluatorStatus = status;
    }

    @Override
    public int compareTo(ValidationMonitor validationMonitor) {
        return name.compareToIgnoreCase(validationMonitor.name);
    }

    @Override
    public String toString() {
        return String
                .format("ValidationMonitor [name=%s, groupName=%s, evaluatorStatus=%s, validationMonitorId=%s, reasonableMaxKwhPerDay=%s, kwhReadingError=%s, kwhSlopeError=%s, peakHeightMinimum=%s, reReadOnUnreasonable=%s, setQuestionableOnPeak=%s]",
                        name, groupName, evaluatorStatus,
                        validationMonitorId, reasonableMaxKwhPerDay,
                        kwhReadingError, kwhSlopeError, peakHeightMinimum,
                        reReadOnUnreasonable, questionableOnPeak);
    }
    
}
