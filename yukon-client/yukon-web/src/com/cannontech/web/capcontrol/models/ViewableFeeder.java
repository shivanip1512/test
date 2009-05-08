package com.cannontech.web.capcontrol.models;

import com.cannontech.yukon.cbc.Feeder;

public class ViewableFeeder {

	private Feeder feeder = null;
	private String subBusName = null;
	
	public ViewableFeeder() {
		
	}

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
	
	
}
