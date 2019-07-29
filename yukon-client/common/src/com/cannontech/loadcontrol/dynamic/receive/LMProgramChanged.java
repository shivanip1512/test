package com.cannontech.loadcontrol.dynamic.receive;

import java.util.GregorianCalendar;

import com.cannontech.message.util.Message;

/**
 * Insert the type's description here.
 * Creation date: (9/5/07 3:06:09 PM)
 * @author: jdayton
 */
public class LMProgramChanged extends Message {
    
    private Integer paoID = null;
    private Boolean disableFlag = null;
    private Integer currentGearNumber = null;
    private Integer lastGroupControlled = null;
    private Integer programState = null;
    private Double reductionTotal = null;
    private GregorianCalendar directStartTime = null;
    private GregorianCalendar directStopTime = null;
    private GregorianCalendar notifyActiveTime = null;
    private GregorianCalendar notifyInactiveTime = null;
    private GregorianCalendar startedRampingOutTime = null;
    private String originSource;

    public LMProgramChanged() {
        super();
    }

    public Boolean getDisableFlag() {
        return disableFlag;
    }
    
    public void setDisableFlag(Boolean disableFlag) {
        this.disableFlag = disableFlag;
    }
    
    public GregorianCalendar getNextCheckTime() {
        return directStartTime;
    }
    
    public void setNextCheckTime(GregorianCalendar nextCheckTime) {
        this.directStartTime = nextCheckTime;
    }
    
    public Integer getPaoID() {
        return paoID;
    }
    
    public void setPaoID(Integer paoID) {
        this.paoID = paoID;
    }

    public Integer getCurrentGearNumber() {
        return currentGearNumber;
    }

    public void setCurrentGearNumber(Integer currentGearNumber) {
        this.currentGearNumber = currentGearNumber;
    }

    public GregorianCalendar getDirectStartTime() {
        return directStartTime;
    }

    public void setDirectStartTime(GregorianCalendar directStartTime) {
        this.directStartTime = directStartTime;
    }

    public GregorianCalendar getDirectStopTime() {
        return directStopTime;
    }

    public void setDirectStopTime(GregorianCalendar directStopTime) {
        this.directStopTime = directStopTime;
    }

    public Integer getLastGroupControlled() {
        return lastGroupControlled;
    }

    public void setLastGroupControlled(Integer lastGroupControlled) {
        this.lastGroupControlled = lastGroupControlled;
    }

    public GregorianCalendar getNotifyActiveTime() {
        return notifyActiveTime;
    }

    public void setNotifyActiveTime(GregorianCalendar notifyActiveTime) {
        this.notifyActiveTime = notifyActiveTime;
    }

    public GregorianCalendar getNotifyInactiveTime() {
        return notifyInactiveTime;
    }

    public void setNotifyInactiveTime(GregorianCalendar notifyInactiveTime) {
        this.notifyInactiveTime = notifyInactiveTime;
    }

    public Integer getProgramState() {
        return programState;
    }

    public void setProgramState(Integer programState) {
        this.programState = programState;
    }

    public Double getReductionTotal() {
        return reductionTotal;
    }

    public void setReductionTotal(Double reductionTotal) {
        this.reductionTotal = reductionTotal;
    }

    public GregorianCalendar getStartedRampingOutTime() {
        return startedRampingOutTime;
    }

    public void setStartedRampingOutTime(GregorianCalendar startedRampingOutTime) {
        this.startedRampingOutTime = startedRampingOutTime;
    }

    public String getOriginSource() {
        return originSource;
    }

    public void setOriginSource(String originSource) {
        this.originSource = originSource;
    }
}