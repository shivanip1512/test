package com.cannontech.multispeak.db;

public class MspLMInterfaceMapping {
    private int mspLMInterfaceMappingId;
    private String strategyName;
    private String substationName;
    private int paobjectId;
    
    public MspLMInterfaceMapping() { }

    public MspLMInterfaceMapping(int mspLMInterfaceMappingId,
			String strategyName, String substationName, int paobjectId) {
		super();
		this.mspLMInterfaceMappingId = mspLMInterfaceMappingId;
		this.strategyName = strategyName;
		this.substationName = substationName;
		this.paobjectId = paobjectId;
	}

    public int getMspLMInterfaceMappingId() {
		return mspLMInterfaceMappingId;
	}

	public void setMspLMInterfaceMappingId(int mspLMInterfaceMappingId) {
		this.mspLMInterfaceMappingId = mspLMInterfaceMappingId;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	public String getSubstationName() {
		return substationName;
	}

	public void setSubstationName(String substationName) {
		this.substationName = substationName;
	}

	public int getPaobjectId() {
		return paobjectId;
	}

	public void setPaobjectId(int paobjectId) {
		this.paobjectId = paobjectId;
	}

	@Override
    public String toString() {
        return strategyName + " - " + substationName;
    }
}
