package com.cannontech.message.capcontrol.streamable;

public class SubStation extends StreamableCapObject {

    private boolean ovuvDisableFlag;
    int[] subBusIds;
    private double powerFactorValue;
    private double estimatedPFValue;
    private boolean specialAreaEnabled;
    private int specialAreaId;
    private boolean verificationFlag;
    private boolean voltReductionFlag;
    private boolean childVoltReductionFlag;
    private boolean recentlyControlledFlag;

    public boolean getOvuvDisableFlag() {
        return ovuvDisableFlag;
    }

    public void setOvuvDisableFlag(boolean ovuvDisableFlag) {
        this.ovuvDisableFlag = ovuvDisableFlag;
    }

    public int[] getSubBusIds() {
        return subBusIds;
    }

    public void setSubBusIds(int[] subBusIds) {
        this.subBusIds = subBusIds;
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

    public boolean getSpecialAreaEnabled() {
        return specialAreaEnabled;
    }

    public void setSpecialAreaEnabled(boolean specialAreaEnabled) {
        this.specialAreaEnabled = specialAreaEnabled;
    }

    public int getSpecialAreaId() {
        return specialAreaId;
    }

    public void setSpecialAreaId(int specialAreaId) {
        this.specialAreaId = specialAreaId;
    }

    public boolean getVerificationFlag() {
        return verificationFlag;
    }

    public void setVerificationFlag(boolean verificationFlag) {
        this.verificationFlag = verificationFlag;
    }

    public boolean getVoltReductionFlag() {
        return voltReductionFlag;
    }

    public void setVoltReductionFlag(boolean voltReductionFlag) {
        this.voltReductionFlag = voltReductionFlag;
    }

    public void setRecentlyControlledFlag(boolean recentlyControlledFlag) {
        this.recentlyControlledFlag = recentlyControlledFlag;
    }

    public boolean getRecentlyControlledFlag() {
        return recentlyControlledFlag;
    }

    public boolean getChildVoltReductionFlag() {
        return childVoltReductionFlag;
    }

    public void setChildVoltReductionFlag(boolean value) {
        childVoltReductionFlag = value;
    }
}