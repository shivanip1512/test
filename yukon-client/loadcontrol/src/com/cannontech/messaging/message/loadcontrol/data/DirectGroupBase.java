package com.cannontech.messaging.message.loadcontrol.data;

import java.util.Date;
import java.util.GregorianCalendar;

import com.cannontech.common.util.CtiUtilities;

public abstract class DirectGroupBase extends GroupBase implements Group {

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
    private Date lastStopTimeSent = null;

    private int internalState = 0x0000000;

    public java.lang.Boolean getAlarmInhibit() {
        return alarmInhibit;
    }

    public java.lang.Integer getChildOrder() {
        return childOrder;
    }

    public String getStatistics() {
        return CtiUtilities.decodeSecondsToTime(getCurrentHoursDaily().intValue()) + " / "
            + CtiUtilities.decodeSecondsToTime(getCurrentHoursMonthly().intValue()) + " / "
            + CtiUtilities.decodeSecondsToTime(getCurrentHoursSeasonal().intValue()) + " / "
            + CtiUtilities.decodeSecondsToTime(getCurrentHoursAnnually().intValue());
    }

    public java.lang.Boolean getControlInhibit() {
        return controlInhibit;
    }

    public java.lang.Integer getCurrentHoursAnnually() {
        return currentHoursAnnually;
    }

    public java.lang.Integer getCurrentHoursDaily() {
        return currentHoursDaily;
    }

    public java.lang.Integer getCurrentHoursMonthly() {
        return currentHoursMonthly;
    }

    public java.lang.Integer getCurrentHoursSeasonal() {
        return currentHoursSeasonal;
    }

    public java.lang.Integer getGroupControlState() {
        return groupControlState;
    }

    public String getGroupControlStateString() {
        return getCurrentStateString(getGroupControlState().intValue());
    }

    public java.util.Date getGroupTime() {
        return getLastControlSent().getTime();
    }

    public java.util.GregorianCalendar getLastControlSent() {
        return lastControlSent;
    }

    public void setAlarmInhibit(java.lang.Boolean newAlarmInhibit) {
        alarmInhibit = newAlarmInhibit;
    }

    public void setChildOrder(java.lang.Integer newChildOrder) {
        childOrder = newChildOrder;
    }

    public void setControlInhibit(java.lang.Boolean newControlInhibit) {
        controlInhibit = newControlInhibit;
    }

    public void setCurrentHoursAnnually(java.lang.Integer newCurrentHoursAnnually) {
        currentHoursAnnually = newCurrentHoursAnnually;
    }

    public void setCurrentHoursDaily(java.lang.Integer newCurrentHoursDaily) {
        currentHoursDaily = newCurrentHoursDaily;
    }

    public void setCurrentHoursMonthly(java.lang.Integer newCurrentHoursMonthly) {
        currentHoursMonthly = newCurrentHoursMonthly;
    }

    public void setCurrentHoursSeasonal(java.lang.Integer newCurrentHoursSeasonal) {
        currentHoursSeasonal = newCurrentHoursSeasonal;
    }

    public void setGroupControlState(java.lang.Integer newGroupControlState) {
        groupControlState = newGroupControlState;
    }

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
    public Date getControlCompleteTime() {
        return controlCompleteTime;
    }

    /**
     * Returns the controlStartTime.
     * @return Date
     */
    public Date getControlStartTime() {
        return controlStartTime;
    }

    /**
     * Sets the controlCompleteTime.
     * @param controlCompleteTime The controlCompleteTime to set
     */
    public void setControlCompleteTime(Date controlCompleteTime) {
        this.controlCompleteTime = controlCompleteTime;
    }

    /**
     * Sets the controlStartTime.
     * @param controlStartTime The controlStartTime to set
     */
    public void setControlStartTime(Date controlStartTime) {
        this.controlStartTime = controlStartTime;
    }

    public Date getNextControlTime() {
        return nextControlTime;
    }

    public void setNextControlTime(Date date) {
        nextControlTime = date;
    }

    public Date getLastStopTimeSent() {
        return lastStopTimeSent;
    }

    public void setLastStopTimeSent(Date lastStopTimeSent) {
        this.lastStopTimeSent = lastStopTimeSent;
    }

    public boolean isRampingIn() {
        return (internalState & GROUP_RAMPING_IN) != 0;
    }

    public boolean isRampingOut() {
        return (internalState & GROUP_RAMPING_OUT) != 0;
    }

    public void setRampingIn(boolean b) {
        internalState = (b ? internalState | GROUP_RAMPING_IN : internalState & ~GROUP_RAMPING_IN);
    }

    public void setRampingOut(boolean b) {
        internalState = (b ? internalState | GROUP_RAMPING_OUT : internalState & ~GROUP_RAMPING_OUT);
    }

    public void setInternalState(int s) {
        internalState = s;
    }

    /**
     * Returns true when Load group is in an ACTIVE state
     * @return
     */
    public boolean isActive() {
        switch (groupControlState) {
            case GroupBase.STATE_ACTIVE:
            case GroupBase.STATE_ACTIVE_PENDING:
                return true;
            default:
                return false;
        }
    }
}
