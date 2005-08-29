package com.cannontech.yukon.cbc;

import com.cannontech.common.util.CtiUtilities;

/**
 * Insert the type's description here.
 * Creation date: (8/18/00 4:23:32 PM)
 * @author: 
 */
public class SubBus extends StreamableCapObject
{   
	private String controlMethod = null;
	private Integer maxDailyOperation = null;
	private Boolean maxOperationDisableFlag = null;
	private Integer peakStartTime = null;
	private Integer peakStopTime = null;
	private Integer currentVarLoadPointID = null;
	private Double currentVarLoadPointValue = null;

	private Integer currentWattLoadPointID = null;
	private Double currentWattLoadPointValue = null;
	private Integer controlInterval = null;
	private Integer minRepsonseTime = null;
	private Integer minConfirmPercent = null;
	private Integer failurePercent = null;
	private String daysOfWeek = null;
	private String mapLocationID = null;
	private Integer decimalPlaces = null;
	private String controlUnits = null;
	private java.util.Date nextCheckTime = null;
	private Boolean newPointDataReceivedFlag = null;
	private Boolean busUpdateFlag = null;
	private java.util.Date lastCurrentVarPointUpdateTime = null;
	private Integer estimatedVarLoadPointID = null;
	private Double estimatedVarLoadPointValue = null;

	private Integer dailyOperationsAnalogPointId = null;
	private Integer powerFactorPointId = null;
	private Integer estimatedPowerFactorPointId = null;
	private Integer currentDailyOperations = null;
	private Boolean peakTimeFlag = null;
	private Boolean recentlyControlledFlag = null;
	private java.util.Date lastOperationTime = null;
	private Double varValueBeforeControl = null;
	private Integer lastFeederControlledPAOID = null;
	private Integer lastFeederControlledPosition = null;
	
	private Double powerFactorValue = null;
	private Double kVarSolution = null;
	private Double estimatedPFValue = null;
	private Integer currentVarPtQuality = null;
	private Integer controlDelayTime = null;
	private Integer controlSendRetries = null;
	private Boolean waiveControlFlag = null;
	private String additionalFlags = null;

	private Double peakLag = new Double(0.0);
	private Double offPkLag = new Double(0.0);
	private Double peakLead = new Double(0.0);
	private Double offPkLead = new Double(0.0);
	private Integer currentVoltLoadPointID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Double currentVoltLoadPointValue = null;

