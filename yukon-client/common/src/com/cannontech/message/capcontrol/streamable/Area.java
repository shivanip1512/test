package com.cannontech.message.capcontrol.streamable;

public class Area extends StreamableCapObject {

    private Boolean disableFlag;
    private Boolean ovUvDisabledFlag;
    private String paoDescription;
    private String paoType;
    private String paoName;
    private String paoCategory;
    private Integer paoId;
    private String paoClass;
    int[] stations = null;
    private Double powerFactorValue = null;
    private Double estimatedPFValue = null;
    private Boolean voltReductionFlag;
    private Boolean childVoltReductionFlag;
    
    public int[] getStations() {
		return stations;
	}

	public void setStations(int[] stations) {
		this.stations = stations;
	}

	public void setPaoId(Integer integer) {
        paoId = integer;
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

    public Integer getPaoId() {
        return paoId;
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
        if (obj instanceof Area) {
            Area area = (Area) obj;
            return area.getPaoId().equals(getPaoId());
        }
        return false;
    }

    public Area copy() {
        Area copy = new Area();
        copy.setPaoId(getPaoId());
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
	
	public Boolean getChildVoltReductionFlag() {
        return childVoltReductionFlag;
    }

    public void setChildVoltReductionFlag(Boolean childVoltReductionFlag) {
        this.childVoltReductionFlag = childVoltReductionFlag;
    }

}
