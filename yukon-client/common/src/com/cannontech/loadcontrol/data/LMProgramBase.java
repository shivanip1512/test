package com.cannontech.loadcontrol.data;

import java.util.List;
import java.util.Vector;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.dr.program.model.ProgramState;

public abstract class LMProgramBase implements ILMProgramMessageCreation, ILMData, Cloneable, YukonPao {
	
	// constants that must match values in lmprogrambase.h/lmprogrambase.cpp
	// control types
	public static final String CONTROL_AUTOMATIC = "Automatic";
	public static final String CONTROL_TIMED = "Timed";
	public static final String CONTROL_MANUAL = "ManualOnly";

	// status of a LMProgramBase, programStatus values
	public static final int STATUS_INACTIVE = 0; // start only
	public static final int STATUS_ACTIVE = 1; // active
	public static final int STATUS_MANUAL_ACTIVE = 2; // active
	public static final int STATUS_SCHEDULED = 3; // scheduled
	public static final int STATUS_NOTIFIED = 4; // notified
	public static final int STATUS_FULL_ACTIVE = 5;
	public static final int STATUS_STOPPING = 6;
	public static final int STATUS_CNTRL_ATTEMPT = 7;
	public static final int STATUS_NON_CNTRL = 8;
	public static final int STATUS_TIMED_ACTIVE = 9;

	private Integer yukonID = null;
	private String yukonName = null;
	private PaoType yukonType = null;
	private String yukonDescription = null;
	private Boolean disableFlag = null;
	private Integer startPriority = null;
	private Integer stopPriority = null;
	private String controlType = null;
	private String availableWeekDays = null;
	private Integer maxHoursDaily = null;
	private Integer maxHoursMonthly = null;
	private Integer maxHoursSeasonal = null;
	private Integer maxHoursAnnually = null;
	private Integer minActivateTime = null;
	private Integer minResponseTime = null;
	private Integer programStatusPointID = null;
	private Integer programStatus = null;
	private Integer reductionAnalogPointId = null;
	private Double reductionTotal = null;
	private java.util.GregorianCalendar startedControlling = null;
	private java.util.GregorianCalendar lastControlSent = null;
	private Boolean manualControlReceivedFlag = null;

	private java.util.Vector controlWindowVector = null;

	// contains objects of type ILMGroup
	private List<LMGroupBase> loadControlGroupVector = null;

	// data not restored when sent/received
	private java.util.GregorianCalendar stoppedControlling = null;

	public LMProgramBase clone() {
		LMProgramBase clone = cloneKeepingLoadGroups();
		if (loadControlGroupVector != null) {
			clone.loadControlGroupVector = new Vector<LMGroupBase>();
			for (LMGroupBase group : loadControlGroupVector) {
				clone.loadControlGroupVector.add(group.clone());
			}
		}
		return clone;
	}

