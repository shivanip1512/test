package com.cannontech.messaging.message.loadcontrol.dynamic.receive;

import java.util.GregorianCalendar;

import com.cannontech.messaging.message.BaseMessage;

public class ProgramChanged extends BaseMessage {

    private int paoId;
    private boolean disableFlag;
    private int currentGearNumber;
    private int lastGroupControlled;
    private int programState;
    private double reductionTotal;
    private GregorianCalendar directStartTime = null;
    private GregorianCalendar directStopTime = null;
    private GregorianCalendar notifyActiveTime = null;
    private GregorianCalendar notifyInactiveTime = null;
    private GregorianCalendar startedRampingOutTime = null;

    public boolean getDisableFlag() {
        return disableFlag;
    }

    public void setDisableFlag(boolean disableFlag) {
        this.disableFlag = disableFlag;
    }

    public GregorianCalendar getNextCheckTime() {
        return directStartTime;
    }

    public void setNextCheckTime(GregorianCalendar nextCheckTime) {
        this.directStartTime = nextCheckTime;
    }

    public int getPaoId() {
        return paoId;
    }

    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }

    public int getCurrentGearNumber() {
        return currentGearNumber;
    }

    public void setCurrentGearNumber(int currentGearNumber) {
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

    public int getLastGroupControlled() {
        return lastGroupControlled;
    }

    public void setLastGroupControlled(int lastGroupControlled) {
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

    public int getProgramState() {
        return programState;
    }

    public void setProgramState(int programState) {
        this.programState = programState;
    }

    public double getReductionTotal() {
        return reductionTotal;
    }

    public void setReductionTotal(double reductionTotal) {
        this.reductionTotal = reductionTotal;
    }

    public GregorianCalendar getStartedRampingOutTime() {
        return startedRampingOutTime;
    }

    public void setStartedRampingOutTime(GregorianCalendar startedRampingOutTime) {
        this.startedRampingOutTime = startedRampingOutTime;
    }
}
