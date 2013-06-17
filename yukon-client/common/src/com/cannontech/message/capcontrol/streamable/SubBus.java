package com.cannontech.message.capcontrol.streamable;

import java.util.Vector;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.database.data.point.UnitOfMeasure;

/**
 * Insert the type's description here. Creation date: (8/18/00 4:23:32 PM)
 * @author:
 */
public class SubBus extends StreamableCapObject implements PointQualityCheckable {
    private Integer maxDailyOperation = null;
    private Boolean maxOperationDisableFlag = null;
    private Double currentVarLoadPointValue = null;
    private Boolean ovUvDisabledFlag = null;
    private Boolean likeDayControlFlag = null;

    private Double currentWattLoadPointValue = null;
    private String mapLocationID = null;
    private Integer decimalPlaces = null;
    private Boolean newPointDataReceivedFlag = null;
    private Boolean busUpdateFlag = null;
    private java.util.Date lastCurrentVarPointUpdateTime = null;
    private Double estimatedVarLoadPointValue = null;

    private Integer currentVarLoadPointID = null;
    private Integer currentWattLoadPointID = null;
    private ControlAlgorithm controlUnits = null;
    private Integer estimatedVarLoadPointID = null;
    private Integer dailyOperationsAnalogPointId = null;
    private Integer powerFactorPointId = null;
    private Integer estimatedPowerFactorPointId = null;
    private Integer currentVoltLoadPointID = null;

    private Integer currentDailyOperations = null;
    private Boolean peakTimeFlag = null;
    private Boolean recentlyControlledFlag = null;
    private java.util.Date lastOperationTime = null;
    private Double varValueBeforeControl = null;

    private Double powerFactorValue = null;
    private Double estimatedPFValue = null;
    private Integer currentVarPtQuality = null;
    private Boolean waiveControlFlag = null;

    private Double peakLag = new Double(0.0);
    private Double offPkLag = new Double(0.0);
    private Double peakLead = new Double(0.0);
    private Double offPkLead = new Double(0.0);
    private Double currentVoltLoadPointValue = null;
    private Boolean verificationFlag = null;
    private Integer currentwattpointquality = null;
    private Integer currentvoltpointquality = null;
    private Double targetvarvalue = null;
    private String solution = null;

    private Vector<Feeder>ccFeeders = new Vector<Feeder>();
    private Boolean switchOverStatus = null;
    private Double peakPFSetPoint = new Double(100.0);
    private Double offpeakPFSetPoint = new Double(100.0);
    private Double phaseA = new Double(0.0);
    private Double phaseB = new Double(0.0);
    private Double phaseC = new Double(0.0);
    private ControlMethod controlMethod = null;
    private Integer displayOrder;
    private Boolean voltReductionFlag = null;
    private Boolean usePhaseData = null;
    
    private Boolean primaryBusFlag = null;
    private Boolean dualBusEnabled = null;
    private Integer alternateBusId = null;
    private Integer strategyId = -1;
    /**
     * constructor comment.
     */
    public SubBus() {
        super();
    }

    /**
     * StreamableCapObject constructor comment.
     */
    public SubBus(Integer paoId_, String paoCategory_, String paoClass_,
            String paoName_, String paoType_, String paoDescription_,
            Boolean paoDisableFlag_) {
        super(paoId_,
              paoCategory_,
              paoClass_,
              paoName_,
              paoType_,
              paoDescription_,
              paoDisableFlag_);

    }

    public Integer getCurrentVarPtQuality() {
        return currentVarPtQuality;
    }

