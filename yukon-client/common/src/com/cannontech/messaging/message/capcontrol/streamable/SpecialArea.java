package com.cannontech.messaging.message.capcontrol.streamable;

public class SpecialArea extends StreamableCapObject {

    private boolean ovUvDisabledFlag;
    private boolean voltReductionFlag;

    // should only contain objects of type integer
    private int[] ccSubIds = null;
    private double powerFactorValue;
    private double estimatedPFValue;

    public int[] getCcSubIds() {
        return ccSubIds;
    }

    public void setCcSubIds(int[] ccSubIds) {
        this.ccSubIds = ccSubIds;
    }

    public boolean getOvUvDisabledFlag() {
        return ovUvDisabledFlag;
    }

    public void setOvUvDisabledFlag(boolean ovUvDisabledFlag) {
        this.ovUvDisabledFlag = ovUvDisabledFlag;
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

    public boolean getVoltReductionFlag() {
        return voltReductionFlag;
    }

    public void setVoltReductionFlag(boolean voltReductionFlag) {
        this.voltReductionFlag = voltReductionFlag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SpecialArea) {
            SpecialArea area = (SpecialArea) obj;
            return area.getCcId() == getCcId();
        }
        return false;
    }

    public SpecialArea copy() {
        SpecialArea copy = new SpecialArea();
        copy.setCcId(getCcId());
        copy.setCcCategory(getCcCategory());
        copy.setCcClass(getCcClass());
        copy.setCcName(getCcName());
        copy.setCcType(getCcType());
        copy.setCcDescription(getCcDescription());
        copy.setCcDisableFlag(getCcDisableFlag());
        return copy;
    }
}
