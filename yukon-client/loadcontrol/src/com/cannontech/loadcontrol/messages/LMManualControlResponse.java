package com.cannontech.loadcontrol.messages;

import java.util.List;

/**
 * LMManualControlResponse is sent in response to a LMManualControlRequest
 * It contains information about the constraints that were violated, if any.
 * 
 * In the event that a control window or maximum control time constraint
 * is violated then the server will return the compromise it had to make
 * as the bestFitAction.
 */

public class LMManualControlResponse extends LMMessage 
{
	private int programID;
	// List<ConstraintViolation> - An object containing the constraint information for each constraint violated
	private List<ConstraintViolation> constraintViolations;
	// If a constraint is violated and the server is forced to
	// determine a "best fit" time period for a program to 
	// run, this field will contain info about the compromise made.
	private String bestFitAction;
	

/**
 * ScheduleCommand constructor comment.
 */
public LMManualControlResponse() {
	super();
}

	/**
	 * @return
	 */
	public List<ConstraintViolation> getConstraintViolations() {
		return constraintViolations;
	}

	/**
	 * @param set
	 */
	public void setConstraintViolations(List<ConstraintViolation> set) {
		constraintViolations = set;
	}

	/**
	 * @return
	 */
	public String getBestFitAction() {
		return bestFitAction;
	}

	/**
	 * @return
	 */
	public int getProgramID() {
		return programID;
	}

	/**
	 * @param string
	 */
	public void setBestFitAction(String string) {
		bestFitAction = string;
	}

	/**
	 * @param i
	 */
	public void setProgramID(int i) {
		programID = i;
	}

}
