package com.cannontech.loadcontrol.data;

import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.common.util.CtiUtilities;

/**
 * Insert the type's description here.
 * Creation date: (1/14/2002 12:58:45 PM)
 * @author: 
 */
public abstract class LMDirectGroupBase extends LMGroupBase implements ILMGroup 
{
	private static final int GROUP_RAMPING_IN = 0x00000001;
	private static final int GROUP_RAMPING_OUT = 0x00000002;
	
	private Integer childOrder = null;	
	private Boolean alarmInhibit = null;
	private Boolean controlInhibit = null;
	private Integer groupControlState = null;
	private Integer currentHoursDaily = null;
	private Integer currentHoursMonthly = null;
	private Integer currentHoursSeasonal = null;
	private Integer currentHoursAnnually = null;
    private GregorianCalendar lastControlSent = null;
	
	private Date controlStartTime = null;
	private Date controlCompleteTime = null;
	private Date nextControlTime = null;	

	private int internalState = 0x0000000;
	
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
	 * Creation date: (4/18/2001 8:56:31 AM)
	 * @return java.lang.String
	 */
	public String getStatistics()
	{
		return CtiUtilities.decodeSecondsToTime(getCurrentHoursDaily().intValue()) + " / " 
			+ CtiUtilities.decodeSecondsToTime(getCurrentHoursMonthly().intValue()) + " / "
			+ CtiUtilities.decodeSecondsToTime(getCurrentHoursSeasonal().intValue()) + " / "
			+ CtiUtilities.decodeSecondsToTime(getCurrentHoursAnnually().intValue()); 
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


	public String getName() {
		return getYukonName();
	}	

	
	public Double getReduction() {
		return getKwCapacity();
	}

	public Integer getOrder() {
		return getChildOrder();
	}


	/**
	 * Returns the controlCompleteTime.
	 * @return Date
	 */
	public Date getControlCompleteTime()
	{
		return controlCompleteTime;
	}

	/**
	 * Returns the controlStartTime.
	 * @return Date
	 */
	public Date getControlStartTime()
	{
		return controlStartTime;
	}

	/**
	 * Sets the controlCompleteTime.
	 * @param controlCompleteTime The controlCompleteTime to set
	 */
	public void setControlCompleteTime(Date controlCompleteTime)
	{
		this.controlCompleteTime = controlCompleteTime;
	}

	/**
	 * Sets the controlStartTime.
	 * @param controlStartTime The controlStartTime to set
	 */
	public void setControlStartTime(Date controlStartTime)
	{
		this.controlStartTime = controlStartTime;
	}

	/**
	 * @return
	 */
	public Date getNextControlTime() {
		return nextControlTime;
	}

	/**
	 * @param date
	 */
	public void setNextControlTime(Date date) {
		nextControlTime = date;
	}
	
	public boolean isRampingIn() {
		return (internalState & GROUP_RAMPING_IN) != 0;
	}
	
	public boolean isRampingOut() {
		return (internalState & GROUP_RAMPING_OUT) != 0;
	}

	public void setRampingIn(boolean b) {
		internalState = (b ?
							internalState | GROUP_RAMPING_IN :
							internalState & ~GROUP_RAMPING_IN);
	}
	
	public void setRampingOut(boolean b) {
		internalState = (b ?
							internalState | GROUP_RAMPING_OUT :
							internalState & ~GROUP_RAMPING_OUT);		
	}
	
	void setInternalState(int s) {
		internalState = s;
	}
}
