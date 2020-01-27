package com.cannontech.loadcontrol.data;

import com.cannontech.database.db.device.lm.GearControlMethod;

public class LMProgramDirectGear {

    public static final String SELECTION_LAST_CONTROLLED = "LastControlled";
    public static final String SELECTION_ALWAYS_FIRST_GROUP = "AlwaysFirstGroup";
    public static final String SELECTION_LEAST_CONTROL_TIME = "LeastControlTime";

    private Integer gearId;
    private String gearName;
    private Integer gearNumber;
    private GearControlMethod controlMethod;
    private Integer methodRate;
    private Integer methodPeriod;
    private Integer methodRateCount;
    private Integer cycleRefreshRate;
    private String methodStopType;
    private String changeCondition;
    private Integer changeDuration;
    private Integer changePriority;
    private Integer changeTriggerNumber;
    private Double changeTriggerOffset;
    private Integer percentReduction;
    private String groupSelectionMethod;
    private String methodOptionType;
    private Integer methodOptionMax;
    private Integer rampInInterval;
    private Integer rampInPercent;
    private Integer rampOutInterval;
    private Integer rampOutPercent;
    private Double kwReduction;
    private Integer programId;
    private String frontRampOption;
    private String backRampOption;
    private Integer stopCommandRepeat;

    public Integer getGearId() {
        return gearId;
    }

    public void setGearId(Integer gearId) {
        this.gearId = gearId;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public String getChangeCondition() {
        return changeCondition;
    }

    public Integer getChangeDuration() {
        return changeDuration;
    }

    public Integer getChangePriority() {
        return changePriority;
    }

    public Integer getChangeTriggerNumber() {
        return changeTriggerNumber;
    }

    public Double getChangeTriggerOffset() {
        return changeTriggerOffset;
    }

    public GearControlMethod getControlMethod() {
        return controlMethod;
    }

    public boolean isTargetCycle() {
        return GearControlMethod.TargetCycle == controlMethod;
    }


    public Integer getCycleRefreshRate() {
        return cycleRefreshRate;
    }

    public String getGearName() {
        return gearName;
    }

    public Integer getGearNumber() {
        return gearNumber;
    }

    public String getGroupSelectionMethod() {
        return groupSelectionMethod;
    }

    public Integer getMethodOptionMax() {
        return methodOptionMax;
    }

    public String getMethodOptionType() {
        return methodOptionType;
    }

    public Integer getMethodPeriod() {
        return methodPeriod;
    }

    public Integer getMethodRate() {
        return methodRate;
    }

    public Integer getMethodRateCount() {
        return methodRateCount;
    }

    public String getMethodStopType() {
        return methodStopType;
    }

    public Integer getPercentReduction() {
        return percentReduction;
    }

    public void setChangeCondition(String newChangeCondition) {
        changeCondition = newChangeCondition;
    }

    public void setChangeDuration(Integer newChangeDuration) {
        changeDuration = newChangeDuration;
    }

    public void setChangePriority(Integer newChangePriority) {
        changePriority = newChangePriority;
    }

    public void setChangeTriggerNumber(Integer newChangeTriggerNumber) {
        changeTriggerNumber = newChangeTriggerNumber;
    }

    public void setChangeTriggerOffset(Double newChangeTriggerOffset) {
        changeTriggerOffset = newChangeTriggerOffset;
    }

    public void setControlMethod(GearControlMethod newControlMethod) {
        controlMethod = newControlMethod;
    }

    public void setCycleRefreshRate(Integer newCycleRefreshRate) {
        cycleRefreshRate = newCycleRefreshRate;
    }

    public void setGearName(String newGearName) {
        gearName = newGearName;
    }

    public void setGearNumber(Integer newGearNumber) {
        gearNumber = newGearNumber;
    }

    public void setGroupSelectionMethod(String newGroupSelectionMethod) {
        groupSelectionMethod = newGroupSelectionMethod;
    }

    public void setMethodOptionMax(Integer newMethodOptionMax) {
        methodOptionMax = newMethodOptionMax;
    }

    public void setMethodOptionType(String newMethodOptionType) {
        methodOptionType = newMethodOptionType;
    }

    public void setMethodPeriod(Integer newMethodPeriod) {
        methodPeriod = newMethodPeriod;
    }

    public void setMethodRate(Integer newMethodRate) {
        methodRate = newMethodRate;
    }

    public void setMethodRateCount(Integer newMethodRateCount) {
        methodRateCount = newMethodRateCount;
    }

    public void setMethodStopType(String newMethodStopType) {
        methodStopType = newMethodStopType;
    }

    public void setPercentReduction(Integer newPercentReduction) {
        percentReduction = newPercentReduction;
    }


    public Integer getRampInInterval() {
        return rampInInterval;
    }

    public Integer getRampInPercent() {
        return rampInPercent;
    }

    public Integer getRampOutInterval() {
        return rampOutInterval;
    }

    public Integer getRampOutPercent() {
        return rampOutPercent;
    }

    public void setRampInInterval(Integer integer) {
        rampInInterval = integer;
    }

    public void setRampInPercent(Integer integer) {
        rampInPercent = integer;
    }

    public void setRampOutInterval(Integer integer) {
        rampOutInterval = integer;
    }

    public void setRampOutPercent(Integer integer) {
        rampOutPercent = integer;
    }

    public Double getKwReduction() {
        return kwReduction;
    }

    public void setKwReduction(Double kwReduction) {
        this.kwReduction = kwReduction;
    }

    
    public String getFrontRampOption() {
        return frontRampOption;
    }

    public void setFrontRampOption(String frontRampOption) {
        this.frontRampOption = frontRampOption;
    }

    public String getBackRampOption() {
        return backRampOption;
    }

    public void setBackRampOption(String backRampOption) {
        this.backRampOption = backRampOption;
    }

    public Integer getStopCommandRepeat() {
        return stopCommandRepeat;
    }

    public void setStopCommandRepeat(Integer stopCommandRepeat) {
        this.stopCommandRepeat = stopCommandRepeat;
    }
    

    @Override
    public String toString() {
        return getGearName();
    }

}
