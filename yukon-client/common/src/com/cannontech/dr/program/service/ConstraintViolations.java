package com.cannontech.dr.program.service;

import java.util.List;

public class ConstraintViolations {
    
	private List<ConstraintContainer> constraintContainers;

	public ConstraintViolations(List<ConstraintContainer> constraintContainers) {
		this.constraintContainers = constraintContainers;
	}

	public List<ConstraintContainer> getConstraintContainers() {
		return constraintContainers;
	}

	public void setConstraintContainers(List<ConstraintContainer> constraintContainers) {
		this.constraintContainers = constraintContainers;
	}
	
	public boolean isViolated() {
		return (constraintContainers.size() > 0);
	}

}
