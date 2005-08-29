package com.cannontech.yukon.cbc;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;

/**
 * Insert the type's description here.
 * Creation date: (8/18/00 4:23:32 PM)
 * @author: 
 */
public class Feeder extends StreamableCapObject
{
	private Integer currentVarLoadPointID = null;
	private Double currentVarLoadPointValue = null;
	private Integer currentWattLoadPointID = null;
	private Double currentWattLoadPointValue = null;
	private String mapLocationID = null;
   
	private Integer displayOrder = null;
	private Boolean newPointDataReceivedFlag = null;
	private java.util.Date lastCurrentVarPointUpdateTime = null;
	private Integer estimatedVarLoadPointID = null;
	private Double estimatedVarLoadPointValue = null;

	private Integer dailyOperationsAnalogPointID = null;
	private Integer powerFactorPointID = null;
	private Integer estimatedPowerFactorPointID = null;
	private Integer currentDailyOperations = null;
	private Boolean recentlyControlledFlag = null;
	private java.util.Date lastOperationTime = null;
	private Double varValueBeforeControl = null;
	private Integer lastCapBankControlledDeviceID= null;

	private Double powerFactorValue = null;
	private Double kVarSolution = null;
	private Double estimatedPFValue = null;   
	private Integer currentVarPtQuality = null;
	private Boolean waiveControlFlag = null;

	private String controlUnits = CalcComponentTypes.LABEL_KVAR;
	private int decimalPlaces = 0;
	private Boolean peakTimeFlag = Boolean.TRUE;

	private Double peakLag = new Double(0.0);
	private Double offPkLag = new Double(0.0);
	private Double peakLead = new Double(0.0);
	private Double offPkLead = new Double(0.0);
	private Integer currentVoltLoadPointID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Double currentVoltLoadPointValue = null;

