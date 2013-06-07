package com.cannontech.messaging.message.loadcontrol.data;

import java.util.Vector;

import com.cannontech.common.pao.PaoType;

public class ControlAreaItem implements Data, Cloneable {

    public static final int INVALID_INT = -1;

    public static final int STATE_INACTIVE = 0;
    public static final int STATE_PARTIALLY_ACTIVE = 1;
    public static final int STATE_MANUAL_ACTIVE = 2;
    public static final int STATE_FULLY_SCHEDULED = 3;
    public static final int STATE_FULLY_ACTIVE = 4;
    public static final int STATE_CNTRL_ATTEMPT = 5;
    public static final int STATE_PARTIALLY_SCHEDULED = 6;

    private Integer yukonId = null;
    private String yukonName = null;
    private PaoType yukonType = null;
    private String yukonDescription = null;
    private Boolean disableFlag = null;
    private String defOperationalState = null;
    private Integer controlInterval = null;
    private Integer minResponseTime = null;
    private Integer defDailyStartTime = null;
    private Integer defDailyStopTime = null;
    private Boolean requireAllTriggersActiveFlag = null;
    private java.util.GregorianCalendar nextCheckTime = null;
    private Boolean newPointDataReceivedFlag = null;
    private Boolean updatedFlag = null;
    private Integer controlAreaStatusPointId = null;
    private Integer controlAreaState = null;
    private Integer currentPriority = null;
    private Integer currentDailyStartTime = null;
    private Integer currentDailyStopTime = null;

    private Vector<ControlAreaTriggerItem> triggerVector = null;

    // ProgramBase Objects
    private Vector<Program> programVector = null;

    public ControlAreaItem clone() {
        ControlAreaItem clone = cloneKeepingPrograms();
        if (programVector != null) {
            clone.programVector = new Vector<Program>();
            for (Program program : programVector) {
                clone.programVector.add(program.clone());
            }
        }
        return clone;
    }

