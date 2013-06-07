package com.cannontech.messaging.message.capcontrol.streamable;

import java.util.Vector;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.database.data.point.UnitOfMeasure;

/**
 * Insert the type's description here. Creation date: (8/18/00 4:23:32 PM)
 * @author:
 */
public class SubBus extends StreamableCapObject implements PointQualityCheckable {

    private int maxDailyOperation;
    private boolean maxOperationDisableFlag;
    private double currentVarLoadPointValue;
    private boolean ovUvDisabledFlag;
    private boolean likeDayControlFlag;

    private double currentWattLoadPointValue;
    private String mapLocationId = null;
    private int decimalPlaces;
    private boolean newPointDataReceivedFlag;
    private boolean busUpdateFlag;
    private java.util.Date lastCurrentVarPointUpdateTime = null;
    private double estimatedVarLoadPointValue;

    private int currentVarLoadPointId;
    private int currentWattLoadPointId;
    private ControlAlgorithm controlUnits = null;
    private int estimatedVarLoadPointId;
    private int dailyOperationsAnalogPointId;
    private int powerFactorPointId;
    private int estimatedPowerFactorPointId;
    private int currentVoltLoadPointId;

    private int currentDailyOperations;
    private boolean peakTimeFlag;
    private boolean recentlyControlledFlag;
    private java.util.Date lastOperationTime = null;
    private double varValueBeforeControl;

    private double powerFactorValue;
    private double estimatedPFValue;
    private int currentVarPtQuality;
    private boolean waiveControlFlag;

    private double peakLag = 0.0;
    private double offPkLag = 0.0;
    private double peakLead = 0.0;
    private double offPkLead = 0.0;
    private double currentVoltLoadPointValue;
    private boolean verificationFlag;
    private int currentwattpointquality;
    private int currentvoltpointquality;
    private double targetvarvalue;
    private String solution = null;

    private Vector<Feeder> ccFeeders = new Vector<Feeder>();
    private boolean switchOverStatus;
    private double peakPFSetPoint = 100.0;
    private double offpeakPFSetPoint = 100.0;
    private double phaseA = 0.0;
    private double phaseB = 0.0;
    private double phaseC = 0.0;
    private ControlMethod controlMethod = null;
    private int displayOrder;
    private boolean voltReductionFlag;
    private boolean usePhaseData;

    private boolean primaryBusFlag;
    private boolean dualBusEnabled;
    private int alternateBusId;
    private int strategyId = -1;

    /**
     * constructor comment.
     */
    public SubBus() {
        super();
    }

    /**
     * StreamableCapObject constructor comment.
     */
    public SubBus(int paoId_, String paoCategory_, String paoClass_, String paoName_, String paoType_,
                  String paoDescription_, boolean paoDisableFlag_) {
        super(paoId_, paoCategory_, paoClass_, paoName_, paoType_, paoDescription_, paoDisableFlag_);

    }

    public int getCurrentVarPtQuality() {
        return currentVarPtQuality;
    }

    public void setCurrentVarPtQuality(int ptQ_) {
        currentVarPtQuality = ptQ_;
    }

    public boolean getBusUpdateFlag() {
        return busUpdateFlag;
    }

    public Vector<Feeder> getCcFeeders() {
        return ccFeeders;
    }

    public int getCurrentDailyOperations() {
        return currentDailyOperations;
    }

    public double getCurrentVarLoadPointValue() {
        return currentVarLoadPointValue;
    }

