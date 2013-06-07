package com.cannontech.messaging.message.capcontrol.streamable;

import java.util.Vector;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.point.UnitOfMeasure;

/**
 * A feeder object
 */
public class Feeder extends StreamableCapObject implements PointQualityCheckable {

    private int maxDailyOperation;
    private boolean maxOperationDisableFlag;
    private boolean ovUvDisabledFlag;
    private boolean likeDayControlFlag;

    private int currentVarLoadPointId;
    private double currentVarLoadPointValue;
    private int currentWattLoadPointId;
    private double currentWattLoadPointValue;
    private String mapLocationId = null;

    private float displayOrder;
    private boolean newPointDataReceivedFlag;
    private java.util.Date lastCurrentVarPointUpdateTime = null;
    private int estimatedVarLoadPointId;
    private double estimatedVarLoadPointValue;

    private int dailyOperationsAnalogPointId;
    private int powerFactorPointId;
    private int estimatedPowerFactorPointId;
    private int currentDailyOperations;
    private boolean recentlyControlledFlag;
    private java.util.Date lastOperationTime = null;
    private double varValueBeforeControl;

    private double powerFactorValue;
    private double estimatedPowerFactorValue;
    private int currentVarPtQuality;
    private boolean waiveControlFlag;

    private ControlAlgorithm controlUnits = ControlAlgorithm.KVAR;
    private int decimalPlaces = 0;
    private boolean peakTimeFlag = true;

    private double peakLag = 0.0;
    private double offPkLag = 0.0;
    private double peakLead = 0.0;
    private double offPkLead = 0.0;
    private int currentVoltLoadPointId = CtiUtilities.NONE_ZERO_ID;
    private double currentVoltLoadPointValue;
    private int currentwattpointquality;
    private int currentvoltpointquality;
    private double targetvarvalue;
    private String solution = null;
    private double peakPFSetPoint = 100.0;
    private double offpeakPFSetPoint = 100.0;
    private ControlMethod controlmethod = null;
    private double phaseA = 0.0;
    private double phaseB = 0.0;
    private double phaseC = 0.0;
    private Vector<CapBankDevice> ccCapBanks = null;
    private boolean usePhaseData;
    private int originalParentId;

    public Feeder() {
        super();
    }

    public Feeder(int paoId_, String paoCategory_, String paoClass_, String paoName_, String paoType_,
                  String paoDescription_, boolean paoDisableFlag_) {
        super(paoId_, paoCategory_, paoClass_, paoName_, paoType_, paoDescription_, paoDisableFlag_);
    }

    public int getCurrentVarPtQuality() {
        return currentVarPtQuality;
    }

    public void setCurrentVarPtQuality(int ptQ_) {
        currentVarPtQuality = ptQ_;
    }

    public Vector<CapBankDevice> getCcCapBanks() {
        return ccCapBanks;
    }

    public int getCurrentDailyOperations() {
        return currentDailyOperations;
    }

    public int getCurrentVarLoadPointId() {
        return currentVarLoadPointId;
    }

    public double getCurrentVarLoadPointValue() {
        return currentVarLoadPointValue;
    }

    public int getCurrentWattLoadPointId() {
        return currentWattLoadPointId;
    }

    public double getCurrentWattLoadPointValue() {
        return currentWattLoadPointValue;
    }

    public int getDailyOperationsAnalogPointId() {
        return dailyOperationsAnalogPointId;
    }

    public float getDisplayOrder() {
        return displayOrder;
    }

