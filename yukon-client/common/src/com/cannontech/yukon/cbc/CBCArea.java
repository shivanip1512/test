package com.cannontech.yukon.cbc;


public class CBCArea extends StreamableCapObject {

    private Boolean disableFlag;
    private Boolean ovUvDisabledFlag;
    private String paoDescription;
    private String paoType;
    private String paoName;
    private String paoCategory;
    private Integer paoID;
    private String paoClass;
    int[] stations = null;
    private Double powerFactorValue = null;
    private Double estimatedPFValue = null;
    private Boolean voltReductionFlag;
    
    public int[] getStations() {
		return stations;
	}

	public void setStations(int[] stations) {
		this.stations = stations;
	}

	public void setPaoID(Integer integer) {
        paoID = integer;
    }

    public void setPaoCategory(String string) {
        paoCategory = string;
    }

    public void setPaoName(String string) {
        paoName = string;
    }

    public void setPaoType(String string) {
        paoType = string;
    }

    public void setPaoDescription(String string) {
        paoDescription = string;
    }

    public void setDisableFlag(Boolean b) {
        disableFlag = b;
    }

    public Boolean getDisableFlag() {
        return disableFlag;
    }

    public String getPaoCategory() {
        return paoCategory;
    }

    public String getPaoDescription() {
        return paoDescription;
    }

    public Integer getPaoID() {
        return paoID;
    }

    public String getPaoName() {
        return paoName;
    }

    public String getPaoType() {
        return paoType;
    }

    public void setPaoClass(String string) {
        paoClass = string;
    }

    public String getPaoClass() {
        return paoClass;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CBCArea) {
            CBCArea area = (CBCArea) obj;
            return area.getPaoID().equals(getPaoID());
        }
        return false;
    }

    public CBCArea copy() {
        CBCArea copy = new CBCArea();
        copy.setPaoID(getPaoID());
        copy.setPaoCategory(getPaoCategory());
        copy.setPaoClass(getPaoClass());
        copy.setPaoName(getPaoName());
        copy.setPaoType(getPaoType());
        copy.setPaoDescription(getPaoDescription());
        copy.setDisableFlag(getDisableFlag());
        return copy;
    }

	public Boolean getOvUvDisabledFlag() {
		return ovUvDisabledFlag;
	}

	public void setOvUvDisabledFlag(Boolean ovUvDisabledFlag) {
		this.ovUvDisabledFlag = ovUvDisabledFlag;
	}
    
    /**
     * Returns the powerFactorValue.
     * @return Double
     */
    public Double getPowerFactorValue() {
        return powerFactorValue;
    }

    /**
     * Sets the powerFactorValue.
     * @param powerFactorValue The powerFactorValue to set
     */
    public void setPowerFactorValue(Double powerFactorValue) {
        this.powerFactorValue = powerFactorValue;
    }

    /**
     * Returns the estimatedPFValue.
     * @return Double
     */
    public Double getEstimatedPFValue() {
        return estimatedPFValue;
    }

    /**
     * Sets the estimatedPFValue.
     * @param estimatedPFValue The estimatedPFValue to set
     */
    public void setEstimatedPFValue(Double estimatedPFValue) {
        this.estimatedPFValue = estimatedPFValue;
    }

	public Boolean getVoltReductionFlag() {
		return voltReductionFlag;
	}

	public void setVoltReductionFlag(Boolean voltReductionFlag) {
		this.voltReductionFlag = voltReductionFlag;
	}

}