    public void setCurrentVarPtQuality(Integer ptQ_) {
        currentVarPtQuality = ptQ_;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @return java.lang.Boolean
     */
    public java.lang.Boolean getBusUpdateFlag() {
        return busUpdateFlag;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @return java.util.Vector
     */
    public Vector<Feeder> getCcFeeders() {
        return ccFeeders;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @return java.lang.Integer
     */
    public java.lang.Integer getCurrentDailyOperations() {
        return currentDailyOperations;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @return java.lang.Double
     */
    public java.lang.Double getCurrentVarLoadPointValue() {
        return currentVarLoadPointValue;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @return java.lang.Double
     */
    public java.lang.Double getCurrentWattLoadPointValue() {
        return currentWattLoadPointValue;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @return java.lang.Integer
     */
    public java.lang.Integer getDecimalPlaces() {
        return decimalPlaces;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @return java.lang.Double
     */
    public java.lang.Double getEstimatedVarLoadPointValue() {
        return estimatedVarLoadPointValue;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @return java.util.Date
     */
    public java.util.Date getLastCurrentVarPointUpdateTime() {
        return lastCurrentVarPointUpdateTime;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @return java.util.Date
     */
    public java.util.Date getLastOperationTime() {
        return lastOperationTime;
    }

    /**
     * Insert the method's description here.
     */
    public String getMapLocationID() {
        return mapLocationID;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @return java.lang.Integer
     */
    public java.lang.Integer getMaxDailyOperation() {
        return maxDailyOperation;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @return java.lang.Boolean
     */
    public java.lang.Boolean getMaxOperationDisableFlag() {
        return maxOperationDisableFlag;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @return java.lang.Boolean
     */
    public java.lang.Boolean getNewPointDataReceivedFlag() {
        return newPointDataReceivedFlag;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @return java.lang.Boolean
     */
    public java.lang.Boolean getPeakTimeFlag() {
        return peakTimeFlag;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @return java.lang.Boolean
     */
    public java.lang.Boolean getRecentlyControlledFlag() {
        return recentlyControlledFlag;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @return java.lang.Double
     */
    public java.lang.Double getVarValueBeforeControl() {
        return varValueBeforeControl;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @param newBusUpdateFlag java.lang.Boolean
     */
    public void setBusUpdateFlag(java.lang.Boolean newBusUpdateFlag) {
        busUpdateFlag = newBusUpdateFlag;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @param newCcFeeders java.util.Vector
     */
    public void setCcFeeders(Vector<Feeder> newCcFeeders) {
        ccFeeders = newCcFeeders;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @param newCurrentDailyOperations java.lang.Integer
     */
    public void setCurrentDailyOperations(
            java.lang.Integer newCurrentDailyOperations) {
        currentDailyOperations = newCurrentDailyOperations;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @param newCurrentVarLoadPointValue java.lang.Double
     */
    public void setCurrentVarLoadPointValue(
            java.lang.Double newCurrentVarLoadPointValue) {
        currentVarLoadPointValue = newCurrentVarLoadPointValue;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @param newCurrentWattLoadPointValue java.lang.Double
     */
    public void setCurrentWattLoadPointValue(
            java.lang.Double newCurrentWattLoadPointValue) {
        currentWattLoadPointValue = newCurrentWattLoadPointValue;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @param newDecimalPlaces java.lang.Integer
     */
    public void setDecimalPlaces(java.lang.Integer newDecimalPlaces) {
        decimalPlaces = newDecimalPlaces;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @param newEstimatedVarLoadPointValue java.lang.Double
     */
    public void setEstimatedVarLoadPointValue(
            java.lang.Double newEstimatedVarLoadPointValue) {
        estimatedVarLoadPointValue = newEstimatedVarLoadPointValue;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @param newLastCurrentVarPointUpdateTime java.util.Date
     */
    public void setLastCurrentVarPointUpdateTime(
            java.util.Date newLastCurrentVarPointUpdateTime) {
        lastCurrentVarPointUpdateTime = newLastCurrentVarPointUpdateTime;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @param newLastOperationTime java.util.Date
     */
    public void setLastOperationTime(java.util.Date newLastOperationTime) {
        lastOperationTime = newLastOperationTime;
    }

    /**
     * Insert the method's description here.
     */
    public void setMapLocationID(String newMapLocationID) {
        mapLocationID = newMapLocationID;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @param newMaxDailyOperation java.lang.Integer
     */
    public void setMaxDailyOperation(java.lang.Integer newMaxDailyOperation) {
        maxDailyOperation = newMaxDailyOperation;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @param newMaxOperationDisableFlag java.lang.Boolean
     */
    public void setMaxOperationDisableFlag(
            java.lang.Boolean newMaxOperationDisableFlag) {
        maxOperationDisableFlag = newMaxOperationDisableFlag;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @param newNewPointDataReceivedFlag java.lang.Boolean
     */
    public void setNewPointDataReceivedFlag(
            java.lang.Boolean newNewPointDataReceivedFlag) {
        newPointDataReceivedFlag = newNewPointDataReceivedFlag;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @param newPeakTimeFlag java.lang.Boolean
     */
    public void setPeakTimeFlag(java.lang.Boolean newPeakTimeFlag) {
        peakTimeFlag = newPeakTimeFlag;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @param newRecentlyConrolledFlag java.lang.Boolean
     */
    public void setRecentlyControlledFlag(
            java.lang.Boolean newRecentlyConrolledFlag) {
        recentlyControlledFlag = newRecentlyConrolledFlag;
    }

    /**
     * Insert the method's description here. Creation date: (11/19/2001 12:01:28
     * PM)
     * @param newVarValueBeforeControl java.lang.Double
     */
    public void setVarValueBeforeControl(
            java.lang.Double newVarValueBeforeControl) {
        varValueBeforeControl = newVarValueBeforeControl;
    }

    /**
     * Returns the powerFactorValue.
     * @return Double
     */
    public Double getPowerFactorValue() {
        return powerFactorValue;
    }

    /**
     * Sets the powerFactorValue.
     * @param powerFactorValue The powerFactorValue to set
     */
    public void setPowerFactorValue(Double powerFactorValue) {
        this.powerFactorValue = powerFactorValue;
    }

    /**
     * Returns the estimatedPFValue.
     * @return Double
     */
    public Double getEstimatedPFValue() {
        return estimatedPFValue;
    }

    /**
     * Sets the estimatedPFValue.
     * @param estimatedPFValue The estimatedPFValue to set
     */
    public void setEstimatedPFValue(Double estimatedPFValue) {
        this.estimatedPFValue = estimatedPFValue;
    }

    /**
     * @return
     */
    public Boolean getWaiveControlFlag() {
        return waiveControlFlag;
    }

    /**
     * @param boolean1
     */
    public void setWaiveControlFlag(Boolean boolean1) {
        waiveControlFlag = boolean1;
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
    public Boolean getVerificationFlag() {
        return verificationFlag;
    }

    /**
     * @param boolean1
     */
    public void setVerificationFlag(Boolean boolean1) {
        verificationFlag = boolean1;
    }

    /**
     * @return
     */
    public ControlAlgorithm getControlUnits() {
        return controlUnits;
    }

    /**
     * @return
     */
    public Integer getCurrentVarLoadPointID() {
        return currentVarLoadPointID;
    }

    /**
     * @return
     */
    public Integer getCurrentWattLoadPointID() {
        return currentWattLoadPointID;
    }

    /**
     * @param string
     */
    public void setControlUnits(ControlAlgorithm units) {
        controlUnits = units;
    }

    /**
     * @param integer
     */
    public void setCurrentVarLoadPointID(Integer integer) {
        currentVarLoadPointID = integer;
    }

    /**
     * @param integer
     */
    public void setCurrentWattLoadPointID(Integer integer) {
        currentWattLoadPointID = integer;
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
    public Integer getDailyOperationsAnalogPointId() {
        return dailyOperationsAnalogPointId;
    }

    /**
     * @return
     */
    public Integer getEstimatedPowerFactorPointId() {
        return estimatedPowerFactorPointId;
    }

    /**
     * @return
     */
    public Integer getEstimatedVarLoadPointID() {
        return estimatedVarLoadPointID;
    }

    /**
     * @return
     */
    public Integer getPowerFactorPointId() {
        return powerFactorPointId;
    }

    /**
     * @param integer
     */
    public void setCurrentVoltLoadPointID(Integer integer) {
        currentVoltLoadPointID = integer;
    }

    /**
     * @param integer
     */
    public void setDailyOperationsAnalogPointId(Integer integer) {
        dailyOperationsAnalogPointId = integer;
    }

    /**
     * @param integer
     */
    public void setEstimatedPowerFactorPointId(Integer integer) {
        estimatedPowerFactorPointId = integer;
    }

    /**
     * @param integer
     */
    public void setEstimatedVarLoadPointID(Integer integer) {
        estimatedVarLoadPointID = integer;
    }

    /**
     * @param integer
     */
    public void setPowerFactorPointId(Integer integer) {
        powerFactorPointId = integer;
    }

    public void setSwitchOverStatus(Boolean b) {
        switchOverStatus = b;

    }

    public Boolean getSwitchOverStatus() {
        return switchOverStatus;
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

	public ControlMethod getControlMethod() {
		return controlMethod;
	}

	public void setControlMethod(ControlMethod controlMethod) {
		this.controlMethod = controlMethod;
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

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

	public Boolean getVoltReductionFlag() {
		return voltReductionFlag;
	}

	public void setVoltReductionFlag(Boolean voltReductionFlag) {
		this.voltReductionFlag = voltReductionFlag;
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

    public Boolean getPrimaryBusFlag() {
        return primaryBusFlag;
    }

    public void setPrimaryBusFlag(Boolean primaryBusFlag) {
        this.primaryBusFlag = primaryBusFlag;
    }

    public Integer getAlternateBusId() {
        return alternateBusId;
    }

    public void setAlternateBusId(Integer alternateBusId) {
        this.alternateBusId = alternateBusId;
    }

    public Boolean getDualBusEnabled() {
        return dualBusEnabled;
    }

    public void setDualBusEnabled(Boolean dualBusEnabled) {
        this.dualBusEnabled = dualBusEnabled;
    }
    
    public void setStrategyId(Integer strategyId) {
        this.strategyId = strategyId;
    }

    public Integer getStrategyId() {
        return strategyId;
    }
    
}