    public int getEstimatedVarLoadPointId() {
        return estimatedVarLoadPointId;
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

    public boolean getNewPointDataReceivedFlag() {
        return newPointDataReceivedFlag;
    }

    public boolean getRecentlyControlledFlag() {
        return recentlyControlledFlag;
    }

    public double getVarValueBeforeControl() {
        return varValueBeforeControl;
    }

    public void setCcCapBanks(Vector<CapBankDevice> newCcCapBanks) {
        ccCapBanks = newCcCapBanks;
    }

    public void setCurrentDailyOperations(int newCurrentDailyOperations) {
        currentDailyOperations = newCurrentDailyOperations;
    }

    public void setCurrentVarLoadPointId(int newCurrentVarLoadPointId) {
        currentVarLoadPointId = newCurrentVarLoadPointId;
    }

    public void setCurrentVarLoadPointValue(double newCurrentVarLoadPointValue) {
        currentVarLoadPointValue = newCurrentVarLoadPointValue;
    }

    public void setCurrentWattLoadPointId(int newCurrentWattLoadPointId) {
        currentWattLoadPointId = newCurrentWattLoadPointId;
    }

    public void setCurrentWattLoadPointValue(double newCurrentWattLoadPointValue) {
        currentWattLoadPointValue = newCurrentWattLoadPointValue;
    }

    public void setDailyOperationsAnalogPointId(int newDailyOperationsAnalogPointId) {
        dailyOperationsAnalogPointId = newDailyOperationsAnalogPointId;
    }

    public void setDisplayOrder(float newDisplayOrder) {
        displayOrder = newDisplayOrder;
    }

    public void setEstimatedVarLoadPointId(int newEstimatedVarLoadPointId) {
        estimatedVarLoadPointId = newEstimatedVarLoadPointId;
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

    public void setNewPointDataReceivedFlag(boolean newNewPointDataReceivedFlag) {
        newPointDataReceivedFlag = newNewPointDataReceivedFlag;
    }

    public void setRecentlyControlledFlag(boolean newRecentlyControlledFlag) {
        recentlyControlledFlag = newRecentlyControlledFlag;
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
    public double getEstimatedPowerFactorValue() {
        return estimatedPowerFactorValue;
    }

    /**
     * Sets the estimatedPFValue.
     * @param estimatedPFValue The estimatedPFValue to set
     */
    public void setEstimatedPowerFactorValue(double estimatedPFValue) {
        this.estimatedPowerFactorValue = estimatedPFValue;
    }

    /**
     * Returns the estimatedPowerFactorPointId.
     * @return int
     */
    public int getEstimatedPowerFactorPointId() {
        return estimatedPowerFactorPointId;
    }

    /**
     * Returns the powerFactorPointId.
     * @return int
     */
    public int getPowerFactorPointId() {
        return powerFactorPointId;
    }

    /**
     * Sets the estimatedPowerFactorPointId.
     * @param estimatedPowerFactorPointId The estimatedPowerFactorPointId to set
     */
    public void setEstimatedPowerFactorPointId(int estimatedPowerFactorPointId) {
        this.estimatedPowerFactorPointId = estimatedPowerFactorPointId;
    }

    /**
     * Sets the powerFactorPointId.
     * @param powerFactorPointId The powerFactorPointId to set
     */
    public void setPowerFactorPointId(int powerFactorPointId) {
        this.powerFactorPointId = powerFactorPointId;
    }

    public boolean getWaiveControlFlag() {
        return waiveControlFlag;
    }

    public void setWaiveControlFlag(boolean boolean1) {
        waiveControlFlag = boolean1;
    }

    public ControlAlgorithm getControlUnits() {
        return controlUnits;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public boolean getPeakTimeFlag() {
        return peakTimeFlag;
    }

    public void setControlUnits(ControlAlgorithm units) {
        controlUnits = units;
    }

    public void setDecimalPlaces(int i) {
        decimalPlaces = i;
    }

    public void setPeakTimeFlag(boolean boolean1) {
        peakTimeFlag = boolean1;
    }

    public int getCurrentVoltLoadPointId() {
        return currentVoltLoadPointId;
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

    public void setCurrentVoltLoadPointId(int integer) {
        currentVoltLoadPointId = integer;
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

    public int getMaxDailyOperation() {
        return maxDailyOperation;
    }

    public boolean getMaxOperationDisableFlag() {
        return maxOperationDisableFlag;
    }

    public void setMaxDailyOperation(int integer) {
        maxDailyOperation = integer;
    }

    public void setMaxOperationDisableFlag(boolean boolean1) {
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

    public ControlMethod getControlmethod() {
        return controlmethod;
    }

    public void setControlmethod(ControlMethod controlmethod) {
        this.controlmethod = controlmethod;
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

    public boolean getUsePhaseData() {
        return usePhaseData;
    }

    public void setUsePhaseData(boolean usePhaseData) {
        this.usePhaseData = usePhaseData;
    }

    public boolean isPowerFactorControlled() {
        return controlUnits == ControlAlgorithm.PFACTOR_KW_KVAR;
    }

    public int getOriginalParentId() {
        return originalParentId;
    }

    public void setOriginalParentId(int originalParentId) {
        this.originalParentId = originalParentId;
    }
}
