package com.cannontech.loadcontrol.data;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:06:09 PM)
 * @author: 
 */
public class LMGroupRipple extends LMDirectGroupBase
{
	private Integer routeID = null;
	private Integer scheduleTime = null;
	private String controlValue = null;
	private String restoreValue = null;
/**
 * Insert the method's description here.
 * Creation date: (1/11/2002 11:00:27 AM)
 * @return java.lang.String
 */
public java.lang.String getControlValue() {
	return controlValue;
}
/**
 * Insert the method's description here.
 * Creation date: (1/11/2002 11:00:27 AM)
 * @return java.lang.String
 */
public java.lang.String getRestoreValue() {
	return restoreValue;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 11:31:08 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getRouteID() {
	return routeID;
}
/**
 * Insert the method's description here.
 * Creation date: (1/11/2002 11:00:27 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getScheduleTime() {
	return scheduleTime;
}

/**
 * Insert the method's description here.
 * Creation date: (1/11/2002 11:00:27 AM)
 * @param newControlValue java.lang.String
 */
public void setControlValue(java.lang.String newControlValue) {
	controlValue = newControlValue;
}
/**
 * Insert the method's description here.
 * Creation date: (1/11/2002 11:00:27 AM)
 * @param newRestoreValue java.lang.String
 */
public void setRestoreValue(java.lang.String newRestoreValue) {
	restoreValue = newRestoreValue;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 11:31:08 AM)
 * @param newRouteID java.lang.Integer
 */
public void setRouteID(java.lang.Integer newRouteID) {
	routeID = newRouteID;
}
/**
 * Insert the method's description here.
 * Creation date: (1/11/2002 11:00:27 AM)
 * @param newScheduleTime java.lang.Integer
 */
public void setScheduleTime(java.lang.Integer newScheduleTime) {
	scheduleTime = newScheduleTime;
}
}
