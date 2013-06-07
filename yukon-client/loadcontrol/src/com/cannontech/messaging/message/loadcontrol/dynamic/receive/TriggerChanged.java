package com.cannontech.messaging.message.loadcontrol.dynamic.receive;

import java.util.GregorianCalendar;

import com.cannontech.messaging.message.BaseMessage;

public class TriggerChanged extends BaseMessage {

    private int paoId;
    private int triggerNumber;
    private double pointValue;
    private GregorianCalendar lastPointValueTimestamp = null;
    private int normalState;
    private double threshold;
    private double peakPointValue;
    private GregorianCalendar lastPeakPointValueTimestamp = null;
    private double projectedPointValue;

    public int getNormalState() {
        return normalState;
    }

    public void setNormalState(int normalState) {
        this.normalState = normalState;
    }

    public GregorianCalendar getLastPointValueTimestamp() {
        return lastPointValueTimestamp;
    }

    public void setLastPointValueTimestamp(GregorianCalendar lastPointValueTimestamp) {
        this.lastPointValueTimestamp = lastPointValueTimestamp;
    }

    public int getPaoId() {
        return paoId;
    }

    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }

    public GregorianCalendar getLastPeakPointValueTimestamp() {
        return lastPeakPointValueTimestamp;
    }

    public void setLastPeakPointValueTimestamp(GregorianCalendar lastPeakPointValueTimestamp) {
        this.lastPeakPointValueTimestamp = lastPeakPointValueTimestamp;
    }

    public double getPeakPointValue() {
        return peakPointValue;
    }

    public void setPeakPointValue(double peakPointValue) {
        this.peakPointValue = peakPointValue;
    }

    public double getPointValue() {
        return pointValue;
    }

    public void setPointValue(double pointValue) {
        this.pointValue = pointValue;
    }

    public double getProjectedPointValue() {
        return projectedPointValue;
    }

    public void setProjectedPointValue(double projectedPointValue) {
        this.projectedPointValue = projectedPointValue;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public int getTriggerNumber() {
        return triggerNumber;
    }

    public void setTriggerNumber(int triggerNumber) {
        this.triggerNumber = triggerNumber;
    }

}
