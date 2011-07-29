package com.cannontech.jobs.dao.impl;

public enum JobDisabledStatus {

	Y("Disabled"),
	N("Enabled"),
	D("Deleted");
	
	private String description;
	
	JobDisabledStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
