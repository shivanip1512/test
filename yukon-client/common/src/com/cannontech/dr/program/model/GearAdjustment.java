package com.cannontech.dr.program.model;

import java.util.Date;

import org.joda.time.DateTime;

public class GearAdjustment {
    private Date beginTime = null;
    private Date endTime = null;
    private int adjustmentValue = 100;

    public GearAdjustment() {
    }

    public GearAdjustment(Date beginTime) {
        setBeginTime(beginTime);
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
        endTime = new DateTime(beginTime).plusHours(1).toDate();
    }

    public Date getEndTime() {
        return endTime;
    }

    public int getAdjustmentValue() {
        return adjustmentValue;
    }

    public void setAdjustmentValue(int adjustmentValue) {
        this.adjustmentValue = adjustmentValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + adjustmentValue;
        result = prime * result + ((beginTime == null) ? 0
                : beginTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GearAdjustment other = (GearAdjustment) obj;
        if (adjustmentValue != other.adjustmentValue)
            return false;
        if (beginTime == null) {
            if (other.beginTime != null)
                return false;
        } else if (!beginTime.equals(other.beginTime))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GearAdjustment [adjustmentValue=" + adjustmentValue +
            ", beginTime=" + beginTime + ", endTime=" + endTime + "]";
    }
}
