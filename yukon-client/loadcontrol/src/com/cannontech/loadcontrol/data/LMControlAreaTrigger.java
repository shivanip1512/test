package com.cannontech.loadcontrol.data;

import java.util.Date;

import com.cannontech.dr.controlarea.model.TriggerType;

public class LMControlAreaTrigger implements Cloneable
{
	private Integer yukonID = null;
	private Integer triggerNumber = null;
	private TriggerType triggerType = null;
	private Integer pointId = null;
	private Double pointValue = null;
	private Date lastPointValueTimeStamp = null;
	private Integer normalState = null;
	private Double threshold = null;
	private Integer thresholdPointId = null;
	private String projectionType = null;
	private Integer projectionPoints = null;
	private Integer projectAheadDuration = null;
	private Integer thresholdKickPercent = null;
	private Double minRestoreOffset = null;
	private Integer peakPointId = null;
	private Double peakPointValue = null;
	private Date lastPeakPointValueTimeStamp = null;
	private Double projectedPointValue = null;

	public LMControlAreaTrigger clone() {
	    try {
            return (LMControlAreaTrigger) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
	}

public java.util.Date getLastPeakPointValueTimeStamp() {
	return lastPeakPointValueTimeStamp;
}

public java.util.Date getLastPointValueTimeStamp() {
	return lastPointValueTimeStamp;
}

public java.lang.Double getMinRestoreOffset() {
	return minRestoreOffset;
}

public java.lang.Integer getNormalState() {
	return normalState;
}

public java.lang.Integer getPeakPointId() {
	return peakPointId;
}

public java.lang.Double getPeakPointValue() {
	return peakPointValue;
}

public java.lang.Integer getPointId() {
	return pointId;
}

public java.lang.Double getPointValue() {
	return pointValue;
}

public java.lang.Integer getProjectAheadDuration() {
	return projectAheadDuration;
}

public java.lang.Double getProjectedPointValue() {
	return projectedPointValue;
}

public java.lang.Integer getProjectionPoints() {
	return projectionPoints;
}

public java.lang.String getProjectionType() {
	return projectionType;
}

public java.lang.Double getThreshold() {
	return threshold;
}

public java.lang.Integer getThresholdKickPercent() {
	return thresholdKickPercent;
}

public java.lang.Integer getTriggerNumber() {
	return triggerNumber;
}

public TriggerType getTriggerType() {
	return triggerType;
}

public java.lang.Integer getYukonID() {
	return yukonID;
}

public void setLastPeakPointValueTimeStamp(java.util.Date newLastPeakPointValueTimeStamp) {
	lastPeakPointValueTimeStamp = newLastPeakPointValueTimeStamp;
}

public void setLastPointValueTimeStamp(java.util.Date newLastPointValueTimeStamp) {
	lastPointValueTimeStamp = newLastPointValueTimeStamp;
}

public void setMinRestoreOffset(java.lang.Double newMinRestoreOffset) {
	minRestoreOffset = newMinRestoreOffset;
}

public void setNormalState(java.lang.Integer newNormalState) {
	normalState = newNormalState;
}

public void setPeakPointId(java.lang.Integer newPeakPointId) {
	peakPointId = newPeakPointId;
}

public void setPeakPointValue(java.lang.Double newPeakPointValue) {
	peakPointValue = newPeakPointValue;
}

public void setPointId(java.lang.Integer newPointId) {
	pointId = newPointId;
}

public void setPointValue(java.lang.Double newPointValue) {
	pointValue = newPointValue;
}

public void setProjectAheadDuration(java.lang.Integer newProjectAheadDuration) {
	projectAheadDuration = newProjectAheadDuration;
}

public void setProjectedPointValue(java.lang.Double newProjectedPointValue) {
	projectedPointValue = newProjectedPointValue;
}

public void setProjectionPoints(java.lang.Integer newProjectionPoints) {
	projectionPoints = newProjectionPoints;
}

public void setProjectionType(java.lang.String newProjectionType) {
	projectionType = newProjectionType;
}

public void setThreshold(java.lang.Double newThreshold) {
	threshold = newThreshold;
}

public void setThresholdKickPercent(java.lang.Integer newThresholdKickPercent) {
	thresholdKickPercent = newThresholdKickPercent;
}

public void setTriggerNumber(java.lang.Integer newTriggerNumber) {
	triggerNumber = newTriggerNumber;
}

public void setTriggerType(TriggerType triggerType) {
	this.triggerType = triggerType;
}

public void setYukonID(java.lang.Integer newYukonID) {
	yukonID = newYukonID;
}

public String toString() 
{
	return getTriggerType() + ":" + getPointId();
}

public void setThresholdPointId(Integer thresholdPointId) {
    this.thresholdPointId = thresholdPointId;
}

public Integer getThresholdPointId() {
    return thresholdPointId;
}
}
