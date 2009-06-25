package com.cannontech.web.capcontrol.models;

import java.util.List;

import com.cannontech.yukon.cbc.SubStation;


public class ViewableSubStation {

    private SubStation substation = null;
    private String subStationName = null;
	private int feederCount;
	private int capBankCount;
	private List<ViewableSubBus> subBuses = null;
	
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
	
    public List<ViewableSubBus> getSubBuses() {
        return subBuses;
    }

    public void setSubBuses(List<ViewableSubBus> subBuses) {
        this.subBuses = subBuses;
    }
    
    public SubStation getSubstation() {
        return substation;
    }

    public void setSubstation(SubStation substation) {
        this.substation = substation;
    }
}
