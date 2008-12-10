package com.cannontech.loadcontrol.service.data;

public enum OverrideStatus {

	ACTIVE("Active"),
	CANCELLED("Canceled"),
	SCHEDULED("Scheduled");
	
	private String description;
	
	OverrideStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
