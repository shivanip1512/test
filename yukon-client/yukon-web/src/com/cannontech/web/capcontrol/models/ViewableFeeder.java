package com.cannontech.web.capcontrol.models;

import java.util.List;

import com.cannontech.yukon.cbc.Feeder;

public class ViewableFeeder {

	private Feeder feeder = null;
	private String subBusName = null;
	private List<ViewableCapBank> capbanks = null;
	
	public Feeder getFeeder() {
		return feeder;
	}

	public void setFeeder(Feeder feeder) {
		this.feeder = feeder;
	}

	public String getSubBusName() {
		return subBusName;
	}

	public void setSubBusName(String subBusName) {
		this.subBusName = subBusName;
	}
	
	public void setCapBanks(List<ViewableCapBank> capbanks) {
	    this.capbanks = capbanks;
	}
	
	public List<ViewableCapBank> getCapBanks() {
        return capbanks;
    }
}
