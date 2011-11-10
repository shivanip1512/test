package com.cannontech.web.capcontrol.models;

import java.util.List;

import com.cannontech.message.capcontrol.streamable.SubStation;


public class ViewableSubStation {

    private SubStation substation = null;
    private String name = null;
	private int feederCount;
	private int capBankCount;
	private List<ViewableSubBus> subBuses = null;
	
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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