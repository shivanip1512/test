package com.cannontech.jobs.dao.impl;

public enum JobDisabledStatus {

	Y("Enabled"),
	N("Disabled"),
	D("Deleted");
	
	private String description;
	
	JobDisabledStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
}