    public ControlAreaItem cloneKeepingPrograms() {
        ControlAreaItem clone;
        try {
            clone = (ControlAreaItem) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        if (triggerVector != null) {
            clone.triggerVector = new Vector<ControlAreaTriggerItem>();
            for (ControlAreaTriggerItem trigger : triggerVector) {
                clone.triggerVector.add(trigger.clone());
            }
        }
        return clone;
    }

    public ControlAreaItem cloneUpdatingProgram(Program newProgram) {
        ControlAreaItem clone = cloneKeepingPrograms();
        if (programVector != null) {
            clone.programVector = new Vector<Program>();
            for (Program program : programVector) {
                if (program.getYukonId().equals(newProgram.getYukonId())) {
                    clone.programVector.add(newProgram);
                }
                else {
                    clone.programVector.add(program);
                }
            }
        }
        return clone;
    }

    public boolean equals(Object val) {
        if (val instanceof ControlAreaItem) {
            return (getYukonId().intValue() == ((ControlAreaItem) val).getYukonId().intValue());
        }
        else
            return super.equals(val);
    }

    public int hashCode() {
        return getYukonId().intValue();
    }

    public java.lang.Integer getControlAreaState() {
        return controlAreaState;
    }

    public static java.awt.Color getControlAreaStateColor(ControlAreaItem areaValue) {
        if (areaValue.getDisableFlag().booleanValue())
            return java.awt.Color.red;
        else if (areaValue.getControlAreaState().intValue() == ControlAreaItem.STATE_CNTRL_ATTEMPT) {
            return java.awt.Color.yellow;
        }
        else if (areaValue.getControlAreaState().intValue() == ControlAreaItem.STATE_FULLY_SCHEDULED
            || areaValue.getControlAreaState().intValue() == ControlAreaItem.STATE_PARTIALLY_SCHEDULED) {
            return java.awt.Color.orange;
        }
        else if (areaValue.getControlAreaState().intValue() == ControlAreaItem.STATE_INACTIVE)
            return java.awt.Color.black;
        else
            return java.awt.Color.green.darker();
    }

    public static String getControlAreaStateString(int state) {
        switch (state) {
            case STATE_INACTIVE:
                return "INACTIVE";

            case STATE_PARTIALLY_ACTIVE:
                return "PARTIALLY ACTIVE";

            case STATE_MANUAL_ACTIVE:
                return "MANUAL ACTIVE";

            case STATE_FULLY_SCHEDULED:
                return "SCHEDULED";

            case STATE_PARTIALLY_SCHEDULED:
                return "PARTIALLY SCHEDULED";

            case STATE_FULLY_ACTIVE:
                return "ACTIVE";

            case STATE_CNTRL_ATTEMPT:
                return "CONTROL ATTEMPT";

            default:
                throw new RuntimeException("*** Unknown state(" + state + ") in getControlAreaStateString(int) in : "
                    + ControlAreaItem.class.getName());
        }

    }

    public java.lang.Integer getControlAreaStatusPointId() {
        return controlAreaStatusPointId;
    }

    public java.lang.Integer getControlInterval() {
        return controlInterval;
    }

    public java.lang.Integer getCurrentDailyStartTime() {
        return currentDailyStartTime;
    }

    public java.lang.Integer getCurrentDailyStopTime() {
        return currentDailyStopTime;
    }

    public java.lang.Integer getDailyStartTime() {
        return (getCurrentDailyStartTime() >= 0) ? getCurrentDailyStartTime() : getDefDailyStartTime();
    }

    public java.lang.Integer getDailyStopTime() {
        return (getCurrentDailyStopTime() >= 0) ? getCurrentDailyStopTime() : getDefDailyStopTime();
    }

    public java.lang.Integer getCurrentPriority() {
        return currentPriority;
    }

    public java.lang.Integer getDefDailyStartTime() {
        return defDailyStartTime;
    }

    public java.lang.Integer getDefDailyStopTime() {
        return defDailyStopTime;
    }

    public java.lang.String getDefOperationalState() {
        return defOperationalState;
    }

    public java.lang.Boolean getDisableFlag() {
        return disableFlag;
    }

    public Vector<Program> getProgramVector() {
        if (programVector == null)
            programVector = new Vector<Program>(10);

        return programVector;
    }

    public java.lang.Integer getMinResponseTime() {
        return minResponseTime;
    }

    public java.lang.Boolean getNewPointDataReceivedFlag() {
        return newPointDataReceivedFlag;
    }

    public java.util.GregorianCalendar getNextCheckTime() {
        return nextCheckTime;
    }

    public java.lang.Boolean getRequireAllTriggersActiveFlag() {
        return requireAllTriggersActiveFlag;
    }

    public Vector<ControlAreaTriggerItem> getTriggerVector() {
        if (triggerVector == null)
            triggerVector = new Vector<ControlAreaTriggerItem>(2);

        return triggerVector;
    }

    public ControlAreaTriggerItem getTrigger(int triggerNumber) {
        for (ControlAreaTriggerItem trigger : getTriggerVector()) {
            if (trigger.getTriggerNumber() == triggerNumber) {
                return trigger;
            }
        }
        return null;
    }

    public java.lang.Boolean getUpdatedFlag() {
        return updatedFlag;
    }

    public java.lang.String getYukonDescription() {
        return yukonDescription;
    }

    public java.lang.Integer getYukonId() {
        return yukonId;
    }

    public java.lang.String getYukonName() {
        return yukonName;
    }

    public PaoType getYukonType() {
        return yukonType;
    }

    public void setControlAreaState(java.lang.Integer newControlAreaState) {
        controlAreaState = newControlAreaState;
    }

    public void setControlAreaStatusPointId(java.lang.Integer newControlAreaStatusPointId) {
        controlAreaStatusPointId = newControlAreaStatusPointId;
    }

    public void setControlInterval(java.lang.Integer newControlInterval) {
        controlInterval = newControlInterval;
    }

    public void setCurrentDailyStartTime(java.lang.Integer newCurrentDailyStartTime) {
        currentDailyStartTime = newCurrentDailyStartTime;
    }

    public void setCurrentDailyStopTime(java.lang.Integer newCurrentDailyStopTime) {
        currentDailyStopTime = newCurrentDailyStopTime;
    }

    public void setCurrentPriority(java.lang.Integer newCurrentPriority) {
        currentPriority = newCurrentPriority;
    }

    public void setDefDailyStartTime(java.lang.Integer newDefDailyStartTime) {
        defDailyStartTime = newDefDailyStartTime;
    }

    public void setDefDailyStopTime(java.lang.Integer newDefDailyStopTime) {
        defDailyStopTime = newDefDailyStopTime;
    }

    public void setDefOperationalState(java.lang.String newDefOperationalState) {
        defOperationalState = newDefOperationalState;
    }

    public void setDisableFlag(java.lang.Boolean newDisableFlag) {
        disableFlag = newDisableFlag;
    }

    public void setProgramVector(Vector<Program> newProgramVector) {
        programVector = newProgramVector;
    }

    public void setMinResponseTime(java.lang.Integer newMinResponseTime) {
        minResponseTime = newMinResponseTime;
    }

    public void setNewPointDataReceivedFlag(java.lang.Boolean newNewPointDataReceivedFlag) {
        newPointDataReceivedFlag = newNewPointDataReceivedFlag;
    }

    public void setNextCheckTime(java.util.GregorianCalendar newNextCheckTime) {
        nextCheckTime = newNextCheckTime;
    }

    public void setRequireAllTriggersActiveFlag(java.lang.Boolean newRequireAllTriggersActiveFlag) {
        requireAllTriggersActiveFlag = newRequireAllTriggersActiveFlag;
    }

    public void setTriggerVector(Vector<ControlAreaTriggerItem> newTriggerVector) {
        triggerVector = newTriggerVector;
    }

    public void setUpdatedFlag(java.lang.Boolean newUpdatedFlag) {
        updatedFlag = newUpdatedFlag;
    }

    public void setYukonDescription(java.lang.String newYukonDescription) {
        yukonDescription = newYukonDescription;
    }

    public void setYukonId(java.lang.Integer newYukonId) {
        yukonId = newYukonId;
    }

    public void setYukonName(java.lang.String newYukonName) {
        yukonName = newYukonName;
    }

    public void setYukonType(PaoType newYukonType) {
        yukonType = newYukonType;
    }

    public String oldtoString() {
        return getYukonName();
    }
}