	public LMProgramBase cloneKeepingLoadGroups() {
		LMProgramBase clone;
		try {
			clone = (LMProgramBase) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		return clone;
	}

	public LMProgramBase cloneUpdatingLoadGroup(LMGroupBase newGroup) {
		LMProgramBase clone = cloneKeepingLoadGroups();
		if (loadControlGroupVector != null) {
			clone.loadControlGroupVector = new Vector<LMGroupBase>();
			for (LMGroupBase loadGroup : loadControlGroupVector) {
				if (loadGroup.getYukonID().equals(newGroup.getYukonID())) {
					clone.loadControlGroupVector.add(newGroup);
				} else {
					clone.loadControlGroupVector.add(loadGroup);
				}
			}
		}
		return clone;
	}

	public Integer getStartPriority() {
		return startPriority;
	}

	public void setStartPriority(Integer startPriority) {
		this.startPriority = startPriority;
	}

	public Integer getStopPriority() {
		return stopPriority;
	}

	public void setStopPriority(Integer stopPriority) {
		this.stopPriority = stopPriority;
	}

	public boolean equals(Object o) {
		return ((o != null) && (o instanceof LMProgramBase) && ((LMProgramBase) o)
				.getYukonID().equals(getYukonID()));
	}

	public java.lang.String getAvailableWeekDays() {
		return availableWeekDays;
	}

	public java.lang.String getControlType() {
		return controlType;
	}

	public java.util.Vector getControlWindowVector() {
		return controlWindowVector;
	}

	public java.lang.Boolean getDisableFlag() {
		return disableFlag;
	}

	public java.util.GregorianCalendar getLastControlSent() {
		return lastControlSent;
	}

	public List<LMGroupBase> getLoadControlGroupVector() {
		if (loadControlGroupVector == null)
			loadControlGroupVector = new Vector<LMGroupBase>(10);

		return loadControlGroupVector;
	}

	public java.lang.Boolean getManualControlReceivedFlag() {
		return manualControlReceivedFlag;
	}

	public java.lang.Integer getMaxHoursAnnually() {
		return maxHoursAnnually;
	}

	public java.lang.Integer getMaxHoursDaily() {
		return maxHoursDaily;
	}

	public java.lang.Integer getMaxHoursMonthly() {
		return maxHoursMonthly;
	}

	public java.lang.Integer getMaxHoursSeasonal() {
		return maxHoursSeasonal;
	}

	public java.lang.Integer getMinActivateTime() {
		return minActivateTime;
	}

	public java.lang.Integer getMinResponseTime() {
		return minResponseTime;
	}

	public java.lang.Integer getProgramStatus() {
		return programStatus;
	}

	public ProgramState getProgramState() {
		return ProgramState.valueOf(programStatus);
	}

	public java.lang.Integer getProgramStatusPointID() {
		return programStatusPointID;
	}

	/**
	 * @deprecated Use internationalized strings in demandResponse.xml instead.
	 */
	public static String getProgramStatusString(int status) {
		switch (status) {
		case STATUS_INACTIVE:
			return "Inactive";

		case STATUS_ACTIVE:
			return "Active";

		case STATUS_MANUAL_ACTIVE:
			return "Manual Active";

		case STATUS_SCHEDULED:
			return "Scheduled";

		case STATUS_NOTIFIED:
			return "Notified";

		case STATUS_FULL_ACTIVE:
			return "Full Active";

		case STATUS_NON_CNTRL:
			return "Control Attempt";

		case STATUS_STOPPING:
			return "Stopping";

		case STATUS_TIMED_ACTIVE:
			return "Timed Active";

		default:
			throw new RuntimeException("*** Unknown status(" + status
					+ ") in getProgramStatusString(int) in : "
					+ LMProgramBase.class.getName());
		}

	}

	public java.lang.Integer getReductionAnalogPointId() {
		return reductionAnalogPointId;
	}

	public java.lang.Double getReductionTotal() {
		return reductionTotal;
	}

	public java.util.GregorianCalendar getStartedControlling() {
		return startedControlling;
	}

	public abstract java.util.GregorianCalendar getStartTime();

	public java.util.GregorianCalendar getStoppedControlling() {
		return stoppedControlling;
	}

	public abstract java.util.GregorianCalendar getStopTime();

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

	public int hashCode() {
		return getYukonID().intValue();
	}

	public void setAvailableWeekDays(java.lang.String newAvailableWeekDays) {
		availableWeekDays = newAvailableWeekDays;
	}

	public void setControlType(java.lang.String newControlType) {
		controlType = newControlType;
	}

	public void setControlWindowVector(java.util.Vector newControlWindowVector) {
		controlWindowVector = newControlWindowVector;
	}

	public void setDisableFlag(java.lang.Boolean newDisableFlag) {
		disableFlag = newDisableFlag;
	}

	public void setLastControlSent(
			java.util.GregorianCalendar newLastControlSent) {
		lastControlSent = newLastControlSent;
	}

	public void setLoadControlGroupVector(
			List<LMGroupBase> newLoadControlGroupVector) {
		loadControlGroupVector = newLoadControlGroupVector;
	}

	public void setManualControlReceivedFlag(
			java.lang.Boolean newManualControlReceivedFlag) {
		manualControlReceivedFlag = newManualControlReceivedFlag;
	}

	public void setMaxHoursAnnually(java.lang.Integer newMaxHoursAnnually) {
		maxHoursAnnually = newMaxHoursAnnually;
	}

	public void setMaxHoursDaily(java.lang.Integer newMaxHoursDaily) {
		maxHoursDaily = newMaxHoursDaily;
	}

	public void setMaxHoursMonthly(java.lang.Integer newMaxHoursMonthly) {
		maxHoursMonthly = newMaxHoursMonthly;
	}

	public void setMaxHoursSeasonal(java.lang.Integer newMaxHoursSeasonal) {
		maxHoursSeasonal = newMaxHoursSeasonal;
	}

	public void setMinActivateTime(java.lang.Integer newMinActivateTime) {
		minActivateTime = newMinActivateTime;
	}

	public void setMinResponseTime(java.lang.Integer newMinResponseTime) {
		minResponseTime = newMinResponseTime;
	}

	public void setProgramStatus(java.lang.Integer newProgramStatus) {
		programStatus = newProgramStatus;
	}

	public void setProgramStatusPointID(
			java.lang.Integer newProgramStatusPointID) {
		programStatusPointID = newProgramStatusPointID;
	}

	public void setReductionAnalogPointId(
			java.lang.Integer newReductionAnalogPointId) {
		reductionAnalogPointId = newReductionAnalogPointId;
	}

	public void setReductionTotal(java.lang.Double newReductionTotal) {
		reductionTotal = newReductionTotal;
	}

	public void setStartedControlling(
			java.util.GregorianCalendar newStartedControlling) {
		startedControlling = newStartedControlling;
	}

	public void setStoppedControlling(
			java.util.GregorianCalendar newStoppedControlling) {
		stoppedControlling = newStoppedControlling;
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

    public String getOriginSource() {
        return "";
    }

	public String toString() {
		return getYukonName();
	}

	/**
	 * Returns true if any of the groups in this program are Ramping In
	 * 
	 * @return
	 */
	public boolean isRampingIn() {
		for (int i = 0; i < getLoadControlGroupVector().size(); i++) {
			ILMGroup grp = (ILMGroup) getLoadControlGroupVector().get(i);
			if (grp.isRampingIn())
				return true;
		}

		return false;
	}

	/**
	 * Returns true if any of the groups in this program are Ramping Out
	 * 
	 * @return
	 */
	public boolean isRampingOut() {
		for (int i = 0; i < getLoadControlGroupVector().size(); i++) {
			ILMGroup grp = (ILMGroup) getLoadControlGroupVector().get(i);
			if (grp.isRampingOut())
				return true;
		}

		return false;
	}

	/**
	 * Returns true when programStatus is in an ACTIVE state
	 * 
	 * @return
	 */
	public boolean isActive() {
		switch (programStatus) {
		case STATUS_ACTIVE:
		case STATUS_FULL_ACTIVE:
		case STATUS_MANUAL_ACTIVE:
		case STATUS_TIMED_ACTIVE:
			return true;
		default:
			return false;
		}
	}
    
    public boolean isScheduled() {
        if (programStatus == STATUS_SCHEDULED) {
            return true;
        } else {
            return false;
        }
    }
	
	@Override
	public PaoIdentifier getPaoIdentifier() {
		return new PaoIdentifier(yukonID, yukonType);
	}
}
