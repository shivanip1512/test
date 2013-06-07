package com.cannontech.messaging.message.loadcontrol.data;

import com.cannontech.database.db.device.lm.GearControlMethod;
public class ProgramDirectGear {

    public static String SELECTION_LAST_CONTROLLED = "LastControlled";
    public static String SELECTION_ALWAYS_FIRST_GROUP = "AlwaysFirstGroup";
    public static String SELECTION_LEAST_CONTROL_TIME = "LeastControlTime";

    private int yukonId;
    private String gearName = null;
    private int gearNumber;
    private GearControlMethod controlMethod = null;
    private int methodRate;
    private int methodPeriod;
    private int methodRateCount;
    private int cycleRefreshRate;
    private String methodStopType = null;
    private String changeCondition = null;
    private int changeDuration;
    private int changePriority;
    private int changeTriggerNumber;
    private double changeTriggerOffset;
    private int percentReduction;
    private String groupSelectionMethod = null;
    private String methodOptionType = null;
    private int methodOptionMax;
    private int rampInInterval;
    private int rampInPercent;
    private int rampOutInterval;
    private int rampOutPercent;
    private double kwReduction;

    // String constants that represent the various
    // states a strategy can be in
    /*
     * public static final String STATE_ENABLED = "Enabled"; public static final String STATE_DISABLED = "Disabled";
     * public static final String STATE_CLOSED_PENDING = "Open Pending"; public static final String STATE_OPEN_PENDING =
     * "Closed Pending"; public static final String ERROR = "**ERROR**"; public static final int RECENTLY_CONTROLLED =
     * 1; public static final int NO_CAPBANK_CONTROLLED = -1; // peakSetPointInUse possible values public static final
     * int PEAK_SETPOINT = 0; public static final int OFF_PEAK_SETPOINT = 1;
     */
    public java.lang.String getChangeCondition() {
        return changeCondition;
    }

    public int getChangeDuration() {
        return changeDuration;
    }

    public int getChangePriority() {
        return changePriority;
    }

    public int getChangeTriggerNumber() {
        return changeTriggerNumber;
    }

    public double getChangeTriggerOffset() {
        return changeTriggerOffset;
    }

    public GearControlMethod getControlMethod() {
        return controlMethod;
    }

    public boolean isTargetCycle() {
        return GearControlMethod.TargetCycle == controlMethod;
    }

    public int getCycleRefreshRate() {
        return cycleRefreshRate;
    }

    public java.lang.String getGearName() {
        return gearName;
    }

    public int getGearNumber() {
        return gearNumber;
    }

    public java.lang.String getGroupSelectionMethod() {
        return groupSelectionMethod;
    }

    public int getMethodOptionMax() {
        return methodOptionMax;
    }

    public java.lang.String getMethodOptionType() {
        return methodOptionType;
    }

    public int getMethodPeriod() {
        return methodPeriod;
    }

    public int getMethodRate() {
        return methodRate;
    }

    public int getMethodRateCount() {
        return methodRateCount;
    }

    public java.lang.String getMethodStopType() {
        return methodStopType;
    }

    public int getPercentReduction() {
        return percentReduction;
    }

    public int getYukonId() {
        return yukonId;
    }

    public void setChangeCondition(java.lang.String newChangeCondition) {
        changeCondition = newChangeCondition;
    }

    public void setChangeDuration(int newChangeDuration) {
        changeDuration = newChangeDuration;
    }

    public void setChangePriority(int newChangePriority) {
        changePriority = newChangePriority;
    }

    public void setChangeTriggerNumber(int newChangeTriggerNumber) {
        changeTriggerNumber = newChangeTriggerNumber;
    }

    public void setChangeTriggerOffset(double newChangeTriggerOffset) {
        changeTriggerOffset = newChangeTriggerOffset;
    }

    public void setControlMethod(GearControlMethod newControlMethod) {
        controlMethod = newControlMethod;
    }

    public void setCycleRefreshRate(int newCycleRefreshRate) {
        cycleRefreshRate = newCycleRefreshRate;
    }

    public void setGearName(java.lang.String newGearName) {
        gearName = newGearName;
    }

    public void setGearNumber(int newGearNumber) {
        gearNumber = newGearNumber;
    }

    public void setGroupSelectionMethod(java.lang.String newGroupSelectionMethod) {
        groupSelectionMethod = newGroupSelectionMethod;
    }

    public void setMethodOptionMax(int newMethodOptionMax) {
        methodOptionMax = newMethodOptionMax;
    }

    public void setMethodOptionType(java.lang.String newMethodOptionType) {
        methodOptionType = newMethodOptionType;
    }

    public void setMethodPeriod(int newMethodPeriod) {
        methodPeriod = newMethodPeriod;
    }

    public void setMethodRate(int newMethodRate) {
        methodRate = newMethodRate;
    }

    public void setMethodRateCount(int newMethodRateCount) {
        methodRateCount = newMethodRateCount;
    }

    public void setMethodStopType(java.lang.String newMethodStopType) {
        methodStopType = newMethodStopType;
    }

    public void setPercentReduction(int newPercentReduction) {
        percentReduction = newPercentReduction;
    }

    public void setYukonId(int newYukonId) {
        yukonId = newYukonId;
    }

    public String toString() {
        return getGearName();
    }

    public int getRampInInterval() {
        return rampInInterval;
    }

    public int getRampInPercent() {
        return rampInPercent;
    }

    public int getRampOutInterval() {
        return rampOutInterval;
    }

    public int getRampOutPercent() {
        return rampOutPercent;
    }

    public void setRampInInterval(int integer) {
        rampInInterval = integer;
    }

    public void setRampInPercent(int integer) {
        rampInPercent = integer;
    }

    public void setRampOutInterval(int integer) {
        rampOutInterval = integer;
    }

    public void setRampOutPercent(int integer) {
        rampOutPercent = integer;
    }

    public double getKwReduction() {
        return kwReduction;
    }

    public void setKwReduction(double kwReduction) {
        this.kwReduction = kwReduction;
    }
}
