package com.cannontech.messaging.message.capcontrol.streamable;

public class SubStation extends StreamableCapObject {

    private boolean ovuvDisableFlag;
    private int[] subBusIds = null;
    private double powerFactorValue;
    private double estimatedPFValue;
    private boolean specialAreaEnabled;
    private int specialAreaId;
    private boolean verificationFlag = false;
    private boolean voltReductionFlag = false;
    private boolean childVoltReductionFlag = false;
    private boolean recentlyControlledFlag = false;

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

    public void setVerificationFlag(boolean bool) {
        verificationFlag = bool;
    }

    public boolean getVoltReductionFlag() {
        return voltReductionFlag;
    }

    public void setVoltReductionFlag(boolean voltReductionFlag) {
        this.voltReductionFlag = voltReductionFlag;
    }

    public void setRecentlyControlledFlag(boolean value) {
        this.recentlyControlledFlag = value;
    }

    public boolean getRecentlyControlledFlag() {
        return this.recentlyControlledFlag;
    }

    public boolean getChildVoltReductionFlag() {
        return this.childVoltReductionFlag;
    }

    public void setChildVoltReductionFlag(boolean value) {
        this.childVoltReductionFlag = value;
    }
}
