package com.cannontech.loadcontrol.data;

/**
 * Insert the type's description here.
 * Creation date: (1/14/2002 12:58:45 PM)
 * @author: 
 */
public abstract class LMDirectGroupBase extends LMGroupBase 
{
	private Integer childOrder = null;	
	private Boolean alarmInhibit = null;
	private Boolean controlInhibit = null;
	private Integer groupControlState = null;
	private Integer currentHoursDaily = null;
	private Integer currentHoursMonthly = null;
	private Integer currentHoursSeasonal = null;
	private Integer currentHoursAnnually = null;
	private java.util.GregorianCalendar lastControlSent = null;
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 12:59:51 PM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getAlarmInhibit() {
	return alarmInhibit;
}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2002 5:25:49 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getChildOrder() {
	return childOrder;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 12:59:51 PM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getControlInhibit() {
	return controlInhibit;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 12:59:51 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentHoursAnnually() {
	return currentHoursAnnually;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 12:59:51 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentHoursDaily() {
	return currentHoursDaily;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 12:59:51 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentHoursMonthly() {
	return currentHoursMonthly;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 12:59:51 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentHoursSeasonal() {
	return currentHoursSeasonal;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 12:59:51 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getGroupControlState() {
	return groupControlState;
}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2001 8:56:31 AM)
 * @return java.lang.String
 */
public String getGroupControlStateString()
{
	return getCurrentStateString( getGroupControlState().intValue() );
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2001 2:33:32 PM)
 * @return java.lang.String
 */
public java.util.Date getGroupTime()
{
	return getLastControlSent().getTime();
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 12:59:51 PM)
 * @return java.util.GregorianCalendar
 */
public java.util.GregorianCalendar getLastControlSent() {
	return lastControlSent;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 12:59:51 PM)
 * @param newAlarmInhibit java.lang.Boolean
 */
public void setAlarmInhibit(java.lang.Boolean newAlarmInhibit) {
	alarmInhibit = newAlarmInhibit;
}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2002 5:25:49 PM)
 * @param newChildOrder java.lang.Integer
 */
public void setChildOrder(java.lang.Integer newChildOrder) {
	childOrder = newChildOrder;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 12:59:51 PM)
 * @param newControlInhibit java.lang.Boolean
 */
public void setControlInhibit(java.lang.Boolean newControlInhibit) {
	controlInhibit = newControlInhibit;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 12:59:51 PM)
 * @param newCurrentHoursAnnually java.lang.Integer
 */
public void setCurrentHoursAnnually(java.lang.Integer newCurrentHoursAnnually) {
	currentHoursAnnually = newCurrentHoursAnnually;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 12:59:51 PM)
 * @param newCurrentHoursDaily java.lang.Integer
 */
public void setCurrentHoursDaily(java.lang.Integer newCurrentHoursDaily) {
	currentHoursDaily = newCurrentHoursDaily;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 12:59:51 PM)
 * @param newCurrentHoursMonthly java.lang.Integer
 */
public void setCurrentHoursMonthly(java.lang.Integer newCurrentHoursMonthly) {
	currentHoursMonthly = newCurrentHoursMonthly;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 12:59:51 PM)
 * @param newCurrentHoursSeasonal java.lang.Integer
 */
public void setCurrentHoursSeasonal(java.lang.Integer newCurrentHoursSeasonal) {
	currentHoursSeasonal = newCurrentHoursSeasonal;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 12:59:51 PM)
 * @param newGroupControlState java.lang.Integer
 */
public void setGroupControlState(java.lang.Integer newGroupControlState) {
	groupControlState = newGroupControlState;
}
/**
 * Insert the method's description here.
 * Creation date: (1/14/2002 12:59:51 PM)
 * @param newLastControlSent java.util.GregorianCalendar
 */
public void setLastControlSent(java.util.GregorianCalendar newLastControlSent) {
	lastControlSent = newLastControlSent;
}
}
