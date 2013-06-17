package com.cannontech.loadcontrol.data;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:06:09 PM)
 * @author: 
 */
public class LMGroupRipple extends LMDirectGroupBase
{
	private Integer shedTime = null;
//	private Integer routeID = null;
//	private String controlValue = null;
//	private String restoreValue = null;
/**
 * Insert the method's description here.
 * Creation date: (1/11/2002 11:00:27 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getShedTime() {
	return shedTime;
}

/**
 * Insert the method's description here.
 * Creation date: (1/11/2002 11:00:27 AM)
 * @param newScheduleTime java.lang.Integer
 */
public void setShedTime(java.lang.Integer newShedTime) {
	shedTime = newShedTime;
}
}
