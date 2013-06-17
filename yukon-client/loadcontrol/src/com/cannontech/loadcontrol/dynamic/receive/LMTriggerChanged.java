package com.cannontech.loadcontrol.dynamic.receive;

import java.util.GregorianCalendar;

import com.cannontech.message.util.Message;

/**
 * Insert the type's description here.
 * Creation date: (9/5/07 3:06:09 PM)
 * @author: jdayton
 */
public class LMTriggerChanged extends Message {
    
    private Integer paoID = null;
    private Integer triggerNumber = null;
    private Double pointValue = null;
    private GregorianCalendar lastPointValueTimestamp = null;
    private Integer normalState = null;
    private Double threshold = null;
    private Double peakPointValue = null;
    private GregorianCalendar lastPeakPointValueTimestamp = null;
    private Double projectedPointValue = null;
    
    public LMTriggerChanged() {
        super();
    }

    public Integer getNormalState() {
        return normalState;
    }
    
    public void setNormalState(Integer normalState) {
        this.normalState = normalState;
    }
    
    public GregorianCalendar getLastPointValueTimestamp() {
        return lastPointValueTimestamp;
    }
    
    public void setLastPointValueTimestamp(GregorianCalendar lastPointValueTimestamp) {
        this.lastPointValueTimestamp = lastPointValueTimestamp;
    }
    
    public Integer getPaoID() {
        return paoID;
    }
    
    public void setPaoID(Integer paoID) {
        this.paoID = paoID;
    }

    public GregorianCalendar getLastPeakPointValueTimestamp() {
        return lastPeakPointValueTimestamp;
    }

    public void setLastPeakPointValueTimestamp(
            GregorianCalendar lastPeakPointValueTimestamp) {
        this.lastPeakPointValueTimestamp = lastPeakPointValueTimestamp;
    }

    public Double getPeakPointValue() {
        return peakPointValue;
    }

    public void setPeakPointValue(Double peakPointValue) {
        this.peakPointValue = peakPointValue;
    }

    public Double getPointValue() {
        return pointValue;
    }

    public void setPointValue(Double pointValue) {
        this.pointValue = pointValue;
    }

    public Double getProjectedPointValue() {
        return projectedPointValue;
    }

    public void setProjectedPointValue(Double projectedPointValue) {
        this.projectedPointValue = projectedPointValue;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public Integer getTriggerNumber() {
        return triggerNumber;
    }

    public void setTriggerNumber(Integer triggerNumber) {
        this.triggerNumber = triggerNumber;
    }

}