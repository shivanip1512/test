package com.cannontech.dr.program.model;

import org.joda.time.LocalTime;

public class GearAdjustment {
    private LocalTime beginTime;
    private LocalTime endTime;
    private int adjustmentValue;

    public LocalTime getBeginTime() {
        return beginTime;
    }
    public void setBeginTime(LocalTime beginTime) {
        this.beginTime = beginTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getAdjustmentValue() {
        return adjustmentValue;
    }
    public void setAdjustmentValue(int adjustmentValue) {
        this.adjustmentValue = adjustmentValue;
    }
}
