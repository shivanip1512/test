package com.cannontech.yukon.cbc;


public class SubStation extends StreamableCapObject //implements PointQualityCheckable
{


	private Boolean ovuvDisableFlag;
	int[] subBusIds = null;
    private Double powerFactorValue = null;
    private Double estimatedPFValue = null;
    private Boolean specialAreaEnabled;
    private Integer specialAreaId;
	private Boolean verificationFlag = false;
    
	public SubStation(){
		super();
	}


	public Boolean getOvuvDisableFlag() {
		return ovuvDisableFlag;
	}

	public void setOvuvDisableFlag(Boolean ovuvDisableFlag) {
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
    
    public Boolean getSpecialAreaEnabled() {
        return specialAreaEnabled;
    }

    public void setSpecialAreaEnabled(Boolean specialAreaEnabled) {
        this.specialAreaEnabled = specialAreaEnabled;
    }
    
    public Integer getSpecialAreaId() {
        return specialAreaId;
    }
    
    public void setSpecialAreaId(Integer specialAreaId) {
        this.specialAreaId = specialAreaId;
    }
    
    public Boolean getVerificationFlag() {
        return verificationFlag;
    }

    public void setVerificationFlag(Boolean bool) {
        verificationFlag = bool;
    }
}
