/**
 * 
 */
package com.cannontech.services.validation.model;

import com.cannontech.common.device.groups.model.DeviceGroup;

public class AnalysisDescription {
    private double reasonableMaxKwhPerDay = 250;
    private double kwhReadingError = .1;
    private double kwhSlopeError = 4;
    private double peakHeightMinimum = 2;
    private boolean reReadOnUnreasonable = false;
    private DeviceGroup deviceGroup;
    
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
    public void setDeviceGroup(DeviceGroup deviceGroup) {
        this.deviceGroup = deviceGroup;
    }
    public DeviceGroup getDeviceGroup() {
        return deviceGroup;
    }
}