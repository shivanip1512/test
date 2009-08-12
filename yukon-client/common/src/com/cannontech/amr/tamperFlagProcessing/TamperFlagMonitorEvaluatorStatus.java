package com.cannontech.amr.tamperFlagProcessing;

public enum TamperFlagMonitorEvaluatorStatus {

	ENABLED("Enabled"),
	DISABLED("Disabled"),
	;
	
	TamperFlagMonitorEvaluatorStatus(String description) {
		this.description = description;
	}
	
	private String description;
	
	public String getDescription() {
		return description;
	};
}
