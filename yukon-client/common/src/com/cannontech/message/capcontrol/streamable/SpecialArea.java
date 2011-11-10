package com.cannontech.message.capcontrol.streamable;


public class SpecialArea extends StreamableCapObject {

    private Boolean disableFlag;
    private Boolean ovUvDisabledFlag;
    private Boolean voltReductionFlag;
    private String paoDescription;
    private String paoType;
    private String paoName;
    private String paoCategory;
    private Integer paoId;
    private String paoClass;
	//should only contain objects of type integer
	private int[] ccSubIds = null;
    private Double powerFactorValue = null;
    private Double estimatedPFValue = null;
    
    public int[] getCcSubIds() {
		return ccSubIds;
	}

	public void setCcSubIds(int[] ccSubIds) {
		this.ccSubIds = ccSubIds;
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
        if (obj instanceof SpecialArea) {
            SpecialArea area = (SpecialArea) obj;
            return area.getPaoId().equals(getPaoId());
        }
        return false;
    }

    public SpecialArea copy() {
        SpecialArea copy = new SpecialArea();
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

}

