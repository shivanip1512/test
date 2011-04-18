package com.cannontech.loadcontrol.data;

import java.util.Vector;

import com.cannontech.common.pao.PaoType;

public class LMControlArea implements ILMData, Cloneable {
	
	public static final int INVALID_INT = -1;

	public static final int STATE_INACTIVE = 0;
	public static final int STATE_ACTIVE = 1;
	public static final int STATE_MANUAL_ACTIVE = 2;
	/* The SCHEDULED state is never set */
	// public static final int STATE_SCHEDULED = 3;
	public static final int STATE_FULLY_ACTIVE = 4;
	public static final int STATE_CNTRL_ATTEMPT = 5;

	private Integer yukonID = null;
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

	private Vector<LMControlAreaTrigger> triggerVector = null;

	// LMProgramBase Objects
	private Vector<LMProgramBase> lmProgramVector = null;

	public LMControlArea clone() {
		LMControlArea clone = cloneKeepingPrograms();
		if (lmProgramVector != null) {
			clone.lmProgramVector = new Vector<LMProgramBase>();
			for (LMProgramBase program : lmProgramVector) {
				clone.lmProgramVector.add(program.clone());
			}
		}
		return clone;
	}

	public LMControlArea cloneKeepingPrograms() {
		LMControlArea clone;
		try {
			clone = (LMControlArea) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		if (triggerVector != null) {
			clone.triggerVector = new Vector<LMControlAreaTrigger>();
			for (LMControlAreaTrigger trigger : triggerVector) {
				clone.triggerVector.add(trigger.clone());
			}
		}
		return clone;
	}

	public LMControlArea cloneUpdatingProgram(LMProgramBase newProgram) {
		LMControlArea clone = cloneKeepingPrograms();
		if (lmProgramVector != null) {
			clone.lmProgramVector = new Vector<LMProgramBase>();
			for (LMProgramBase program : lmProgramVector) {
				if (program.getYukonID().equals(newProgram.getYukonID())) {
					clone.lmProgramVector.add(newProgram);
				} else {
					clone.lmProgramVector.add(program);
				}
			}
		}
		return clone;
	}

	public boolean equals(Object val) {
		if (val instanceof LMControlArea) {
			return (getYukonID().intValue() == ((LMControlArea) val)
					.getYukonID().intValue());
		} else
			return super.equals(val);
	}

	public int hashCode() {
		return getYukonID().intValue();
	}

	public java.lang.Integer getControlAreaState() {
		return controlAreaState;
	}

	public static java.awt.Color getControlAreaStateColor(
			LMControlArea areaValue) {
		if (areaValue.getDisableFlag().booleanValue())
			return java.awt.Color.red;
		else if (areaValue.getControlAreaState().intValue() == LMControlArea.STATE_CNTRL_ATTEMPT)
		// || areaValue.getControlAreaState().intValue() ==
		// LMControlArea.STATE_SCHEDULED )
		{
			return java.awt.Color.yellow;
		} else if (areaValue.getControlAreaState().intValue() == LMControlArea.STATE_INACTIVE)
			return java.awt.Color.black;
		else
			return java.awt.Color.green.darker();
	}

	public static String getControlAreaStateString(int state) {
		switch (state) {
		case STATE_INACTIVE:
			return "INACTIVE";

		case STATE_ACTIVE:
			return "ACTIVE";

		case STATE_MANUAL_ACTIVE:
			return "MANUAL ACTIVE";

			// case STATE_SCHEDULED:
			// return "SCHEDULED";

		case STATE_FULLY_ACTIVE:
			return "FULLY ACTIVE";

		case STATE_CNTRL_ATTEMPT:
			return "CONTROL ATTEMPT";

		default:
			throw new RuntimeException("*** Unknown state(" + state
					+ ") in getControlAreaStateString(int) in : "
					+ LMControlArea.class.getName());
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
		return (getCurrentDailyStartTime() >= 0) ? getCurrentDailyStartTime()
				: getDefDailyStartTime();
	}

	public java.lang.Integer getDailyStopTime() {
		return (getCurrentDailyStopTime() >= 0) ? getCurrentDailyStopTime()
				: getDefDailyStopTime();
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

	public Vector<LMProgramBase> getLmProgramVector() {
		if (lmProgramVector == null)
			lmProgramVector = new Vector<LMProgramBase>(10);

		return lmProgramVector;
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

	public Vector<LMControlAreaTrigger> getTriggerVector() {
		if (triggerVector == null)
			triggerVector = new Vector<LMControlAreaTrigger>(2);

		return triggerVector;
	}

	public LMControlAreaTrigger getTrigger(int triggerNumber) {
		for (LMControlAreaTrigger trigger : getTriggerVector()) {
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

	public java.lang.Integer getYukonID() {
		return yukonID;
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

	public void setControlAreaStatusPointId(
			java.lang.Integer newControlAreaStatusPointId) {
		controlAreaStatusPointId = newControlAreaStatusPointId;
	}

	public void setControlInterval(java.lang.Integer newControlInterval) {
		controlInterval = newControlInterval;
	}

	public void setCurrentDailyStartTime(
			java.lang.Integer newCurrentDailyStartTime) {
		currentDailyStartTime = newCurrentDailyStartTime;
	}

	public void setCurrentDailyStopTime(
			java.lang.Integer newCurrentDailyStopTime) {
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

	public void setLmProgramVector(Vector<LMProgramBase> newLmProgramVector) {
		lmProgramVector = newLmProgramVector;
	}

	public void setMinResponseTime(java.lang.Integer newMinResponseTime) {
		minResponseTime = newMinResponseTime;
	}

	public void setNewPointDataReceivedFlag(
			java.lang.Boolean newNewPointDataReceivedFlag) {
		newPointDataReceivedFlag = newNewPointDataReceivedFlag;
	}

	public void setNextCheckTime(java.util.GregorianCalendar newNextCheckTime) {
		nextCheckTime = newNextCheckTime;
	}

	public void setRequireAllTriggersActiveFlag(
			java.lang.Boolean newRequireAllTriggersActiveFlag) {
		requireAllTriggersActiveFlag = newRequireAllTriggersActiveFlag;
	}

	public void setTriggerVector(Vector<LMControlAreaTrigger> newTriggerVector) {
		triggerVector = newTriggerVector;
	}

	public void setUpdatedFlag(java.lang.Boolean newUpdatedFlag) {
		updatedFlag = newUpdatedFlag;
	}

	public void setYukonDescription(java.lang.String newYukonDescription) {
		yukonDescription = newYukonDescription;
	}

	public void setYukonID(java.lang.Integer newYukonID) {
		yukonID = newYukonID;
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
