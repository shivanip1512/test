package com.cannontech.message.capcontrol.streamable;



public class SubStation extends StreamableCapObject {

	private Boolean ovuvDisableFlag;
	int[] subBusIds = null;
    private Double powerFactorValue = null;
    private Double estimatedPFValue = null;
    private Boolean specialAreaEnabled;
    private Integer specialAreaId;
	private Boolean verificationFlag = false;
	private Boolean voltReductionFlag = false;
	private Boolean childVoltReductionFlag = false;
	private Boolean recentlyControlledFlag = false;
    
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

	public Boolean getVoltReductionFlag() {
		return voltReductionFlag;
	}

	public void setVoltReductionFlag(Boolean voltReductionFlag) {
		this.voltReductionFlag = voltReductionFlag;
	}
	
	public void setRecentlyControlledFlag(Boolean value) {
	    this.recentlyControlledFlag = value;
	}
	
	public Boolean getRecentlyControlledFlag() {
	    return this.recentlyControlledFlag;
	}
	
	public Boolean getChildVoltReductionFlag() {
	    return this.childVoltReductionFlag;
	}
	
	public void setChildVoltReductionFlag(Boolean value) {
	    this.childVoltReductionFlag = value;
	}
}
