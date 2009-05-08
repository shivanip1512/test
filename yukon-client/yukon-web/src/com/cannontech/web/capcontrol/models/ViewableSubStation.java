package com.cannontech.web.capcontrol.models;

public class ViewableSubStation {

	private String subStationName = null;
	private int feederCount;
	private int capBankCount;
	
	public ViewableSubStation() {
		
	}

	public String getSubStationName() {
		return subStationName;
	}

	public void setSubStationName(String subStationName) {
		this.subStationName = subStationName;
	}

	public int getFeederCount() {
		return feederCount;
	}

	public void setFeederCount(int feederCount) {
		this.feederCount = feederCount;
	}

	public int getCapBankCount() {
		return capBankCount;
	}

	public void setCapBankCount(int capBankCount) {
		this.capBankCount = capBankCount;
	}
	
	
}
