package com.cannontech.database.db.capcontrol;

public class PeakTargetSetting {

    private double peakValue = 0;
    private double offPeakValue = 0;
    
    public PeakTargetSetting() {}
    
    public PeakTargetSetting(double peakValue, double offPeakValue) {
        this.peakValue = peakValue;
        this.offPeakValue = offPeakValue;
    }

    public double getPeakValue() {
        return peakValue;
    }

    public void setPeakValue(double peakValue) {
        this.peakValue = peakValue;
    }
    
    public double getOffPeakValue() {
        return offPeakValue;
    }

    public void setOffPeakValue(double offPeakValue) {
        this.offPeakValue = offPeakValue;
    }
}