package com.cannontech.message.capcontrol.streamable;

import java.util.Vector;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.UnitOfMeasure;

/**
 * A feeder object
 */
public class Feeder extends StreamableCapObject implements PointQualityCheckable
{
	private Integer maxDailyOperation = null;
	private Boolean maxOperationDisableFlag = null;
	private Boolean ovUvDisabledFlag = null;
	private Boolean likeDayControlFlag = null;

	private Integer currentVarLoadPointID = null;
	private Double currentVarLoadPointValue = null;
	private Integer currentWattLoadPointID = null;
	private Double currentWattLoadPointValue = null;
	private String mapLocationID = null;
   
	private Float displayOrder = null;
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

	private Double powerFactorValue = null;
	private Double estimatedPFValue = null;   
	private Integer currentVarPtQuality = null;
	private Boolean waiveControlFlag = null;

	private ControlAlgorithm controlUnits = ControlAlgorithm.KVAR;
	private int decimalPlaces = 0;
	private Boolean peakTimeFlag = Boolean.TRUE;

	private Double peakLag = new Double(0.0);
	private Double offPkLag = new Double(0.0);
	private Double peakLead = new Double(0.0);
	private Double offPkLead = new Double(0.0);
	private Integer currentVoltLoadPointID = new Integer(CtiUtilities.NONE_ZERO_ID);
	private Double currentVoltLoadPointValue = null;
    private Integer currentwattpointquality = null;
    private Integer currentvoltpointquality = null;
    private Double targetvarvalue = null;
    private String solution = null;
    private Double peakPFSetPoint = new Double(100.0);
    private Double offpeakPFSetPoint = new Double(100.0);
    private ControlMethod controlmethod = null;
    private Double phaseA = new Double(0.0);
    private Double phaseB = new Double(0.0);
    private Double phaseC = new Double(0.0);
	private Vector<CapBankDevice> ccCapBanks = null;
	private Boolean usePhaseData = null;
	private Integer originalParentId = null;
	
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
public Vector<CapBankDevice> getCcCapBanks() {
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
public java.lang.Float getDisplayOrder() {
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
public void setCcCapBanks(Vector<CapBankDevice> newCcCapBanks) {
	ccCapBanks = newCcCapBanks;
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
public void setDisplayOrder(java.lang.Float newDisplayOrder) {
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
	public ControlAlgorithm getControlUnits()
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
	public void setControlUnits(ControlAlgorithm units)
	{
		controlUnits = units;
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

	/**
	 * @return
	 */
	public Integer getMaxDailyOperation() {
		return maxDailyOperation;
	}

	/**
	 * @return
	 */
	public Boolean getMaxOperationDisableFlag() {
		return maxOperationDisableFlag;
	}

	/**
	 * @param integer
	 */
	public void setMaxDailyOperation(Integer integer) {
		maxDailyOperation = integer;
	}

	/**
	 * @param boolean1
	 */
	public void setMaxOperationDisableFlag(Boolean boolean1) {
		maxOperationDisableFlag = boolean1;
	}
	
    public Integer getCurrentPtQuality(int uomid) {
        UnitOfMeasure uom = UnitOfMeasure.getForId(uomid);
        if (uom.isCapControlVar())
            return getCurrentVarPtQuality();
        if (uom.isCapControlWatt())
            return getCurrentwattpointquality();
        if (uom.isCapControlVolt())
            return getCurrentvoltpointquality();
        return null;
    }
	public Integer getCurrentvoltpointquality() {
		return currentvoltpointquality;
	}
	public void setCurrentvoltpointquality(Integer currentvoltpointquality) {
		this.currentvoltpointquality = currentvoltpointquality;
	}
	public Integer getCurrentwattpointquality() {
		return currentwattpointquality;
	}
	public void setCurrentwattpointquality(Integer currentwattpointquality) {
		this.currentwattpointquality = currentwattpointquality;
	}
	public String getSolution() {
		return solution;
	}
	public void setSolution(String solution) {
		this.solution = solution;
	}
	public Double getTargetvarvalue() {
		return targetvarvalue;
	}
	public void setTargetvarvalue(Double targetvarvalue) {
		this.targetvarvalue = targetvarvalue;
	}
	public Boolean getOvUvDisabledFlag() {
		return ovUvDisabledFlag;
	}
	public void setOvUvDisabledFlag(Boolean ovUvDisabledFlag) {
		this.ovUvDisabledFlag = ovUvDisabledFlag;
	}

    public Double getOffpeakPFSetPoint() {
        return offpeakPFSetPoint;
    }

    public void setOffpeakPFSetPoint(Double offpeakPFSetting) {
        this.offpeakPFSetPoint = offpeakPFSetting;
    }

    public Double getPeakPFSetPoint() {
        return peakPFSetPoint;
    }

    public void setPeakPFSetPoint(Double peakPFSetting) {
        this.peakPFSetPoint = peakPFSetting;
    }
	public ControlMethod getControlmethod() {
		return controlmethod;
	}
	public void setControlmethod(ControlMethod controlmethod) {
		this.controlmethod = controlmethod;
	}
    
    public Double getPhaseA() {
        return phaseA;
    }

    public void setPhaseA(Double phaseA) {
        this.phaseA = phaseA;
    }

    public Double getPhaseB() {
        return phaseB;
    }

    public void setPhaseB(Double phaseB) {
        this.phaseB = phaseB;
    }

    public Double getPhaseC() {
        return phaseC;
    }

    public void setPhaseC(Double phaseC) {
        this.phaseC = phaseC;
    }
	public Boolean getLikeDayControlFlag() {
		return likeDayControlFlag;
	}
	public void setLikeDayControlFlag(Boolean likeDayControlFlag) {
		this.likeDayControlFlag = likeDayControlFlag;
	}
	public Boolean getUsePhaseData() {
		return usePhaseData;
	}
	public void setUsePhaseData(Boolean usePhaseData) {
		this.usePhaseData = usePhaseData;
	}
	public boolean isPowerFactorControlled() {
		return controlUnits == ControlAlgorithm.PFACTOR_KW_KVAR;
	}
    public Integer getOriginalParentId() {
        return originalParentId;
    }
    public void setOriginalParentId(Integer originalParentId) {
        this.originalParentId = originalParentId;
    }
}
