package com.cannontech.yukon.cbc;

import com.cannontech.clientutils.CommonUtils;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.common.login.ClientSession;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;
import com.cannontech.roles.application.TDCRole;

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
	private Double peakSetPoint = null;
	private Double offPeakSetPoint = null;
	private Integer peakStartTime = null;
	private Integer peakStopTime = null;
	private Integer currentVarLoadPointID = null;
	private Double currentVarLoadPointValue = null;

	private Integer currentWattLoadPointID = null;
	private Double currentWattLoadPointValue = null;
	private Double upperBandWidth = null;
	private Integer controlInterval = null;
	private Integer minRepsonseTime = null;
	private Integer minConfirmPercent = null;
	private Integer failurePercent = null;

	private String daysOfWeek = null;
	private Integer mapLocationID = null;
	private Integer decimalPlaces = null;
   
   private Double lowerBandWidth = null;
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


	//should only contain objects of type Feeder
	private java.util.Vector ccFeeders = null;

	// Possible values for ControlMethod
	//com.cannontech.database.db.capcontrol.CapControlSubstationBus.CNTRL_INDIVIDUAL_FEEDER;
	//com.cannontech.database.db.capcontrol.CapControlSubstationBus.CNTRL_SUBSTATION_BUS;
	//com.cannontech.database.db.capcontrol.CapControlSubstationBus.CNTRL_BUSOPTIMIZED_FEEDER;
/**
 * CapBankDevice constructor comment.
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

public boolean isPowerFactorControlled()
{
   return( CalcComponentTypes.PFACTOR_KW_KVAR_FUNCTION.equalsIgnoreCase(getControlUnits())
            || CalcComponentTypes.PFACTOR_KW_KQ_FUNCTION.equalsIgnoreCase(getControlUnits()) );
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
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getMapLocationID() {
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
 * @return java.lang.Double
 */
