package com.cannontech.loadcontrol.dynamic.receive;

import java.util.GregorianCalendar;

import com.cannontech.message.util.Message;


/**
 * Insert the type's description here.
 * Creation date: (9/5/07 3:06:09 PM)
 * @author: jdayton
 */
public class LMGroupChanged extends Message {
    
    private Integer paoID = null;
    private Boolean disableFlag = null;
    private Integer groupControlState = null;
    private Integer currentHoursDaily = null;
    private Integer currentHoursMonthly = null;
    private Integer currentHoursSeasonal = null;
    private Integer currentHoursAnnually = null;
    private GregorianCalendar lastControlSent = null;
    private GregorianCalendar controlStartTime = null;
    private GregorianCalendar controlCompleteTime = null;
    private GregorianCalendar nextControlTime = null;
    private Integer internalState = null;
    private Integer dailyOps = null;
    
    public LMGroupChanged() {
        super();
    }

    public Boolean getDisableFlag() {
        return disableFlag;
    }
    
    public void setDisableFlag(Boolean disableFlag) {
        this.disableFlag = disableFlag;
    }
    
    public Integer getPaoID() {
        return paoID;
    }
    
    public void setPaoID(Integer paoID) {
        this.paoID = paoID;
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

    public Integer getCurrentHoursAnnually() {
        return currentHoursAnnually;
    }

    public void setCurrentHoursAnnually(Integer currentHoursAnnually) {
        this.currentHoursAnnually = currentHoursAnnually;
    }

    public Integer getCurrentHoursDaily() {
        return currentHoursDaily;
    }

    public void setCurrentHoursDaily(Integer currentHoursDaily) {
        this.currentHoursDaily = currentHoursDaily;
    }

    public Integer getCurrentHoursMonthly() {
        return currentHoursMonthly;
    }

    public void setCurrentHoursMonthly(Integer currentHoursMonthly) {
        this.currentHoursMonthly = currentHoursMonthly;
    }

    public Integer getCurrentHoursSeasonal() {
        return currentHoursSeasonal;
    }

    public void setCurrentHoursSeasonal(Integer currentHoursSeasonal) {
        this.currentHoursSeasonal = currentHoursSeasonal;
    }

    public Integer getDailyOps() {
        return dailyOps;
    }

    public void setDailyOps(Integer dailyOps) {
        this.dailyOps = dailyOps;
    }

    public Integer getGroupControlState() {
        return groupControlState;
    }

    public void setGroupControlState(Integer groupControlState) {
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

    public Integer getInternalState() {
        return internalState;
    }

    public void setInternalState(Integer internalState) {
        this.internalState = internalState;
    }
}