	//should only contain objects of type CapBankDevice
	private java.util.Vector ccCapBanks = null;
	
	
/**
 * CapBankDevice constructor comment.
 */
public Feeder() {
	super();
}
/**
 * StreamableCapObject constructor comment.
 */
public Feeder( Integer paoId_, String paoCategory_, String paoClass_,
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
 * Creation date: (11/19/2001 1:11:48 PM)
 * @return java.util.Vector
 */
public java.util.Vector getCcCapBanks() {
	return ccCapBanks;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 11:45:37 AM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentDailyOperations() {
	return currentDailyOperations;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentVarLoadPointID() {
	return currentVarLoadPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @return java.lang.Double
 */
public java.lang.Double getCurrentVarLoadPointValue() {
	return currentVarLoadPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getCurrentWattLoadPointID() {
	return currentWattLoadPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @return java.lang.Double
 */
public java.lang.Double getCurrentWattLoadPointValue() {
	return currentWattLoadPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDailyOperationsAnalogPointID() {
	return dailyOperationsAnalogPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getDisplayOrder() {
	return displayOrder;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getEstimatedVarLoadPointID() {
	return estimatedVarLoadPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @return java.lang.Double
 */
public java.lang.Double getEstimatedVarLoadPointValue() {
	return estimatedVarLoadPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getLastCapBankControlledDeviceID() {
	return lastCapBankControlledDeviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @return java.util.Date
 */
public java.util.Date getLastCurrentVarPointUpdateTime() {
	return lastCurrentVarPointUpdateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
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
 * Creation date: (11/19/2001 1:11:48 PM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getNewPointDataReceivedFlag() {
	return newPointDataReceivedFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @return java.lang.Boolean
 */
public java.lang.Boolean getRecentlyControlledFlag() {
	return recentlyControlledFlag;
}

/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @return java.lang.Double
 */
public java.lang.Double getVarValueBeforeControl() {
	return varValueBeforeControl;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @param newCcCapBanks java.util.Vector
 */
public void setCcCapBanks(java.util.Vector newCcCapBanks) 
{
	ccCapBanks = newCcCapBanks;

	//set all the capbanks feeders owner name to this feeders name!
/*	if( getCcCapBanks() != null )
		for( int i = 0; i < getCcCapBanks().size(); i++ )
		{
			//just let this statement throw a ClassCastException
			((CapBankDevice)getCcCapBanks().get(i)).setFeederOwner( getCcName() );
		}
*/
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 11:45:37 AM)
 * @param newCurrentDailyOperations java.lang.Integer
 */
public void setCurrentDailyOperations(java.lang.Integer newCurrentDailyOperations) {
	currentDailyOperations = newCurrentDailyOperations;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @param newCurrentVarLoadPointID java.lang.Integer
 */
public void setCurrentVarLoadPointID(java.lang.Integer newCurrentVarLoadPointID) {
	currentVarLoadPointID = newCurrentVarLoadPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @param newCurrentVarLoadPointValue java.lang.Double
 */
public void setCurrentVarLoadPointValue(java.lang.Double newCurrentVarLoadPointValue) {
	currentVarLoadPointValue = newCurrentVarLoadPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @param newCurrentWattLoadPointID java.lang.Integer
 */
public void setCurrentWattLoadPointID(java.lang.Integer newCurrentWattLoadPointID) {
	currentWattLoadPointID = newCurrentWattLoadPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @param newCurrentWattLoadPointValue java.lang.Double
 */
public void setCurrentWattLoadPointValue(java.lang.Double newCurrentWattLoadPointValue) {
	currentWattLoadPointValue = newCurrentWattLoadPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @param newDailyOperationsAnalogPointID java.lang.Integer
 */
public void setDailyOperationsAnalogPointID(java.lang.Integer newDailyOperationsAnalogPointID) {
	dailyOperationsAnalogPointID = newDailyOperationsAnalogPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @param newDisplayOrder java.lang.Integer
 */
public void setDisplayOrder(java.lang.Integer newDisplayOrder) {
	displayOrder = newDisplayOrder;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @param newEstimatedVarLoadPointID java.lang.Integer
 */
public void setEstimatedVarLoadPointID(java.lang.Integer newEstimatedVarLoadPointID) {
	estimatedVarLoadPointID = newEstimatedVarLoadPointID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @param newEstimatedVarLoadPointValue java.lang.Double
 */
public void setEstimatedVarLoadPointValue(java.lang.Double newEstimatedVarLoadPointValue) {
	estimatedVarLoadPointValue = newEstimatedVarLoadPointValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @param newLastCapBankControlledDeviceID java.lang.Integer
 */
public void setLastCapBankControlledDeviceID(java.lang.Integer newLastCapBankControlledDeviceID) {
	lastCapBankControlledDeviceID = newLastCapBankControlledDeviceID;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @param newLastCurrentVarPointUpdateTime java.util.Date
 */
public void setLastCurrentVarPointUpdateTime(java.util.Date newLastCurrentVarPointUpdateTime) {
	lastCurrentVarPointUpdateTime = newLastCurrentVarPointUpdateTime;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
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
 * Creation date: (11/19/2001 1:11:48 PM)
 * @param newNewPointDataReceivedFlag java.lang.Boolean
 */
public void setNewPointDataReceivedFlag(java.lang.Boolean newNewPointDataReceivedFlag) {
	newPointDataReceivedFlag = newNewPointDataReceivedFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @param newRecentlyControlledFlag java.lang.Boolean
 */
public void setRecentlyControlledFlag(java.lang.Boolean newRecentlyControlledFlag) {
	recentlyControlledFlag = newRecentlyControlledFlag;
}


/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 1:11:48 PM)
 * @param newVarValueBeforeControl java.lang.Double
 */
public void setVarValueBeforeControl(java.lang.Double newVarValueBeforeControl) {
	varValueBeforeControl = newVarValueBeforeControl;
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
	 * Returns the estimatedPowerFactorPointID.
	 * @return Integer
	 */
	public Integer getEstimatedPowerFactorPointID() {
		return estimatedPowerFactorPointID;
	}

	/**
	 * Returns the powerFactorPointID.
	 * @return Integer
	 */
	public Integer getPowerFactorPointID() {
		return powerFactorPointID;
	}

	/**
	 * Sets the estimatedPowerFactorPointID.
	 * @param estimatedPowerFactorPointID The estimatedPowerFactorPointID to set
	 */
	public void setEstimatedPowerFactorPointID(Integer estimatedPowerFactorPointID) {
		this.estimatedPowerFactorPointID = estimatedPowerFactorPointID;
	}

	/**
	 * Sets the powerFactorPointID.
	 * @param powerFactorPointID The powerFactorPointID to set
	 */
	public void setPowerFactorPointID(Integer powerFactorPointID) {
		this.powerFactorPointID = powerFactorPointID;
	}

	/**
	 * @return
	 */
	public Boolean getWaiveControlFlag()
	{
		return waiveControlFlag;
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
	public String getControlUnits()
	{
		return controlUnits;
	}

	/**
	 * @return
	 */
	public int getDecimalPlaces()
	{
		return decimalPlaces;
	}

	/**
	 * @return
	 */
	public Boolean getPeakTimeFlag()
	{
		return peakTimeFlag;
	}

	/**
	 * @param string
	 */
	public void setControlUnits(String string)
	{
		controlUnits = string;
	}

	/**
	 * @param i
	 */
	public void setDecimalPlaces(int i)
	{
		decimalPlaces = i;
	}

	/**
	 * @param boolean1
	 */
	public void setPeakTimeFlag(Boolean boolean1)
	{
		peakTimeFlag = boolean1;
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
