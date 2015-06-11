package com.cannontech.message.capcontrol.streamable;

import java.util.List;
import java.util.Vector;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.database.data.point.UnitOfMeasure;

public class Feeder extends StreamableCapObject implements PointQualityCheckable {

    private int maxDailyOperation;
    private boolean maxOperationDisableFlag;
    private boolean ovUvDisabledFlag;
    private boolean likeDayControlFlag;

    private int currentVarLoadPointID;
    private double currentVarLoadPointValue;
    private int currentWattLoadPointID;
    private double currentWattLoadPointValue;
    private String mapLocationID;

    private float displayOrder;
    private boolean newPointDataReceivedFlag;
    private java.util.Date lastCurrentVarPointUpdateTime;
    private int estimatedVarLoadPointID;
    private double estimatedVarLoadPointValue;

    private int dailyOperationsAnalogPointID;
    private int powerFactorPointID;
    private int estimatedPowerFactorPointID;
    private int currentDailyOperations;
    private boolean recentlyControlledFlag;
    private java.util.Date lastOperationTime;
    private double varValueBeforeControl;

    private double powerFactorValue;
    private double estimatedPFValue;
    private int currentVarPtQuality;
    private boolean waiveControlFlag;

    private ControlAlgorithm algorithm = ControlAlgorithm.KVAR;
    private int decimalPlaces;
    private boolean peakTimeFlag;

    private double peakLag;
    private double offPkLag;
    private double peakLead;
    private double offPkLead;
    private int currentVoltLoadPointID;
    private double currentVoltLoadPointValue;
    private int currentwattpointquality;
    private int currentvoltpointquality;
    private double targetvarvalue;
    private String solution;
    private double peakPFSetPoint = 100;
    private double offpeakPFSetPoint = 100;
    private ControlMethod controlmethod;
    private double phaseA;
    private double phaseB;
    private double phaseC;
    private Vector<CapBankDevice> ccCapBanks;
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

    public List<CapBankDevice> getCcCapBanks() {
        return ccCapBanks;
    }

    public int getCurrentDailyOperations() {
        return currentDailyOperations;
    }

    public int getCurrentVarLoadPointID() {
        return currentVarLoadPointID;
    }

    public double getCurrentVarLoadPointValue() {
        return currentVarLoadPointValue;
    }

    public int getCurrentWattLoadPointID() {
        return currentWattLoadPointID;
    }

    public double getCurrentWattLoadPointValue() {
        return currentWattLoadPointValue;
    }

    public int getDailyOperationsAnalogPointID() {
        return dailyOperationsAnalogPointID;
    }

    public float getDisplayOrder() {
        return displayOrder;
    }

    public int getEstimatedVarLoadPointID() {
        return estimatedVarLoadPointID;
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

    public String getMapLocationID() {
        return mapLocationID;
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

    public void setCurrentVarLoadPointID(int newCurrentVarLoadPointID) {
        currentVarLoadPointID = newCurrentVarLoadPointID;
    }

    public void setCurrentVarLoadPointValue(double newCurrentVarLoadPointValue) {
        currentVarLoadPointValue = newCurrentVarLoadPointValue;
    }

    public void setCurrentWattLoadPointID(int newCurrentWattLoadPointID) {
        currentWattLoadPointID = newCurrentWattLoadPointID;
    }

    public void setCurrentWattLoadPointValue(double newCurrentWattLoadPointValue) {
        currentWattLoadPointValue = newCurrentWattLoadPointValue;
    }

    public void setDailyOperationsAnalogPointID(int newDailyOperationsAnalogPointID) {
        dailyOperationsAnalogPointID = newDailyOperationsAnalogPointID;
    }

    public void setDisplayOrder(float newDisplayOrder) {
        displayOrder = newDisplayOrder;
    }

    public void setEstimatedVarLoadPointID(int newEstimatedVarLoadPointID) {
        estimatedVarLoadPointID = newEstimatedVarLoadPointID;
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

    public void setMapLocationID(String newMapLocationID) {
        mapLocationID = newMapLocationID;
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

    public double getPowerFactorValue() {
        return powerFactorValue;
    }

    public void setPowerFactorValue(double powerFactorValue) {
        this.powerFactorValue = powerFactorValue;
    }

    public double getEstimatedPFValue() {
        return estimatedPFValue;
    }

    public void setEstimatedPFValue(double estimatedPFValue) {
        this.estimatedPFValue = estimatedPFValue;
    }

    public int getEstimatedPowerFactorPointID() {
        return estimatedPowerFactorPointID;
    }

    public int getPowerFactorPointID() {
        return powerFactorPointID;
    }

    public void setEstimatedPowerFactorPointID(int estimatedPowerFactorPointID) {
        this.estimatedPowerFactorPointID = estimatedPowerFactorPointID;
    }

    public void setPowerFactorPointID(int powerFactorPointID) {
        this.powerFactorPointID = powerFactorPointID;
    }

    public boolean getWaiveControlFlag() {
        return waiveControlFlag;
    }

    public void setWaiveControlFlag(boolean boolean1) {
        waiveControlFlag = boolean1;
    }

    public ControlAlgorithm getAlgorithm() {
        return algorithm;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    public boolean getPeakTimeFlag() {
        return peakTimeFlag;
    }

    public void setAlgorithm(ControlAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void setDecimalPlaces(int i) {
        decimalPlaces = i;
    }

    public void setPeakTimeFlag(boolean boolean1) {
        peakTimeFlag = boolean1;
    }

    public int getCurrentVoltLoadPointID() {
        return currentVoltLoadPointID;
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

    public void setCurrentVoltLoadPointID(int integer) {
        currentVoltLoadPointID = integer;
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

    @Override
    public int getCurrentPtQuality(UnitOfMeasure uom) {
        if (uom.isCapControlVar())
            return getCurrentVarPtQuality();
        if (uom.isCapControlWatt())
            return getCurrentwattpointquality();
        if (uom.isCapControlVolt())
            return getCurrentvoltpointquality();
       throw new IllegalArgumentException("Feeder only supports Var, Watt and Volt UnitOfMeasure");
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
        offpeakPFSetPoint = offpeakPFSetting;
    }

    public double getPeakPFSetPoint() {
        return peakPFSetPoint;
    }

    public void setPeakPFSetPoint(double peakPFSetting) {
        peakPFSetPoint = peakPFSetting;
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
        return algorithm == ControlAlgorithm.PFACTOR_KW_KVAR;
    }

    public int getOriginalParentId() {
        return originalParentId;
    }

    public void setOriginalParentId(int originalParentId) {
        this.originalParentId = originalParentId;
    }
}