public java.lang.Double getOffPeakSetPoint() {
	return offPeakSetPoint;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @return java.lang.Double
 */
public java.lang.Double getPeakSetPoint() {
	return peakSetPoint;
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
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newMapLocationID java.lang.Integer
 */
public void setMapLocationID(java.lang.Integer newMapLocationID) {
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
 * @param newOffPeakSetPoint java.lang.Double
 */
public void setOffPeakSetPoint(java.lang.Double newOffPeakSetPoint) {
	offPeakSetPoint = newOffPeakSetPoint;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 12:01:28 PM)
 * @param newPeakSetPoint java.lang.Double
 */
public void setPeakSetPoint(java.lang.Double newPeakSetPoint) {
	peakSetPoint = newPeakSetPoint;
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
	 * Returns the lowerBandWidth.
	 * @return Double
	 */
	public Double getLowerBandWidth()
	{
		return lowerBandWidth;
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
	 * Returns the upperBandWidth.
	 * @return Double
	 */
	public Double getUpperBandWidth()
	{
		return upperBandWidth;
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
	 * Sets the lowerBandWidth.
	 * @param lowerBandWidth The lowerBandWidth to set
	 */
	public void setLowerBandWidth(Double lowerBandWidth)
	{
		this.lowerBandWidth = lowerBandWidth;
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
	 * Sets the upperBandWidth.
	 * @param upperBandWidth The upperBandWidth to set
	 */
	public void setUpperBandWidth(Double upperBandWidth)
	{
		this.upperBandWidth = upperBandWidth;
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

	public String getRenderName()
	{
		return getCcName();
	}
	
	public Object getRenderWatts()
	{
		if( getCurrentWattLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
			return DASH_LINE;
		else
		{
			if( getDecimalPlaces().intValue() == 0 )
					return new Integer( CommonUtils.formatDecimalPlaces( 
				   getCurrentWattLoadPointValue().doubleValue(), getDecimalPlaces().intValue() ) );             
			else
				 return new Double( CommonUtils.formatDecimalPlaces( 
					  getCurrentWattLoadPointValue().doubleValue(), getDecimalPlaces().intValue() ) );
		 }
	}
	
	public Object getRenderTimeStamp()
	{
		if( getLastCurrentVarPointUpdateTime().getTime() <= 
			com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
			return DASH_LINE;
		else
			return new ModifiedDate( getLastCurrentVarPointUpdateTime().getTime(), ModifiedDate.FRMT_NOSECS );
	}

	public String getRenderPF()
	{
		return getPowerFactorText( getPowerFactorValue().doubleValue(), true )
			+ " / " +
			getPowerFactorText( getEstimatedPFValue().doubleValue(), true );
	}

	public String getRenderState()
	{
		String state = null;
                
		if( getCcDisableFlag().booleanValue() )
		{
			state = "DISABLED";
		}
		else if( getRecentlyControlledFlag().booleanValue() )
		{
			state = getSubBusPendingState();
                    
			if( state == null )
			{
				state = "PENDING"; //we only know its pending for sure
			}
                    
		}
		else
			state = "ENABLED";


		//show waived with a W at the end of the state
		if( getWaiveControlFlag().booleanValue() )
			state += "-W";

		return state;
	}
	
	public String getRenderDailyOps()
	{
		return new String(getCurrentDailyOperations() + " / " + 
				(getMaxDailyOperation().intValue() <= 0 
					? STR_NA 
					: getMaxDailyOperation().toString()) );
	}
	
	public String getRenderTarget()
	{
		// decide which set Point we are to use
		if( isPowerFactorControlled() )
		{
			return getPowerFactorText(getPeakSetPoint().doubleValue(), false);
		}
		else if( getLowerBandWidth().doubleValue() == 0
					 && getUpperBandWidth().doubleValue() == 0 )
		{
			return STR_NA;
		}
		else if( getPeakTimeFlag().booleanValue() )
		{
			return
				CommonUtils.formatDecimalPlaces(getPeakSetPoint().doubleValue() - getLowerBandWidth().doubleValue(), 0) +
				" to " + 
				CommonUtils.formatDecimalPlaces(getUpperBandWidth().doubleValue() + getPeakSetPoint().doubleValue(), 0) + 
				" Pk";
		}
		else
		{
			return
				CommonUtils.formatDecimalPlaces(getOffPeakSetPoint().doubleValue() - getLowerBandWidth().doubleValue(), 0) +
				" to " + 
				CommonUtils.formatDecimalPlaces(getUpperBandWidth().doubleValue() + getOffPeakSetPoint().doubleValue(), 0) + 
				" OffPk";
		}
	}

	public String getRenderVarLoad()
	{
		String retVal = DASH_LINE; //default just in case

		if( getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
		   retVal = DASH_LINE;
		else 
		{                        
			if( getDecimalPlaces().intValue() == 0 )
					retVal =  CommonUtils.formatDecimalPlaces( 
				  getCurrentVarLoadPointValue().doubleValue(), getDecimalPlaces().intValue() );             
			else
					retVal = CommonUtils.formatDecimalPlaces( 
				  getCurrentVarLoadPointValue().doubleValue(), getDecimalPlaces().intValue() );
		}
                
		retVal += " / ";

		if( getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
			retVal += DASH_LINE;
		else 
		{               
			if( getDecimalPlaces().intValue() == 0 )
					retVal += CommonUtils.formatDecimalPlaces( 
				  getEstimatedVarLoadPointValue().doubleValue(), getDecimalPlaces().intValue() );           
			else
					retVal += CommonUtils.formatDecimalPlaces( 
						getEstimatedVarLoadPointValue().doubleValue(), getDecimalPlaces().intValue() );
		}
                
		return retVal;
	}

	private String getPowerFactorText( double value, boolean compute )
	{   
	   int decPlaces = 1;
	   try
	   {
		  decPlaces = 
			 Integer.parseInt(
					ClientSession.getInstance().getRolePropertyValue(
				   TDCRole.PFACTOR_DECIMAL_PLACES, 
				   "1") );
	   }
	   catch( Exception e)
	   {}
        
	   if( value <= CapControlConst.PF_INVALID_VALUE )
		  return STR_NA;
	   else
		  return CommonUtils.formatDecimalPlaces(
				value * (compute ? 100 : 1), decPlaces ) + "%"; //get percent   
	}

	/**
	 * Discovers if the given SubBus is in any Pending state
	 *
	 */
	private String getSubBusPendingState() 
	{
/*		
		for( int i = 0; i < getCcFeeders().size(); i++ )
		{
			com.cannontech.yukon.cbc.Feeder feeder =
				(com.cannontech.yukon.cbc.Feeder)getCcFeeders().get(i);

			int size = feeder.getCcCapBanks().size();
			for( int j = 0; j < size; j++ )
			{
				CapBankDevice capBank = ((CapBankDevice)feeder.getCcCapBanks().elementAt(j));
                
				if( capBank.getControlStatus().intValue() == CapControlConst.BANK_CLOSE_PENDING )
					return CapBankTableModel.getStateNames()[CapControlConst.BANK_CLOSE_PENDING];
                    
				if( capBank.getControlStatus().intValue() == CapControlConst.BANK_OPEN_PENDING )
					return CapBankTableModel.getStateNames()[CapControlConst.BANK_OPEN_PENDING];
			}

		}
*/
		// we are not pending
		return null;
	}
/*
	case SubBusTableModel.SUB_NAME_COLUMN:
	{
		return subBus.getCcName();
	}

	case SubBusTableModel.AREA_NAME_COLUMN:
	{
		return subBus.getCcArea();
	}

	case SubBusTableModel.CURRENT_STATE_COLUMN:
	{
		String state = null;
                
		if( subBus.getCcDisableFlag().booleanValue() )
		{
			state = "DISABLED";
		}
		else if( subBus.getRecentlyControlledFlag().booleanValue() )
		{
			state = getSubBusPendingState( subBus );
                    
			if( state == null )
			{
				state = "PENDING"; //we only know its pending for sure
			}
                    
		}
		else
			state = "ENABLED";


		//show waived with a W at the end of the state
		if( subBus.getWaiveControlFlag().booleanValue() )
			state += "-W";

		return state;

	}

	case SubBusTableModel.TARGET_COLUMN:
	{
		// decide which set Point we are to use
		if( subBus.isPowerFactorControlled() )
		{
			return getPowerFactorText(subBus.getPeakSetPoint().doubleValue(), false);
		}
		else if( subBus.getLowerBandWidth().doubleValue() == 0
					 && subBus.getUpperBandWidth().doubleValue() == 0 )
		{
			return STR_NA;
		}
		else if( subBus.getPeakTimeFlag().booleanValue() )
		{
			return
				CommonUtils.formatDecimalPlaces(subBus.getPeakSetPoint().doubleValue() - subBus.getLowerBandWidth().doubleValue(), 0) +
				" to " + 
				CommonUtils.formatDecimalPlaces(subBus.getUpperBandWidth().doubleValue() + subBus.getPeakSetPoint().doubleValue(), 0) + 
				" Pk";
		}
		else
		{
			return
				CommonUtils.formatDecimalPlaces(subBus.getOffPeakSetPoint().doubleValue() - subBus.getLowerBandWidth().doubleValue(), 0) +
				" to " + 
				CommonUtils.formatDecimalPlaces(subBus.getUpperBandWidth().doubleValue() + subBus.getOffPeakSetPoint().doubleValue(), 0) + 
				" OffPk";
		}
	}
                
	case SubBusTableModel.DAILY_OPERATIONS_COLUMN:
	{
		return new String(subBus.getCurrentDailyOperations() + " / " + 
				(subBus.getMaxDailyOperation().intValue() <= 0 
					? STR_NA 
					: subBus.getMaxDailyOperation().toString()) );
	}

	case SubBusTableModel.VAR_LOAD_COLUMN:
	{
		String retVal = DASH_LINE; //default just in case

		if( subBus.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
		   retVal = DASH_LINE;
		else 
		{                        
			if( subBus.getDecimalPlaces().intValue() == 0 )
					retVal =  CommonUtils.formatDecimalPlaces( 
				  subBus.getCurrentVarLoadPointValue().doubleValue(), subBus.getDecimalPlaces().intValue() );             
			else
					retVal = CommonUtils.formatDecimalPlaces( 
				  subBus.getCurrentVarLoadPointValue().doubleValue(), subBus.getDecimalPlaces().intValue() );
		}
                
		retVal += " / ";

		if( subBus.getCurrentVarLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
			retVal += DASH_LINE;
		else 
		{               
			if( subBus.getDecimalPlaces().intValue() == 0 )
					retVal += CommonUtils.formatDecimalPlaces( 
				  subBus.getEstimatedVarLoadPointValue().doubleValue(), subBus.getDecimalPlaces().intValue() );           
			else
					retVal += CommonUtils.formatDecimalPlaces( 
						subBus.getEstimatedVarLoadPointValue().doubleValue(), subBus.getDecimalPlaces().intValue() );
		}
                
		return retVal;
	}
          
	case SubBusTableModel.POWER_FACTOR_COLUMN:
	{
		return getPowerFactorText( subBus.getPowerFactorValue().doubleValue(), true )
			+ " / " +
			getPowerFactorText( subBus.getEstimatedPFValue().doubleValue(), true );
	}

	case SubBusTableModel.WATTS_COLUMN:
	{
		if( subBus.getCurrentWattLoadPointID().intValue() <= PointTypes.SYS_PID_SYSTEM )
			return DASH_LINE;
		 else {
			if( subBus.getDecimalPlaces().intValue() == 0 )
					return new Integer( CommonUtils.formatDecimalPlaces( 
				   subBus.getCurrentWattLoadPointValue().doubleValue(), subBus.getDecimalPlaces().intValue() ) );             
			else
				 return new Double( CommonUtils.formatDecimalPlaces( 
					  subBus.getCurrentWattLoadPointValue().doubleValue(), subBus.getDecimalPlaces().intValue() ) );
		 }
	}
            
	case SubBusTableModel.TIME_STAMP_COLUMN:
	{
		if( subBus.getLastCurrentVarPointUpdateTime().getTime() <= 
			com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
			return DASH_LINE;
		else
			return new ModifiedDate( subBus.getLastCurrentVarPointUpdateTime().getTime(), ModifiedDate.FRMT_NOSECS );
	}
*/
}
