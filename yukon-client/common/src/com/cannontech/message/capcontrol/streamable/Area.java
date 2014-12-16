package com.cannontech.message.capcontrol.streamable;

public class Area extends StreamableCapObject {

    private boolean disableFlag;
    private boolean ovUvDisabledFlag;
    private String paoDescription;
    private String paoCategory;
    private String paoClass;
    int[] stations;
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

    public void setPaoCategory(String string) {
        paoCategory = string;
    }

    public void setPaoDescription(String string) {
        paoDescription = string;
    }

    public void setDisableFlag(boolean disableFlag) {
        this.disableFlag = disableFlag;
    }

    public boolean getDisableFlag() {
        return disableFlag;
    }

    public String getPaoCategory() {
        return paoCategory;
    }

    public String getPaoDescription() {
        return paoDescription;
    }

    public void setPaoClass(String string) {
        paoClass = string;
    }

    public String getPaoClass() {
        return paoClass;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Area) {
            Area area = (Area) obj;
            return area.getCcId() == getCcId();
        }
        return false;
    }

    public boolean getOvUvDisabledFlag() {
        return ovUvDisabledFlag;
        }

    public void setOvUvDisabledFlag(boolean ovUvDisabledFlag) {
        this.ovUvDisabledFlag = ovUvDisabledFlag;
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

}