	//should only contain objects of type Feeder
	private java.util.Vector ccFeeders = null;	
	
	
/**
 * constructor comment.
 */
public SubBus() {
	super();
}
/**
 * StreamableCapObject constructor comment.
 */
public SubBus( Integer paoId_, String paoCategory_, String paoClass_,
				String paoName_, String paoType_, String paoDescription_, 
				Boolean paoDisableFlag_ )
{
	super( paoId_, paoCategory_, paoClass_, paoName_, 
				paoType_, paoDescription_, paoDisableFlag_ );

}

public Integer getCurrentVarPtQuality()
{
   return currentVarPtQuality;   
}

public void setCurrentVarPtQuality( Integer ptQ_ )
{
   currentVarPtQuality = ptQ_;
}

/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getBusUpdateFlag() {
	return busUpdateFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.util.Vector
 */
public java.util.Vector getCcFeeders() {
	return ccFeeders;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getControlInterval() {
	return controlInterval;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.String
 */
public java.lang.String getControlMethod() {
	return controlMethod;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentDailyOperations() {
	return currentDailyOperations;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentVarLoadPointID() {
	return currentVarLoadPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Double
 */
public java.lang.Double getCurrentVarLoadPointValue() {
	return currentVarLoadPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentWattLoadPointID() {
	return currentWattLoadPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Double
 */
public java.lang.Double getCurrentWattLoadPointValue() {
	return currentWattLoadPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDailyOperationsAnalogPointId() {
	return dailyOperationsAnalogPointId;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.String
 */
public java.lang.String getDaysOfWeek() {
	return daysOfWeek;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDecimalPlaces() {
	return decimalPlaces;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getEstimatedVarLoadPointID() {
	return estimatedVarLoadPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Double
 */
public java.lang.Double getEstimatedVarLoadPointValue() {
	return estimatedVarLoadPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getFailurePercent() {
	return failurePercent;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.util.Date
 */
public java.util.Date getLastCurrentVarPointUpdateTime() {
	return lastCurrentVarPointUpdateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLastFeederControlledPAOID() {
	return lastFeederControlledPAOID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLastFeederControlledPosition() {
	return lastFeederControlledPosition;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.util.Date
 */
public java.util.Date getLastOperationTime() {
	return lastOperationTime;
}
/**
 * Insert the method's description here.
 *
 */
public String getMapLocationID() {
	return mapLocationID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMaxDailyOperation() {
	return maxDailyOperation;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getMaxOperationDisableFlag() {
	return maxOperationDisableFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMinConfirmPercent() {
	return minConfirmPercent;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMinRepsonseTime() {
	return minRepsonseTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getNewPointDataReceivedFlag() {
	return newPointDataReceivedFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.util.Date
 */
public java.util.Date getNextCheckTime() {
	return nextCheckTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPeakStartTime() {
	return peakStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getPeakStopTime() {
	return peakStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getPeakTimeFlag() {
	return peakTimeFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getRecentlyControlledFlag() {
	return recentlyControlledFlag;
}

/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Double
 */
public java.lang.Double getVarValueBeforeControl() {
	return varValueBeforeControl;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newBusUpdateFlag java.lang.Boolean
 */
public void setBusUpdateFlag(java.lang.Boolean newBusUpdateFlag) {
	busUpdateFlag = newBusUpdateFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newCcFeeders java.util.Vector
 */
public void setCcFeeders(java.util.Vector newCcFeeders) {
	ccFeeders = newCcFeeders;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newControlInterval java.lang.Integer
 */
public void setControlInterval(java.lang.Integer newControlInterval) {
	controlInterval = newControlInterval;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newControlMethod java.lang.String
 */
public void setControlMethod(java.lang.String newControlMethod) {
	controlMethod = newControlMethod;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newCurrentDailyOperations java.lang.Integer
 */
public void setCurrentDailyOperations(java.lang.Integer newCurrentDailyOperations) {
	currentDailyOperations = newCurrentDailyOperations;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newCurrentVarLoadPointID java.lang.Integer
 */
public void setCurrentVarLoadPointID(java.lang.Integer newCurrentVarLoadPointID) {
	currentVarLoadPointID = newCurrentVarLoadPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newCurrentVarLoadPointValue java.lang.Double
 */
public void setCurrentVarLoadPointValue(java.lang.Double newCurrentVarLoadPointValue) {
	currentVarLoadPointValue = newCurrentVarLoadPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newCurrentWattLoadPointID java.lang.Integer
 */
public void setCurrentWattLoadPointID(java.lang.Integer newCurrentWattLoadPointID) {
	currentWattLoadPointID = newCurrentWattLoadPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newCurrentWattLoadPointValue java.lang.Double
 */
public void setCurrentWattLoadPointValue(java.lang.Double newCurrentWattLoadPointValue) {
	currentWattLoadPointValue = newCurrentWattLoadPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newDailyOperationsAnalogPointId java.lang.Integer
 */
public void setDailyOperationsAnalogPointId(java.lang.Integer newDailyOperationsAnalogPointId) {
	dailyOperationsAnalogPointId = newDailyOperationsAnalogPointId;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newDaysOfWeek java.lang.String
 */
public void setDaysOfWeek(java.lang.String newDaysOfWeek) {
	daysOfWeek = newDaysOfWeek;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newDecimalPlaces java.lang.Integer
 */
public void setDecimalPlaces(java.lang.Integer newDecimalPlaces) {
	decimalPlaces = newDecimalPlaces;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newEstimatedVarLoadPointID java.lang.Integer
 */
public void setEstimatedVarLoadPointID(java.lang.Integer newEstimatedVarLoadPointID) {
	estimatedVarLoadPointID = newEstimatedVarLoadPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newEstimatedVarLoadPointValue java.lang.Double
 */
public void setEstimatedVarLoadPointValue(java.lang.Double newEstimatedVarLoadPointValue) {
	estimatedVarLoadPointValue = newEstimatedVarLoadPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newFailurePercent java.lang.Integer
 */
public void setFailurePercent(java.lang.Integer newFailurePercent) {
	failurePercent = newFailurePercent;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newLastCurrentVarPointUpdateTime java.util.Date
 */
public void setLastCurrentVarPointUpdateTime(java.util.Date newLastCurrentVarPointUpdateTime) {
	lastCurrentVarPointUpdateTime = newLastCurrentVarPointUpdateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newLastFeederControlledPAOID java.lang.Integer
 */
public void setLastFeederControlledPAOID(java.lang.Integer newLastFeederControlledPAOID) {
	lastFeederControlledPAOID = newLastFeederControlledPAOID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newLastFeederControlledPosition java.lang.Integer
 */
public void setLastFeederControlledPosition(java.lang.Integer newLastFeederControlledPosition) {
	lastFeederControlledPosition = newLastFeederControlledPosition;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newLastOperationTime java.util.Date
 */
public void setLastOperationTime(java.util.Date newLastOperationTime) {
	lastOperationTime = newLastOperationTime;
}
/**
 * Insert the method's description here.
 *
 */
public void setMapLocationID(String newMapLocationID) {
	mapLocationID = newMapLocationID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newMaxDailyOperation java.lang.Integer
 */
public void setMaxDailyOperation(java.lang.Integer newMaxDailyOperation) {
	maxDailyOperation = newMaxDailyOperation;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newMaxOperationDisableFlag java.lang.Boolean
 */
public void setMaxOperationDisableFlag(java.lang.Boolean newMaxOperationDisableFlag) {
	maxOperationDisableFlag = newMaxOperationDisableFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newMinConfirmPercent java.lang.Integer
 */
public void setMinConfirmPercent(java.lang.Integer newMinConfirmPercent) {
	minConfirmPercent = newMinConfirmPercent;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newMinRepsonseTime java.lang.Integer
 */
public void setMinRepsonseTime(java.lang.Integer newMinRepsonseTime) {
	minRepsonseTime = newMinRepsonseTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newNewPointDataReceivedFlag java.lang.Boolean
 */
public void setNewPointDataReceivedFlag(java.lang.Boolean newNewPointDataReceivedFlag) {
	newPointDataReceivedFlag = newNewPointDataReceivedFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newNextCheckTime java.util.Date
 */
public void setNextCheckTime(java.util.Date newNextCheckTime) {
	nextCheckTime = newNextCheckTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newPeakStartTime java.lang.Integer
 */
public void setPeakStartTime(java.lang.Integer newPeakStartTime) {
	peakStartTime = newPeakStartTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newPeakStopTime java.lang.Integer
 */
public void setPeakStopTime(java.lang.Integer newPeakStopTime) {
	peakStopTime = newPeakStopTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newPeakTimeFlag java.lang.Boolean
 */
public void setPeakTimeFlag(java.lang.Boolean newPeakTimeFlag) {
	peakTimeFlag = newPeakTimeFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newRecentlyConrolledFlag java.lang.Boolean
 */
public void setRecentlyControlledFlag(java.lang.Boolean newRecentlyConrolledFlag) {
	recentlyControlledFlag = newRecentlyConrolledFlag;
}

/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newVarValueBeforeControl java.lang.Double
 */
public void setVarValueBeforeControl(java.lang.Double newVarValueBeforeControl) {
	varValueBeforeControl = newVarValueBeforeControl;
}
	/**
	 * Returns the controlUnits.
	 * @return String
	 */
	public String getControlUnits()
	{
		return controlUnits;
	}
	/**
	 * Returns the powerFactorValue.
	 * @return Double
	 */
	public Double getPowerFactorValue()
	{
		return powerFactorValue;
	}

	/**
	 * Sets the controlUnits.
	 * @param controlUnits The controlUnits to set
	 */
	public void setControlUnits(String controlUnits)
	{
		this.controlUnits = controlUnits;
	}

	/**
	 * Sets the powerFactorValue.
	 * @param powerFactorValue The powerFactorValue to set
	 */
	public void setPowerFactorValue(Double powerFactorValue)
	{
		this.powerFactorValue = powerFactorValue;
	}

	/**
	 * Returns the kVarSolution.
	 * @return Double
	 */
	public Double getKVarSolution()
	{
		return kVarSolution;
	}

	/**
	 * Sets the kVarSolution.
	 * @param kVarSolution The kVarSolution to set
	 */
	public void setKVarSolution(Double kVarSolution)
	{
		this.kVarSolution = kVarSolution;
	}

	/**
	 * Returns the estimatedPFValue.
	 * @return Double
	 */
	public Double getEstimatedPFValue()
	{
		return estimatedPFValue;
	}

	/**
	 * Sets the estimatedPFValue.
	 * @param estimatedPFValue The estimatedPFValue to set
	 */
	public void setEstimatedPFValue(Double estimatedPFValue)
	{
		this.estimatedPFValue = estimatedPFValue;
	}

	/**
	 * Returns the estimatedPowerFactorPointId.
	 * @return Integer
	 */
	public Integer getEstimatedPowerFactorPointId() {
		return estimatedPowerFactorPointId;
	}

	/**
	 * Returns the powerFactorPointId.
	 * @return Integer
	 */
	public Integer getPowerFactorPointId() {
		return powerFactorPointId;
	}

	/**
	 * Sets the estimatedPowerFactorPointId.
	 * @param estimatedPowerFactorPointId The estimatedPowerFactorPointId to set
	 */
	public void setEstimatedPowerFactorPointId(Integer estimatedPowerFactorPointId) {
		this.estimatedPowerFactorPointId = estimatedPowerFactorPointId;
	}

	/**
	 * Sets the powerFactorPointId.
	 * @param powerFactorPointId The powerFactorPointId to set
	 */
	public void setPowerFactorPointId(Integer powerFactorPointId) {
		this.powerFactorPointId = powerFactorPointId;
	}

	/**
	 * @return
	 */
	public Integer getControlDelayTime()
	{
		return controlDelayTime;
	}

	/**
	 * @return
	 */
	public Integer getControlSendRetries()
	{
		return controlSendRetries;
	}

	/**
	 * @return
	 */
	public Boolean getWaiveControlFlag()
	{
		return waiveControlFlag;
	}

	/**
	 * @param integer
	 */
	public void setControlDelayTime(Integer integer)
	{
		controlDelayTime = integer;
	}

	/**
	 * @param integer
	 */
	public void setControlSendRetries(Integer integer)
	{
		controlSendRetries = integer;
	}

	/**
	 * @param boolean1
	 */
	public void setWaiveControlFlag(Boolean boolean1)
	{
		waiveControlFlag = boolean1;
	}

	/**
	 * @return
	 */
	public String getAdditionalFlags()
	{
		return additionalFlags;
	}

	/**
	 * @param string
	 */
	public void setAdditionalFlags(String string)
	{
		additionalFlags = string;
	}

	/**
	 * @return
	 */
	public Integer getCurrentVoltLoadPointID() {
		return currentVoltLoadPointID;
	}

	/**
	 * @return
	 */
	public Double getCurrentVoltLoadPointValue() {
		return currentVoltLoadPointValue;
	}

	/**
	 * @return
	 */
	public Double getOffPkLag() {
		return offPkLag;
	}

	/**
	 * @return
	 */
	public Double getOffPkLead() {
		return offPkLead;
	}

	/**
	 * @return
	 */
	public Double getPeakLag() {
		return peakLag;
	}

	/**
	 * @return
	 */
	public Double getPeakLead() {
		return peakLead;
	}

	/**
	 * @param integer
	 */
	public void setCurrentVoltLoadPointID(Integer integer) {
		currentVoltLoadPointID = integer;
	}

	/**
	 * @param double1
	 */
	public void setCurrentVoltLoadPointValue(Double double1) {
		currentVoltLoadPointValue = double1;
	}

	/**
	 * @param double1
	 */
	public void setOffPkLag(Double double1) {
		offPkLag = double1;
	}

	/**
	 * @param double1
	 */
	public void setOffPkLead(Double double1) {
		offPkLead = double1;
	}

	/**
	 * @param double1
	 */
	public void setPeakLag(Double double1) {
		peakLag = double1;
	}

	/**
	 * @param double1
	 */
	public void setPeakLead(Double double1) {
		peakLead = double1;
	}

}
