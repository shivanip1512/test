package com.cannontech.dr.program.service;

import java.util.List;

public class ConstraintViolations {
    
	private List<ConstraintContainer> violations;

	public ConstraintViolations(List<ConstraintContainer> violations) {
		this.violations = violations;
	}

	public List<ConstraintContainer> getViolationContainers() {
		return violations;
	}

	public void setViolationContainers(List<ConstraintContainer> violations) {
		this.violations = violations;
	}

}