    public double getCurrentWattLoadPointValue() {
        return currentWattLoadPointValue;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public double getEstimatedVarLoadPointValue() {
        return estimatedVarLoadPointValue;
    }

    public java.util.Date getLastCurrentVarPointUpdateTime() {
        return lastCurrentVarPointUpdateTime;
    }

    public java.util.Date getLastOperationTime() {
        return lastOperationTime;
    }

    public String getMapLocationId() {
        return mapLocationId;
    }

    public int getMaxDailyOperation() {
        return maxDailyOperation;
    }

    public boolean getMaxOperationDisableFlag() {
        return maxOperationDisableFlag;
    }

    public boolean getNewPointDataReceivedFlag() {
        return newPointDataReceivedFlag;
    }

    public boolean getPeakTimeFlag() {
        return peakTimeFlag;
    }

    public boolean getRecentlyControlledFlag() {
        return recentlyControlledFlag;
    }

    public double getVarValueBeforeControl() {
        return varValueBeforeControl;
    }

    public void setBusUpdateFlag(boolean newBusUpdateFlag) {
        busUpdateFlag = newBusUpdateFlag;
    }

    public void setCcFeeders(Vector<Feeder> newCcFeeders) {
        ccFeeders = newCcFeeders;
    }

    public void setCurrentDailyOperations(int newCurrentDailyOperations) {
        currentDailyOperations = newCurrentDailyOperations;
    }

    public void setCurrentVarLoadPointValue(double newCurrentVarLoadPointValue) {
        currentVarLoadPointValue = newCurrentVarLoadPointValue;
    }

    public void setCurrentWattLoadPointValue(double newCurrentWattLoadPointValue) {
        currentWattLoadPointValue = newCurrentWattLoadPointValue;
    }

    public void setDecimalPlaces(int newDecimalPlaces) {
        decimalPlaces = newDecimalPlaces;
    }

    public void setEstimatedVarLoadPointValue(double newEstimatedVarLoadPointValue) {
        estimatedVarLoadPointValue = newEstimatedVarLoadPointValue;
    }

    public void setLastCurrentVarPointUpdateTime(java.util.Date newLastCurrentVarPointUpdateTime) {
        lastCurrentVarPointUpdateTime = newLastCurrentVarPointUpdateTime;
    }

    public void setLastOperationTime(java.util.Date newLastOperationTime) {
        lastOperationTime = newLastOperationTime;
    }

    public void setMapLocationId(String newMapLocationId) {
        mapLocationId = newMapLocationId;
    }

    public void setMaxDailyOperation(int newMaxDailyOperation) {
        maxDailyOperation = newMaxDailyOperation;
    }

    public void setMaxOperationDisableFlag(boolean newMaxOperationDisableFlag) {
        maxOperationDisableFlag = newMaxOperationDisableFlag;
    }

    public void setNewPointDataReceivedFlag(boolean newNewPointDataReceivedFlag) {
        newPointDataReceivedFlag = newNewPointDataReceivedFlag;
    }

    public void setPeakTimeFlag(boolean newPeakTimeFlag) {
        peakTimeFlag = newPeakTimeFlag;
    }

    public void setRecentlyControlledFlag(boolean newRecentlyConrolledFlag) {
        recentlyControlledFlag = newRecentlyConrolledFlag;
    }

    public void setVarValueBeforeControl(double newVarValueBeforeControl) {
        varValueBeforeControl = newVarValueBeforeControl;
    }

    /**
     * Returns the powerFactorValue.
     * @return double
     */
    public double getPowerFactorValue() {
        return powerFactorValue;
    }

    /**
     * Sets the powerFactorValue.
     * @param powerFactorValue The powerFactorValue to set
     */
    public void setPowerFactorValue(double powerFactorValue) {
        this.powerFactorValue = powerFactorValue;
    }

    /**
     * Returns the estimatedPFValue.
     * @return double
     */
    public double getEstimatedPFValue() {
        return estimatedPFValue;
    }

    /**
     * Sets the estimatedPFValue.
     * @param estimatedPFValue The estimatedPFValue to set
     */
    public void setEstimatedPFValue(double estimatedPFValue) {
        this.estimatedPFValue = estimatedPFValue;
    }

    public boolean getWaiveControlFlag() {
        return waiveControlFlag;
    }

    public void setWaiveControlFlag(boolean boolean1) {
        waiveControlFlag = boolean1;
    }

    public double getCurrentVoltLoadPointValue() {
        return currentVoltLoadPointValue;
    }

    public double getOffPkLag() {
        return offPkLag;
    }

    public double getOffPkLead() {
        return offPkLead;
    }

    public double getPeakLag() {
        return peakLag;
    }

    public double getPeakLead() {
        return peakLead;
    }

    public void setCurrentVoltLoadPointValue(double double1) {
        currentVoltLoadPointValue = double1;
    }

    public void setOffPkLag(double double1) {
        offPkLag = double1;
    }

    public void setOffPkLead(double double1) {
        offPkLead = double1;
    }

    public void setPeakLag(double double1) {
        peakLag = double1;
    }

    public void setPeakLead(double double1) {
        peakLead = double1;
    }

    public boolean getVerificationFlag() {
        return verificationFlag;
    }

    public void setVerificationFlag(boolean boolean1) {
        verificationFlag = boolean1;
    }

    public ControlAlgorithm getControlUnits() {
        return controlUnits;
    }

    public int getCurrentVarLoadPointId() {
        return currentVarLoadPointId;
    }

    public int getCurrentWattLoadPointId() {
        return currentWattLoadPointId;
    }

    public void setControlUnits(ControlAlgorithm units) {
        controlUnits = units;
    }

    public void setCurrentVarLoadPointId(int integer) {
        currentVarLoadPointId = integer;
    }

    public void setCurrentWattLoadPointId(int integer) {
        currentWattLoadPointId = integer;
    }

    public int getCurrentVoltLoadPointId() {
        return currentVoltLoadPointId;
    }

    public int getDailyOperationsAnalogPointId() {
        return dailyOperationsAnalogPointId;
    }

    public int getEstimatedPowerFactorPointId() {
        return estimatedPowerFactorPointId;
    }

    public int getEstimatedVarLoadPointId() {
        return estimatedVarLoadPointId;
    }

    public int getPowerFactorPointId() {
        return powerFactorPointId;
    }

    public void setCurrentVoltLoadPointId(int integer) {
        currentVoltLoadPointId = integer;
    }

    public void setDailyOperationsAnalogPointId(int integer) {
        dailyOperationsAnalogPointId = integer;
    }

    public void setEstimatedPowerFactorPointId(int integer) {
        estimatedPowerFactorPointId = integer;
    }

    public void setEstimatedVarLoadPointId(int integer) {
        estimatedVarLoadPointId = integer;
    }

    public void setPowerFactorPointId(int integer) {
        powerFactorPointId = integer;
    }

    public void setSwitchOverStatus(boolean b) {
        switchOverStatus = b;

    }

    public boolean getSwitchOverStatus() {
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

    public int getCurrentvoltpointquality() {
        return currentvoltpointquality;
    }

    public void setCurrentvoltpointquality(int currentvoltpointquality) {
        this.currentvoltpointquality = currentvoltpointquality;
    }

    public int getCurrentwattpointquality() {
        return currentwattpointquality;
    }

    public void setCurrentwattpointquality(int currentwattpointquality) {
        this.currentwattpointquality = currentwattpointquality;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public double getTargetvarvalue() {
        return targetvarvalue;
    }

    public void setTargetvarvalue(double targetvarvalue) {
        this.targetvarvalue = targetvarvalue;
    }

    public boolean getOvUvDisabledFlag() {
        return ovUvDisabledFlag;
    }

    public void setOvUvDisabledFlag(boolean ovUvDisabledFlag) {
        this.ovUvDisabledFlag = ovUvDisabledFlag;
    }

    public double getOffpeakPFSetPoint() {
        return offpeakPFSetPoint;
    }

    public void setOffpeakPFSetPoint(double offpeakPFSetting) {
        this.offpeakPFSetPoint = offpeakPFSetting;
    }

    public double getPeakPFSetPoint() {
        return peakPFSetPoint;
    }

    public void setPeakPFSetPoint(double peakPFSetting) {
        this.peakPFSetPoint = peakPFSetting;
    }

    public ControlMethod getControlMethod() {
        return controlMethod;
    }

    public void setControlMethod(ControlMethod controlMethod) {
        this.controlMethod = controlMethod;
    }

    public double getPhaseA() {
        return phaseA;
    }

    public void setPhaseA(double phaseA) {
        this.phaseA = phaseA;
    }

    public double getPhaseB() {
        return phaseB;
    }

    public void setPhaseB(double phaseB) {
        this.phaseB = phaseB;
    }

    public double getPhaseC() {
        return phaseC;
    }

    public void setPhaseC(double phaseC) {
        this.phaseC = phaseC;
    }

    public boolean getLikeDayControlFlag() {
        return likeDayControlFlag;
    }

    public void setLikeDayControlFlag(boolean likeDayControlFlag) {
        this.likeDayControlFlag = likeDayControlFlag;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public boolean getVoltReductionFlag() {
        return voltReductionFlag;
    }

    public void setVoltReductionFlag(boolean voltReductionFlag) {
        this.voltReductionFlag = voltReductionFlag;
    }

    public boolean getUsePhaseData() {
        return usePhaseData;
    }

    public void setUsePhaseData(boolean usePhaseData) {
        this.usePhaseData = usePhaseData;
    }

    public boolean isPowerFactorControlled() {
        return controlUnits == ControlAlgorithm.PFACTOR_KW_KVAR;
    }

    public boolean getPrimaryBusFlag() {
        return primaryBusFlag;
    }

    public void setPrimaryBusFlag(boolean primaryBusFlag) {
        this.primaryBusFlag = primaryBusFlag;
    }

    public int getAlternateBusId() {
        return alternateBusId;
    }

    public void setAlternateBusId(int alternateBusId) {
        this.alternateBusId = alternateBusId;
    }

    public boolean getDualBusEnabled() {
        return dualBusEnabled;
    }

    public void setDualBusEnabled(boolean dualBusEnabled) {
        this.dualBusEnabled = dualBusEnabled;
    }

    public void setStrategyId(int strategyId) {
        this.strategyId = strategyId;
    }

    public int getStrategyId() {
        return strategyId;
    }
}
