package com.cannontech.message.capcontrol.streamable;


public class SpecialArea extends StreamableCapObject {

    private boolean disableFlag;
    private boolean ovUvDisabledFlag;
    private boolean voltReductionFlag;
    private String paoDescription;
    private String paoCategory;
    private String paoClass;
    private int[] ccSubIds;
    private double powerFactorValue;
    private double estimatedPFValue;

    public int[] getCcSubIds() {
        return ccSubIds;
    }

    public void setCcSubIds(int[] ccSubIds) {
        this.ccSubIds = ccSubIds;
    }

    public void setPaoCategory(String string) {
        paoCategory = string;
    }

    public void setPaoDescription(String string) {
        paoDescription = string;
    }

    public void setDisableFlag(boolean b) {
        disableFlag = b;
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
        if (obj instanceof SpecialArea) {
            SpecialArea area = (SpecialArea) obj;
            return area.getCcId().equals(getCcId());
        }
        return false;
    }

    public boolean getOvUvDisabledFlag() {
        return ovUvDisabledFlag;
    }

    public void setOvUvDisabledFlag(boolean ovUvDisabledFlag) {
        this.ovUvDisabledFlag = ovUvDisabledFlag;
    }

    public Double getPowerFactorValue() {
        return powerFactorValue;
    }

    public void setPowerFactorValue(Double powerFactorValue) {
        this.powerFactorValue = powerFactorValue;
    }

    public Double getEstimatedPFValue() {
        return estimatedPFValue;
    }

    public void setEstimatedPFValue(Double estimatedPFValue) {
        this.estimatedPFValue = estimatedPFValue;
    }

    public boolean getVoltReductionFlag() {
        return voltReductionFlag;
    }

    public void setVoltReductionFlag(boolean voltReductionFlag) {
        this.voltReductionFlag = voltReductionFlag;
    }

}
