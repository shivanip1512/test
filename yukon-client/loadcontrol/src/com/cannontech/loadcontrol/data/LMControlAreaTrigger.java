package com.cannontech.loadcontrol.data;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:06:09 PM)
 * @author: 
 */
import java.util.Date;

public class LMControlAreaTrigger
{
	private Integer yukonID = null;
	private Integer triggerNumber = null;
	private String triggerType = null;
	private Integer pointId = null;
	private Double pointValue = null;
	private Date lastPointValueTimeStamp = null;
	private Integer normalState = null;
	private Double threshold = null;
	private String projectionType = null;
	private Integer projectionPoints = null;
	private Integer projectAheadDuration = null;
	private Integer thresholdKickPercent = null;
	private Double minRestoreOffset = null;
	private Integer peakPointId = null;
	private Double peakPointValue = null;
	private Date lastPeakPointValueTimeStamp = null;
	private Double projectedPointValue = null;


/**
 * Creation date: (7/18/2001 10:24:18 AM)
 * @return java.util.Date
 */
public java.util.Date getLastPeakPointValueTimeStamp() {
	return lastPeakPointValueTimeStamp;
}
/**
 * Creation date: (7/18/2001 10:24:18 AM)
 * @return java.util.Date
 */
public java.util.Date getLastPointValueTimeStamp() {
	return lastPointValueTimeStamp;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @return java.lang.Double
 */
public java.lang.Double getMinRestoreOffset() {
	return minRestoreOffset;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getNormalState() {
	return normalState;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPeakPointId() {
	return peakPointId;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @return java.lang.Double
 */
public java.lang.Double getPeakPointValue() {
	return peakPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPointId() {
	return pointId;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @return java.lang.Double
 */
public java.lang.Double getPointValue() {
	return pointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getProjectAheadDuration() {
	return projectAheadDuration;
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2002 12:22:16 PM)
 * @return java.lang.Double
 */
public java.lang.Double getProjectedPointValue() {
	return projectedPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getProjectionPoints() {
	return projectionPoints;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @return java.lang.String
 */
public java.lang.String getProjectionType() {
	return projectionType;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @return java.lang.Double
 */
public java.lang.Double getThreshold() {
	return threshold;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getThresholdKickPercent() {
	return thresholdKickPercent;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getTriggerNumber() {
	return triggerNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @return java.lang.String
 */
public java.lang.String getTriggerType() {
	return triggerType;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:39:51 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getYukonID() {
	return yukonID;
}
/**
 * Creation date: (7/18/2001 10:24:18 AM)
 * @param newLastPeakPointValueTimeStamp java.util.Date
 */
public void setLastPeakPointValueTimeStamp(java.util.Date newLastPeakPointValueTimeStamp) {
	lastPeakPointValueTimeStamp = newLastPeakPointValueTimeStamp;
}
/**
 * Creation date: (7/18/2001 10:24:18 AM)
 * @param newLastPointValueTimeStamp java.util.Date
 */
public void setLastPointValueTimeStamp(java.util.Date newLastPointValueTimeStamp) {
	lastPointValueTimeStamp = newLastPointValueTimeStamp;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @param newMinRestoreOffset java.lang.Double
 */
public void setMinRestoreOffset(java.lang.Double newMinRestoreOffset) {
	minRestoreOffset = newMinRestoreOffset;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @param newNormalState java.lang.Integer
 */
public void setNormalState(java.lang.Integer newNormalState) {
	normalState = newNormalState;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @param newPeakPointId java.lang.Integer
 */
public void setPeakPointId(java.lang.Integer newPeakPointId) {
	peakPointId = newPeakPointId;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @param newPeakPointValue java.lang.Double
 */
public void setPeakPointValue(java.lang.Double newPeakPointValue) {
	peakPointValue = newPeakPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @param newPointId java.lang.Integer
 */
public void setPointId(java.lang.Integer newPointId) {
	pointId = newPointId;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @param newPointValue java.lang.Double
 */
public void setPointValue(java.lang.Double newPointValue) {
	pointValue = newPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @param newProjectAheadDuration java.lang.Integer
 */
public void setProjectAheadDuration(java.lang.Integer newProjectAheadDuration) {
	projectAheadDuration = newProjectAheadDuration;
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2002 12:22:16 PM)
 * @param newProjectedPointValue java.lang.Double
 */
public void setProjectedPointValue(java.lang.Double newProjectedPointValue) {
	projectedPointValue = newProjectedPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @param newProjectionPoints java.lang.Integer
 */
public void setProjectionPoints(java.lang.Integer newProjectionPoints) {
	projectionPoints = newProjectionPoints;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @param newProjectionType java.lang.String
 */
public void setProjectionType(java.lang.String newProjectionType) {
	projectionType = newProjectionType;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @param newThreshold java.lang.Double
 */
public void setThreshold(java.lang.Double newThreshold) {
	threshold = newThreshold;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @param newThresholdKickPercent java.lang.Integer
 */
public void setThresholdKickPercent(java.lang.Integer newThresholdKickPercent) {
	thresholdKickPercent = newThresholdKickPercent;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @param newTriggerNumber java.lang.Integer
 */
public void setTriggerNumber(java.lang.Integer newTriggerNumber) {
	triggerNumber = newTriggerNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:59:06 PM)
 * @param newTriggerType java.lang.String
 */
public void setTriggerType(java.lang.String newTriggerType) {
	triggerType = newTriggerType;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:39:51 AM)
 * @param newYukonID java.lang.Integer
 */
public void setYukonID(java.lang.Integer newYukonID) {
	yukonID = newYukonID;
}
/**
 * Creation date: (6/3/2001 2:27:44 PM)
 * @return java.lang.String
 */
public String toString() 
{
	return getTriggerType() + ":" + getPointId();
}
}
