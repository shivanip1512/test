package com.cannontech.loadcontrol.data;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:06:09 PM)
 * @author: 
 */
public class LMGroupPoint extends LMDirectGroupBase
{
	private Integer deviceIDUsage = null;
	private Integer pointIDUsage = null;
	private Integer startControlRawState = null;
/**
 * Insert the method's description here.
 * Creation date: (3/14/2002 4:22:47 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDeviceIDUsage() {
	return deviceIDUsage;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/2002 4:22:47 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPointIDUsage() {
	return pointIDUsage;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/2002 4:22:47 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getStartControlRawState() {
	return startControlRawState;
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/2002 4:22:47 PM)
 * @param newDeviceIDUsage java.lang.Integer
 */
public void setDeviceIDUsage(java.lang.Integer newDeviceIDUsage) {
	deviceIDUsage = newDeviceIDUsage;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/2002 4:22:47 PM)
 * @param newPointIDUsage java.lang.Integer
 */
public void setPointIDUsage(java.lang.Integer newPointIDUsage) {
	pointIDUsage = newPointIDUsage;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/2002 4:22:47 PM)
 * @param newStartControlRawState java.lang.Integer
 */
public void setStartControlRawState(java.lang.Integer newStartControlRawState) {
	startControlRawState = newStartControlRawState;
}
}
