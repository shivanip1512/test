package com.cannontech.loadcontrol.data;

/**
 * Insert the type's description here.
 * Creation date: (8/17/00 3:06:09 PM)
 * @author: 
 */
public class LMProgramDirectGear
{
	public static String SELECTION_LAST_CONTROLLED = "LastControlled";
	public static String SELECTION_ALWAYS_FIRST_GROUP = "AlwaysFirstGroup";
	public static String SELECTION_LEAST_CONTROL_TIME = "LeastControlTime";

	private Integer yukonID = null;
	private String gearName = null;
	private Integer gearNumber = null;
	private String controlMethod = null;
	private Integer methodRate = null;
	private Integer methodPeriod = null;
	private Integer methodRateCount = null;
	private Integer cycleRefreshRate = null;
	private String methodStopType = null;
	private String changeCondition = null;
	private Integer changeDuration = null;
	private Integer changePriority = null;
	private Integer changeTriggerNumber = null;
	private Double changeTriggerOffset = null;
	private Integer percentReduction = null;
	private String groupSelectionMethod = null;
	private String methodOptionType = null;
	private Integer methodOptionMax = null;
	private Integer rampInInterval = null;
	private Integer rampInPercent = null;
	private Integer rampOutInterval = null;
	private Integer rampOutPercent = null;
	
	// String constants that represent the various
	// states a strategy can be in
/*	public static final String STATE_ENABLED = "Enabled";
	public static final String STATE_DISABLED = "Disabled";
	public static final String STATE_CLOSED_PENDING = "Open Pending";
	public static final String STATE_OPEN_PENDING = "Closed Pending";
	public static final String ERROR = "**ERROR**";
	
	public static final int RECENTLY_CONTROLLED = 1;
	public static final int NO_CAPBANK_CONTROLLED = -1;

	
	// peakSetPointInUse possible values
	public static final int PEAK_SETPOINT = 0;
	public static final int OFF_PEAK_SETPOINT = 1;*/
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @return java.lang.String
 */
public java.lang.String getChangeCondition() {
	return changeCondition;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getChangeDuration() {
	return changeDuration;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getChangePriority() {
	return changePriority;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getChangeTriggerNumber() {
	return changeTriggerNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @return java.lang.Double
 */
public java.lang.Double getChangeTriggerOffset() {
	return changeTriggerOffset;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @return java.lang.String
 */
public java.lang.String getControlMethod() {
	return controlMethod;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCycleRefreshRate() {
	return cycleRefreshRate;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @return java.lang.String
 */
public java.lang.String getGearName() {
	return gearName;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getGearNumber() {
	return gearNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2002 4:34:26 PM)
 * @return java.lang.String
 */
public java.lang.String getGroupSelectionMethod() {
	return groupSelectionMethod;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2002 12:20:41 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMethodOptionMax() {
	return methodOptionMax;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2002 12:20:41 PM)
 * @return java.lang.String
 */
public java.lang.String getMethodOptionType() {
	return methodOptionType;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMethodPeriod() {
	return methodPeriod;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMethodRate() {
	return methodRate;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMethodRateCount() {
	return methodRateCount;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @return java.lang.String
 */
public java.lang.String getMethodStopType() {
	return methodStopType;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPercentReduction() {
	return percentReduction;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:41:03 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getYukonID() {
	return yukonID;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @param newChangeCondition java.lang.String
 */
public void setChangeCondition(java.lang.String newChangeCondition) {
	changeCondition = newChangeCondition;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @param newChangeDuration java.lang.Integer
 */
public void setChangeDuration(java.lang.Integer newChangeDuration) {
	changeDuration = newChangeDuration;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @param newChangePriority java.lang.Integer
 */
public void setChangePriority(java.lang.Integer newChangePriority) {
	changePriority = newChangePriority;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @param newChangeTriggerNumber java.lang.Integer
 */
public void setChangeTriggerNumber(java.lang.Integer newChangeTriggerNumber) {
	changeTriggerNumber = newChangeTriggerNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @param newChangeTriggerOffset java.lang.Double
 */
public void setChangeTriggerOffset(java.lang.Double newChangeTriggerOffset) {
	changeTriggerOffset = newChangeTriggerOffset;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @param newControlMethod java.lang.String
 */
public void setControlMethod(java.lang.String newControlMethod) {
	controlMethod = newControlMethod;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @param newCycleRefreshRate java.lang.Integer
 */
public void setCycleRefreshRate(java.lang.Integer newCycleRefreshRate) {
	cycleRefreshRate = newCycleRefreshRate;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @param newGearName java.lang.String
 */
public void setGearName(java.lang.String newGearName) {
	gearName = newGearName;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @param newGearNumber java.lang.Integer
 */
public void setGearNumber(java.lang.Integer newGearNumber) {
	gearNumber = newGearNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/2002 4:34:26 PM)
 * @param newGroupSelectionMethod java.lang.String
 */
public void setGroupSelectionMethod(java.lang.String newGroupSelectionMethod) {
	groupSelectionMethod = newGroupSelectionMethod;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2002 12:20:41 PM)
 * @param newMethodOptionMax java.lang.Integer
 */
public void setMethodOptionMax(java.lang.Integer newMethodOptionMax) {
	methodOptionMax = newMethodOptionMax;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2002 12:20:41 PM)
 * @param newMethodOptionType java.lang.String
 */
public void setMethodOptionType(java.lang.String newMethodOptionType) {
	methodOptionType = newMethodOptionType;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @param newMethodPeriod java.lang.Integer
 */
public void setMethodPeriod(java.lang.Integer newMethodPeriod) {
	methodPeriod = newMethodPeriod;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @param newMethodRate java.lang.Integer
 */
public void setMethodRate(java.lang.Integer newMethodRate) {
	methodRate = newMethodRate;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @param newMethodRateCount java.lang.Integer
 */
public void setMethodRateCount(java.lang.Integer newMethodRateCount) {
	methodRateCount = newMethodRateCount;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @param newMethodStopType java.lang.String
 */
public void setMethodStopType(java.lang.String newMethodStopType) {
	methodStopType = newMethodStopType;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/2001 9:58:28 AM)
 * @param newPercentReduction java.lang.Integer
 */
public void setPercentReduction(java.lang.Integer newPercentReduction) {
	percentReduction = newPercentReduction;
}
/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 10:41:03 AM)
 * @param newYukonID java.lang.Integer
 */
public void setYukonID(java.lang.Integer newYukonID) {
	yukonID = newYukonID;
}
/**
 * Insert the method's description here.
 * Creation date: (7/17/2001 9:31:19 AM)
 * @return java.lang.String
 */
public String toString() {
	return getGearName();
}
	/**
	 * @return
	 */
	public Integer getRampInInterval() {
		return rampInInterval;
	}

	/**
	 * @return
	 */
	public Integer getRampInPercent() {
		return rampInPercent;
	}

	/**
	 * @return
	 */
	public Integer getRampOutInterval() {
		return rampOutInterval;
	}

	/**
	 * @return
	 */
	public Integer getRampOutPercent() {
		return rampOutPercent;
	}

	/**
	 * @param integer
	 */
	public void setRampInInterval(Integer integer) {
		rampInInterval = integer;
	}

	/**
	 * @param integer
	 */
	public void setRampInPercent(Integer integer) {
		rampInPercent = integer;
	}

	/**
	 * @param integer
	 */
	public void setRampOutInterval(Integer integer) {
		rampOutInterval = integer;
	}

	/**
	 * @param integer
	 */
	public void setRampOutPercent(Integer integer) {
		rampOutPercent = integer;
	}

}
