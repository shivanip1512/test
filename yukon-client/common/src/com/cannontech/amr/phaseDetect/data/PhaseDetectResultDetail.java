package com.cannontech.amr.phaseDetect.data;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PhaseDetectResultDetail {

    private String phase;
    private int meterCount;
    private double percentage;
    private String colorHexValue;
    
    public int getMeterCount() {
        return meterCount;
    }

    public void setMeterCount(int meterCount) {
        this.meterCount = meterCount;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getColorHexValue() {
        return colorHexValue;
    }

    public void setColorHexValue(String colorHexValue) {
        this.colorHexValue = colorHexValue;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }
    
    public void calculatePrecentage(int total) {
        if (total != 0) {
            this.percentage = new BigDecimal(this.meterCount).divide(new BigDecimal(total), 10, RoundingMode.HALF_EVEN).multiply(new BigDecimal(100)).doubleValue();
        }
    }
}
