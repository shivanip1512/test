package com.cannontech.yukon.cbc;

//import java.util.Arrays;
import java.util.Vector;

//import com.cannontech.database.data.point.PointUnits;

public class SubStation extends StreamableCapObject //implements PointQualityCheckable
{

	private Integer paoId;
	private String  paoCategory;
	private String  paoClass;
	private String  paoName;
	private String  paoType;
	private String  paoDescription;
	private Boolean disableFlag;
	private Boolean ovuvDisableFlag;
	int[] subBusIds = null;
    private Double powerFactorValue = null;
    private Double estimatedPFValue = null;
	
	public SubStation(){
		super();
	}
    
	public Boolean getDisableFlag() {
		return disableFlag;
	}

	public void setDisableFlag(Boolean disableFlag) {
		this.disableFlag = disableFlag;
	}

	public Boolean getOvuvDisableFlag() {
		return ovuvDisableFlag;
	}

	public void setOvuvDisableFlag(Boolean ovuvDisableFlag) {
		this.ovuvDisableFlag = ovuvDisableFlag;
	}

	public String getPaoCategory() {
		return paoCategory;
	}

	public void setPaoCategory(String paoCategory) {
		this.paoCategory = paoCategory;
	}

	public String getPaoClass() {
		return paoClass;
	}

	public void setPaoClass(String paoClass) {
		this.paoClass = paoClass;
	}

	public String getPaoDescription() {
		return paoDescription;
	}

	public void setPaoDescription(String paoDescription) {
		this.paoDescription = paoDescription;
	}

	public Integer getPaoId() {
		return paoId;
	}

	public void setPaoId(Integer paoId) {
		this.paoId = paoId;
	}

	public String getPaoName() {
		return paoName;
	}

	public void setPaoName(String paoName) {
		this.paoName = paoName;
	}

	public String getPaoType() {
		return paoType;
	}

	public void setPaoType(String paoType) {
		this.paoType = paoType;
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
    
}
