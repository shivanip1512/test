package com.cannontech.messaging.message.loadcontrol.dynamic.receive;

import java.util.GregorianCalendar;

import com.cannontech.messaging.message.BaseMessage;

public class GroupChanged extends BaseMessage {

    private int paoId;
    private boolean disableFlag;
    private int groupControlState;
    private int currentHoursDaily;
    private int currentHoursMonthly;
    private int currentHoursSeasonal;
    private int currentHoursAnnually;
    private GregorianCalendar lastControlSent = null;
    private GregorianCalendar controlStartTime = null;
    private GregorianCalendar controlCompleteTime = null;
    private GregorianCalendar nextControlTime = null;
    private int internalState;
    private int dailyOps;

    public boolean getDisableFlag() {
        return disableFlag;
    }

    public void setDisableFlag(boolean disableFlag) {
        this.disableFlag = disableFlag;
    }

    public int getPaoId() {
        return paoId;
    }

    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }

    public GregorianCalendar getControlCompleteTime() {
        return controlCompleteTime;
    }

    public void setControlCompleteTime(GregorianCalendar controlCompleteTime) {
        this.controlCompleteTime = controlCompleteTime;
    }

    public GregorianCalendar getControlStartTime() {
        return controlStartTime;
    }

    public void setControlStartTime(GregorianCalendar controlStartTime) {
        this.controlStartTime = controlStartTime;
    }

    public int getCurrentHoursAnnually() {
        return currentHoursAnnually;
    }

    public void setCurrentHoursAnnually(int currentHoursAnnually) {
        this.currentHoursAnnually = currentHoursAnnually;
    }

    public int getCurrentHoursDaily() {
        return currentHoursDaily;
    }

    public void setCurrentHoursDaily(int currentHoursDaily) {
        this.currentHoursDaily = currentHoursDaily;
    }

    public int getCurrentHoursMonthly() {
        return currentHoursMonthly;
    }

    public void setCurrentHoursMonthly(int currentHoursMonthly) {
        this.currentHoursMonthly = currentHoursMonthly;
    }

    public int getCurrentHoursSeasonal() {
        return currentHoursSeasonal;
    }

    public void setCurrentHoursSeasonal(int currentHoursSeasonal) {
        this.currentHoursSeasonal = currentHoursSeasonal;
    }

    public int getDailyOps() {
        return dailyOps;
    }

    public void setDailyOps(int dailyOps) {
        this.dailyOps = dailyOps;
    }

    public int getGroupControlState() {
        return groupControlState;
    }

    public void setGroupControlState(int groupControlState) {
        this.groupControlState = groupControlState;
    }

    public GregorianCalendar getLastControlSent() {
        return lastControlSent;
    }

    public void setLastControlSent(GregorianCalendar lastControlSent) {
        this.lastControlSent = lastControlSent;
    }

    public GregorianCalendar getNextControlTime() {
        return nextControlTime;
    }

    public void setNextControlTime(GregorianCalendar nextControlTime) {
        this.nextControlTime = nextControlTime;
    }

    public int getInternalState() {
        return internalState;
    }

    public void setInternalState(int internalState) {
        this.internalState = internalState;
    }
}
