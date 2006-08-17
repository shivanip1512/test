package com.cannontech.yukon.cbc;

import com.cannontech.common.util.CtiUtilities;

public class SubSnapshotParams {
private int busID = CtiUtilities.NONE_ZERO_ID;
private String algorithm = "(none)";
private String controlMethod = "(none)";
	
	public SubSnapshotParams() {
		super();

	}

	public int getBusID() {
		return busID;
	}

	public void setBusID(int busID) {
		this.busID = busID;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String strategyType) {
		this.algorithm = strategyType;
	}

	public String getControlMethod() {
		return controlMethod;
	}

	public void setControlMethod(String controlMethod) {
		this.controlMethod = controlMethod;
	}



}
