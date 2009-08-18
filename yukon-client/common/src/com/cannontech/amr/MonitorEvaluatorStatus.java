package com.cannontech.amr;

public enum MonitorEvaluatorStatus {

	ENABLED("Enabled"),
	DISABLED("Disabled"),
	;
	
	MonitorEvaluatorStatus(String description) {
		this.description = description;
	}
	
	private String description;
	
	public String getDescription() {
		return description;
	};
}
