package com.cannontech.messaging.message.loadcontrol;

import java.util.List;

import com.cannontech.messaging.message.loadcontrol.data.ConstraintViolation;


/**
 * LMManualControlResponse is sent in response to a LMManualControlRequest
 * It contains information about the constraints that were violated, if any.
 * 
 * In the event that a control window or maximum control time constraint
 * is violated then the server will return the compromise it had to make
 * as the bestFitAction.
 */

public class ManualControlResponseMessage extends LmMessage 
{
	private int programId;
	// List<ConstraintViolation> - An object containing the constraint information for each constraint violated
	private List<ConstraintViolation> constraintViolations;
	// If a constraint is violated and the server is forced to
	// determine a "best fit" time period for a program to 
	// run, this field will contain info about the compromise made.
	private String bestFitAction;
	
	public List<ConstraintViolation> getConstraintViolations() {
		return constraintViolations;
	}

	public void setConstraintViolations(List<ConstraintViolation> set) {
		constraintViolations = set;
	}

	public String getBestFitAction() {
		return bestFitAction;
	}

	public int getProgramId() {
		return programId;
	}

	public void setBestFitAction(String string) {
		bestFitAction = string;
	}

	public void setProgramId(int i) {
		programId = i;
	}
}
