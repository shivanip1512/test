package com.cannontech.messaging.message.capcontrol.streamable;

public class Area extends StreamableCapObject {

    private boolean ovUvDisabledFlag;
    private int[] stations = null;
    private double powerFactorValue;
    private double estimatedPFValue;
    private boolean voltReductionFlag;
    private boolean childVoltReductionFlag;

    public int[] getStations() {
        return stations;
    }

    public void setStations(int[] stations) {
        this.stations = stations;
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

    public boolean getChildVoltReductionFlag() {
        return childVoltReductionFlag;
    }

    public void setChildVoltReductionFlag(boolean childVoltReductionFlag) {
        this.childVoltReductionFlag = childVoltReductionFlag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Area) {
            Area area = (Area) obj;
            return area.getCcId() == getCcId();
        }
        return false;
    }

    public Area copy() {
        Area copy = new Area();
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
