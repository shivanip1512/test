package com.cannontech.message.capcontrol.streamable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.ControlMethod;
import com.cannontech.database.data.point.UnitOfMeasure;

public class SubBus extends StreamableCapObject implements PointQualityCheckable {

    private int maxDailyOperation;
    private boolean maxOperationDisableFlag;
    private double currentVarLoadPointValue;
    private boolean ovUvDisabledFlag;
    private boolean likeDayControlFlag;

    private double currentWattLoadPointValue;
    private String mapLocationID;
    private int decimalPlaces;
    private boolean newPointDataReceivedFlag;
    private boolean busUpdateFlag;
    private Date lastCurrentVarPointUpdateTime;
    private double estimatedVarLoadPointValue;

    private int currentVarLoadPointID;
    private int currentWattLoadPointID;
    private ControlAlgorithm algorithm;
    private int estimatedVarLoadPointID;
    private int dailyOperationsAnalogPointId;
    private int powerFactorPointId;
    private int estimatedPowerFactorPointId;
    private int currentVoltLoadPointID;

    private int currentDailyOperations;
    private boolean peakTimeFlag;
    private boolean recentlyControlledFlag;
    private Date lastOperationTime;
    private double varValueBeforeControl;

    private double powerFactorValue;
    private double estimatedPFValue;
    private int currentVarPtQuality;
    private boolean waiveControlFlag;

    private double peakLag;
    private double offPkLag;
    private double peakLead;
    private double offPkLead;
    private double currentVoltLoadPointValue;
    private boolean verificationFlag;
    private int currentwattpointquality;
    private int currentvoltpointquality;
    private double targetvarvalue;
    private String solution;

    private List<Feeder> ccFeeders = new ArrayList<>();
    private boolean switchOverStatus;
    private double peakPFSetPoint = 100;
    private double offpeakPFSetPoint = 100;
    private double phaseA;
    private double phaseB;
    private double phaseC;
    private ControlMethod controlMethod;
    private int displayOrder;
    private boolean voltReductionFlag;
    private boolean usePhaseData;

    private boolean primaryBusFlag;
    private boolean dualBusEnabled;
    private int alternateBusId;
    private int strategyId = -1;

    public SubBus() {
        super();
    }

    public SubBus(int paoId_, String paoCategory_, String paoClass_,
            String paoName_, String paoType_, String paoDescription_,
            boolean paoDisableFlag_) {
        super(paoId_,
              paoCategory_,
              paoClass_,
              paoName_,
              paoType_,
              paoDescription_,
              paoDisableFlag_);

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

    public List<Feeder> getCcFeeders() {
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

    public Date getLastCurrentVarPointUpdateTime() {
        return lastCurrentVarPointUpdateTime;
    }

    public Date getLastOperationTime() {
        return lastOperationTime;
    }

    public String getMapLocationID() {
        return mapLocationID;
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

    public void setCcFeeders(List<Feeder> ccFeeders) {
        this.ccFeeders = ccFeeders;
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

    public void setLastCurrentVarPointUpdateTime(
            java.util.Date newLastCurrentVarPointUpdateTime) {
        lastCurrentVarPointUpdateTime = newLastCurrentVarPointUpdateTime;
    }

    public void setLastOperationTime(Date newLastOperationTime) {
        lastOperationTime = newLastOperationTime;
    }


    public void setMapLocationID(String newMapLocationID) {
        mapLocationID = newMapLocationID;
    }

    public void setMaxDailyOperation(int newMaxDailyOperation) {
        maxDailyOperation = newMaxDailyOperation;
    }

    public void setMaxOperationDisableFlag(
            boolean newMaxOperationDisableFlag) {
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

    public ControlAlgorithm getAlgorithm() {
        return algorithm;
    }

    public int getCurrentVarLoadPointID() {
        return currentVarLoadPointID;
    }

    public int getCurrentWattLoadPointID() {
        return currentWattLoadPointID;
    }

    public void setAlgorithm(ControlAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void setCurrentVarLoadPointID(int integer) {
        currentVarLoadPointID = integer;
    }

    public void setCurrentWattLoadPointID(int integer) {
        currentWattLoadPointID = integer;
    }

    public int getCurrentVoltLoadPointID() {
        return currentVoltLoadPointID;
    }

    public int getDailyOperationsAnalogPointId() {
        return dailyOperationsAnalogPointId;
    }

    public int getEstimatedPowerFactorPointId() {
        return estimatedPowerFactorPointId;
    }

    public int getEstimatedVarLoadPointID() {
        return estimatedVarLoadPointID;
    }

    public int getPowerFactorPointId() {
        return powerFactorPointId;
    }

    public void setCurrentVoltLoadPointID(int integer) {
        currentVoltLoadPointID = integer;
    }

    public void setDailyOperationsAnalogPointId(int integer) {
        dailyOperationsAnalogPointId = integer;
    }

    public void setEstimatedPowerFactorPointId(int integer) {
        estimatedPowerFactorPointId = integer;
    }

    public void setEstimatedVarLoadPointID(int integer) {
        estimatedVarLoadPointID = integer;
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

    @Override
    public int getCurrentPtQuality(UnitOfMeasure uom) {
        if (uom.isCapControlVar())
            return getCurrentVarPtQuality();
        if (uom.isCapControlWatt())
            return getCurrentwattpointquality();
        if (uom.isCapControlVolt())
            return getCurrentvoltpointquality();
        throw new IllegalArgumentException("SubBus only supports Var, Watt and Volt UnitOfMeasure");
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
        return algorithm == ControlAlgorithm.PFACTOR_KW_KVAR;
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