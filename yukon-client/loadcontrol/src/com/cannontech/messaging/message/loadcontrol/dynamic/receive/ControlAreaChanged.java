package com.cannontech.messaging.message.loadcontrol.dynamic.receive;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import com.cannontech.messaging.message.BaseMessage;

public class ControlAreaChanged extends BaseMessage {

    private int paoId;
    private boolean disableFlag;
    private GregorianCalendar nextCheckTime = null;
    private int controlAreaStatusPointId;
    private int controlAreaState;
    private int currentPriority;
    private int currentDailyStartTime;
    private int currentDailyStopTime;

    private List<TriggerChanged> triggers = new ArrayList<TriggerChanged>();

    public int getControlAreaState() {
        return controlAreaState;
    }

    public void setControlAreaState(int controlAreaState) {
        this.controlAreaState = controlAreaState;
    }

    public int getControlAreaStatusPointId() {
        return controlAreaStatusPointId;
    }

    public void setControlAreaStatusPointId(int controlAreaStatusPointId) {
        this.controlAreaStatusPointId = controlAreaStatusPointId;
    }

    public int getCurrentDailyStartTime() {
        return currentDailyStartTime;
    }

    public void setCurrentDailyStartTime(int currentDailyStartTime) {
        this.currentDailyStartTime = currentDailyStartTime;
    }

    public int getCurrentDailyStopTime() {
        return currentDailyStopTime;
    }

    public void setCurrentDailyStopTime(int currentDailyStopTime) {
        this.currentDailyStopTime = currentDailyStopTime;
    }

    public int getCurrentPriority() {
        return currentPriority;
    }

    public void setCurrentPriority(int currentPriority) {
        this.currentPriority = currentPriority;
    }

    public boolean getDisableFlag() {
        return disableFlag;
    }

    public void setDisableFlag(boolean disableFlag) {
        this.disableFlag = disableFlag;
    }

    public GregorianCalendar getNextCheckTime() {
        return nextCheckTime;
    }

    public void setNextCheckTime(GregorianCalendar nextCheckTime) {
        this.nextCheckTime = nextCheckTime;
    }

    public int getPaoId() {
        return paoId;
    }

    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }

    public List<TriggerChanged> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<TriggerChanged> triggers) {
        this.triggers = triggers;
    }
}
