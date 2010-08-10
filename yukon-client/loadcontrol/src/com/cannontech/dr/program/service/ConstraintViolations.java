package com.cannontech.dr.program.service;

import java.util.List;

public class ConstraintViolations {
    
	private String serverResponse;
	private List<ConstraintContainer> violations;

	public ConstraintViolations(List<ConstraintContainer> violations) {
		this.violations = violations;
	}
	
	public ConstraintViolations(String serverResponse) {
		this.serverResponse = serverResponse;
	}

	public List<ConstraintContainer> getViolations() {
		return violations;
	}

	public void setViolations(List<ConstraintContainer> violations) {
		this.violations = violations;
	}
	
	public void setServerResponse(String serverResponse) {
		this.serverResponse = serverResponse;
	}
	
	public String getServerResponse() {
		return serverResponse;
	}
}
