package com.cannontech.loadcontrol.dynamic.receive;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import com.cannontech.message.util.Message;

/**
 * Insert the type's description here.
 * Creation date: (9/5/07 3:06:09 PM)
 * @author: jdayton
 */
public class LMControlAreaChanged extends Message {
    
    private Integer paoID = null;
    private Boolean disableFlag = null;
    private GregorianCalendar nextCheckTime = null;
    private Integer controlAreaStatusPointId = null;
    private Integer controlAreaState = null;
    private Integer currentPriority = null;
    private Integer currentDailyStartTime = null;
    private Integer currentDailyStopTime = null;
    
    private List<LMTriggerChanged> triggers = new ArrayList<LMTriggerChanged>();
    
    public LMControlAreaChanged() {
        super();
    }

    public Integer getControlAreaState() {
        return controlAreaState;
    }
    
    public void setControlAreaState(Integer controlAreaState) {
        this.controlAreaState = controlAreaState;
    }
    
    public Integer getControlAreaStatusPointId() {
        return controlAreaStatusPointId;
    }
    
    public void setControlAreaStatusPointId(Integer controlAreaStatusPointId) {
        this.controlAreaStatusPointId = controlAreaStatusPointId;
    }
    
    public Integer getCurrentDailyStartTime() {
        return currentDailyStartTime;
    }
    
    public void setCurrentDailyStartTime(Integer currentDailyStartTime) {
        this.currentDailyStartTime = currentDailyStartTime;
    }
    
    public Integer getCurrentDailyStopTime() {
        return currentDailyStopTime;
    }
    
    public void setCurrentDailyStopTime(Integer currentDailyStopTime) {
        this.currentDailyStopTime = currentDailyStopTime;
    }
    
    public Integer getCurrentPriority() {
        return currentPriority;
    }
    
    public void setCurrentPriority(Integer currentPriority) {
        this.currentPriority = currentPriority;
    }
    
    public Boolean getDisableFlag() {
        return disableFlag;
    }
    
    public void setDisableFlag(Boolean disableFlag) {
        this.disableFlag = disableFlag;
    }
    
    public GregorianCalendar getNextCheckTime() {
        return nextCheckTime;
    }
    
    public void setNextCheckTime(GregorianCalendar nextCheckTime) {
        this.nextCheckTime = nextCheckTime;
    }
    
    public Integer getPaoID() {
        return paoID;
    }
    
    public void setPaoID(Integer paoID) {
        this.paoID = paoID;
    }

    public List<LMTriggerChanged> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<LMTriggerChanged> triggers) {
        this.triggers = triggers;
    }
}