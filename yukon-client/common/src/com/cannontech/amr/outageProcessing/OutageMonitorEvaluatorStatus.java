package com.cannontech.amr.outageProcessing;

public enum OutageMonitorEvaluatorStatus {

	ENABLED("Enabled"),
	DISABLED("Disabled"),
	;
	
	OutageMonitorEvaluatorStatus(String description) {
		this.description = description;
	}
	
	private String description;
	
	public String getDescription() {
		return description;
	};
}
